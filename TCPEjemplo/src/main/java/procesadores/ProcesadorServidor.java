package procesadores;

import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iPizarraJuego;
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

    private final iPizarraJuego pizarra;
    private final iDespachador despachador;
    private final iDirectorio directorio; // ¡Añade el directorio!

    // Modifica el constructor
    public ProcesadorServidor(iPizarraJuego pizarra, iDespachador despachador, iDirectorio directorio) {
        this.pizarra = pizarra;
        this.despachador = despachador;
        this.directorio = directorio; // Guárdalo
    }

    @Override
    public String procesar(String ipCliente, String mensaje) {
        String[] partes = mensaje.split(":", 3);
        if (partes.length < 2) {
            return "ERROR: Formato inválido.";
        }

        String idCliente = partes[0];
        String comando = partes[1];
        String payload = (partes.length > 2) ? partes[2] : "";
        System.out.println("COMANDO DESDE PROCESADOR SERVIDOR: " + comando);

        // --- INICIO DE LÓGICA DE RED (LA ÚNICA PERMITIDA AQUÍ) ---
        if (comando.equals("REGISTRAR")) {
            try {
                int puertoCliente = Integer.parseInt(payload);
                this.directorio.addJugador(idCliente, ipCliente, puertoCliente);

                // Llama a la pizarra con un payload vacío para el registro del juego
                pizarra.procesarComando(idCliente, comando, "");

                return "REGISTRO_OK";
            } catch (Exception e) {
                return "ERROR: Puerto inválido";
            }
        }
        // --- FIN DE LÓGICA DE RED ---

        // ¡PROCESADOR "TONTO"!
        // Para CUALQUIER OTRO comando, simplemente pasa el comando y el payload CRUDO
        // a la pizarra. La pizarra decidirá qué hacer.
        boolean resultado = pizarra.procesarComando(idCliente, comando, payload);

        if (!resultado) {
            return new String("NO_TE_JALASTE"); // O JUGADA_INVALIDA
        }

        // ¡LA LÓGICA DE BROADCAST YA NO VIVE AQUÍ!
        // El ControladorBlackboard (Paso 3) se encargará de esto.
        return new String("MOVIMIENTO_RECIBIDO_OK");
    }
    // ... (La clase interna ClienteInfo ya no es necesaria aquí) ...
}
