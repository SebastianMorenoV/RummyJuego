package directorio;

import contratos.iDirectorio;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación del "Libro de Direcciones". Almacena la información de
 * conexión (IP/Puerto) de cada jugador. Ya NO se encarga de enviar mensajes.
 *
 * * @author Sebastian Moreno (Refactorizado)
 */
public class Directorio implements iDirectorio {

    // Un mapa para guardar la info de conexión por ID de jugador
    // (idJugador -> ClienteInfo)
    private final Map<String, ClienteInfo> directorioJugadores;

    public Directorio() {
        // Mapa seguro para concurrencia
        this.directorioJugadores = new ConcurrentHashMap<>();
    }

    /**
     * Clase interna que almacena host/puerto e IMPLEMENTA la interfaz pública
     * de datos de conexión.
     */
    public static class ClienteInfo implements ClienteInfoDatos {

        public final String host;
        public final int puerto;

        public ClienteInfo(String host, int puerto) {
            this.host = host;
            this.puerto = puerto;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public int getPuerto() {
            return puerto;
        }
    }

    // Implementación de iDirectorio
    
    @Override
    public void addJugador(String idJugador, String ip, int puerto) {
        directorioJugadores.put(idJugador, new ClienteInfo(ip, puerto));
        System.out.println("[Directorio] Jugador " + idJugador + " registrado en " + ip + ":" + puerto);
    }

    @Override
    public void removeJugador(String idJugador) {
        directorioJugadores.remove(idJugador);
    }

    /**
     * Devuelve un mapa de solo lectura con la información de todos los
     * jugadores.
     */
    @Override
    public Map<String, ClienteInfoDatos> getAllClienteInfo() {
        // Devuelve una copia para evitar modificación externa y cumplir con la interfaz.
        return new java.util.HashMap<>(directorioJugadores);
    }

    /**
     * Devuelve la información de un jugador específico.
     */
    @Override
    public ClienteInfoDatos getClienteInfo(String idJugador) {
        return directorioJugadores.get(idJugador);
    }

}
