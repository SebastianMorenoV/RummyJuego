// En: RummyJuego/BlackBoardServer/src/main/java/directorio/Directorio.java

package directorio;

import contratos.iDespachador; // ¡Importante!
import contratos.iDirectorio;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Directorio implements iDirectorio {

    // Necesita al despachador para enviar mensajes
    private final iDespachador despachador;
    
    // Un mapa para guardar la info de conexión por ID de jugador
    private final Map<String, ClienteInfo> directorioJugadores;

    // ¡Inyéctale el despachador!
    public Directorio(iDespachador despachador) {
        this.despachador = despachador;
        // Usa un mapa seguro para concurrencia
        this.directorioJugadores = new ConcurrentHashMap<>();
    }

    // Un método para registrar al jugador con toda la info necesaria
    public void addJugador(String idJugador, String ip, int puerto) {
        directorioJugadores.put(idJugador, new ClienteInfo(ip, puerto));
        System.out.println("[Directorio] Jugador " + idJugador + " registrado en " + ip + ":" + puerto);
    }

    @Override
    public void removeJugador(String idJugador) {
        directorioJugadores.remove(idJugador);
    }

    // Tu lógica para enviar a todos menos a uno
    @Override
    public void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje) {
        for (Map.Entry<String, ClienteInfo> entry : directorioJugadores.entrySet()) {
            
            // Si el ID del destinatario NO es el que envió el mensaje
            if (!entry.getKey().equals(jugadorQueEnvio)) {
                try {
                    ClienteInfo destino = entry.getValue();
                    System.out.println("[Directorio] Enviando '" + mensaje + "' a " + destino.host + ":" + destino.puerto);
                    
                    // ¡Aquí ocurre la magia! Usa el despachador.
                    this.despachador.enviar(destino.host, destino.puerto, mensaje);
                    
                } catch (IOException e) {
                    System.err.println("[Directorio] Error al enviar a " + entry.getKey() + ": " + e.getMessage());
                }
            }
        }
    }

    // Clase interna simple para guardar host y puerto
    private static class ClienteInfo {
        public final String host;
        public final int puerto;

        public ClienteInfo(String host, int puerto) {
            this.host = host;
            this.puerto = puerto;
        }
    }
}