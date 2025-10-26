package procesadores;

import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iProcesador;
import contratos.iResultadoComando;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lógica del Servidor. Su diseño es robusto y cumple su función. Sabe qué hacer
 * con "REGISTRAR" y "MOVER". Al recibir "MOVER", reenvía el payload intacto a
 * los demás clientes.
 */
public class ProcesadorServidor implements iProcesador {

    // Correcto: El servidor necesita un directorio para saber a quién enviar mensajes.
    private final Map<String, ClienteInfo> directorioRed;

    // Correcto: Depende de la interfaz, no de una clase concreta. Mantiene el desacoplamiento.
    private final iControladorBlackboard controladorBlackboard;
    private final iDespachador despachador;

    public ProcesadorServidor(iControladorBlackboard controlador, iDespachador despachador) {
        this.controladorBlackboard = controlador;
        this.despachador = despachador;
        this.directorioRed = new ConcurrentHashMap<>();
    }

    @Override
    public String procesar(String ipCliente, String mensaje) {
        String[] partes = mensaje.split(":", 3);
        String idCliente = partes[0];
        String comando = partes[1];
        String payload = (partes.length > 2) ? partes[2] : "";

        // --- Lógica de Red ---
        if (comando.equals("REGISTRAR")) {
            // El payload de REGISTRAR debe ser el puerto
            try {
                int puertoCliente = Integer.parseInt(payload);
                directorioRed.put(idCliente, new ClienteInfo(ipCliente, puertoCliente));
                System.out.println("[Procesador] " + idCliente + " conectado desde " + ipCliente + ":" + puertoCliente);
            } catch (Exception e) {
                return "ERROR: Puerto inválido";
            }
            // OJO: El payload que le pasamos al blackboard aquí podría ser diferente
            // (ej. la mano), pero por ahora pasamos el payload crudo.
        }

        // --- Lógica de Componente ---
        // 1. Traduce la llamada de red a una llamada al componente
        // Aquí pasamos un payload "vacío" para REGISTRAR, asumiendo que 
        // el payload original era solo el puerto.
        String payloadParaBlackboard = comando.equals("REGISTRAR") ? "" : payload;

        iResultadoComando resultado = controladorBlackboard.procesarComando(idCliente, comando, payloadParaBlackboard);

        // --- Lógica de Red (Salida) ---
        // 2. Ejecuta las instrucciones de "broadcast" del resultado
        for (String msgBroadcast : resultado.getMensajesBroadcast()) {
            enviarBroadcast(idCliente, msgBroadcast);
        }

        // 3. Devuelve la respuesta al cliente original
        return resultado.getRespuestaAlRemitente();
    }

    /**
     * Método de ayuda para enviar un mensaje a todos menos al remitente.
     */
    private void enviarBroadcast(String idRemitente, String mensaje) {
        for (Map.Entry<String, ClienteInfo> entry : directorioRed.entrySet()) {
            if (entry.getKey().equals(idRemitente)) {
                continue;
            }

            ClienteInfo infoDestino = entry.getValue();
            try {
                despachador.enviar(infoDestino.getHost(), infoDestino.getPuerto(), mensaje);
            } catch (IOException e) {
                System.err.println("[Procesador] Error en broadcast: " + e.getMessage());
            }
        }
    }

    // Clase interna para almacenar la información de conexión de cada cliente.
    private static class ClienteInfo {

        private final String host;
        private final int puerto;

        public ClienteInfo(String host, int puerto) {
            this.host = host;
            this.puerto = puerto;
        }

        public String getHost() {
            return host;
        }

        public int getPuerto() {
            return puerto;
        }
    }
}
