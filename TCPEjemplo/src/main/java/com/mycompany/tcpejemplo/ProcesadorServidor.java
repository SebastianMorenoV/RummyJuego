package com.mycompany.tcpejemplo;

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
    private final Map<String, ClienteInfo> directorioClientes = new ConcurrentHashMap<>();

    // Correcto: Depende de la interfaz, no de una clase concreta. Mantiene el desacoplamiento.
    private final iDespachador despachador;

    public ProcesadorServidor(iDespachador despachador) {
        if (despachador == null) {
            throw new IllegalArgumentException("El despachador no puede ser nulo");
        }
        this.despachador = despachador;
    }

    @Override
    public String procesar(String ipCliente, String mensaje) {
        String[] partes = mensaje.split(":", 3);
        if (partes.length < 2) {
            return "ERROR: Mensaje malformado";
        }

        String idCliente = partes[0];
        String comando = partes[1];
        String payload = (partes.length > 2) ? partes[2] : "";

        // Lógica para registrar un nuevo cliente en el directorio.
        if (comando.equals("REGISTRAR")) {
            try {
                int puertoCliente = Integer.parseInt(payload);
                directorioClientes.put(idCliente, new ClienteInfo(ipCliente, puertoCliente));
                System.out.println("[BlackBoard] Cliente " + idCliente + " registrado en **" + ipCliente + ":" + puertoCliente + "**");
                return "REGISTRADO_OK";
            } catch (NumberFormatException e) {
                return "ERROR: Puerto inválido";
            }
        }

        // Lógica para reenviar (broadcast) un movimiento a los demás jugadores.
        if (comando.equals("MOVER")) {
            System.out.println("[BlackBoard] " + idCliente + " (" + ipCliente + ") movió datos: " + payload);

            String msgUpdate = "MOVIMIENTO_RECIBIDO:" + idCliente + ":" + payload;

            // Itera sobre el directorio para notificar a todos menos al remitente.
            for (Map.Entry<String, ClienteInfo> entry : directorioClientes.entrySet()) {
                if (entry.getKey().equals(idCliente)) {
                    continue; // No notificar a sí mismo
                }

                String idDestino = entry.getKey();
                ClienteInfo infoDestino = entry.getValue();

                new Thread(() -> {
                    try {
                        System.out.println("[BlackBoard] Reenviando a " + idDestino + " en " + infoDestino.getHost() + ":" + infoDestino.getPuerto());

                        // LÍNEA INCORRECTA (la que tienes ahora y causa el error):
                        // this.despachador.enviar(msgUpdate);
                        // LÍNEA CORRECTA (la que debes poner):
                        this.despachador.enviar(infoDestino.getHost(), infoDestino.getPuerto(), msgUpdate);

                    } catch (IOException e) {
                        System.err.println("[BlackBoard] Error al notificar a " + idDestino + ": " + e.getMessage());
                    }
                }).start();
            }
            return "MOVIMIENTO_RECIBIDO_OK"; // Respuesta al cliente original
        }

        return "COMANDO_DESCONOCIDO";
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
