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

    private final Map<String, ClienteInfo> directorioJugadores;

    public Directorio() {
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

        /**
         * @return La dirección IP o nombre de host del cliente.
         */
        @Override
        public String getHost() {
            return host;
        }

        /**
         * @return El número de puerto en el que el cliente está escuchando 
         * para recibir mensajes (puerto de retorno del cliente).
         */
        @Override
        public int getPuerto() {
            return puerto;
        }
    }

    /**
     * Agrega un nuevo jugador al directorio.
     * La información se almacena usando el ID del jugador como clave.
     * * @param idJugador El ID único del jugador (cliente).
     * @param ip La dirección IP del cliente.
     * @param puerto El puerto de escucha del cliente.
     */
    @Override
    public void addJugador(String idJugador, String ip, int puerto) {
        directorioJugadores.put(idJugador, new ClienteInfo(ip, puerto));
        System.out.println("[Directorio] Jugador " + idJugador + " registrado en " + ip + ":" + puerto);
    }

    /**
     * Elimina un jugador y su información de conexión del directorio.
     * * @param idJugador El ID del jugador a eliminar.
     */
    @Override
    public void removeJugador(String idJugador) {
        directorioJugadores.remove(idJugador);
    }

    /**
     * Devuelve un mapa de solo lectura (una copia) con la información de conexión 
     * de todos los jugadores registrados.
     *
     * @return Un {@code Map<String, ClienteInfoDatos>} con el ID del jugador como clave.
     */
    @Override
    public Map<String, ClienteInfoDatos> getAllClienteInfo() {
        return new java.util.HashMap<>(directorioJugadores);
    }

    /**
     * Devuelve la información de conexión (IP y Puerto) de un jugador específico.
     *
     * @param idJugador El ID del jugador cuya información se desea obtener.
     * @return El objeto {@link ClienteInfoDatos} con la IP y el Puerto, o null si el ID no existe.
     */
    @Override
    public ClienteInfoDatos getClienteInfo(String idJugador) {
        return directorioJugadores.get(idJugador);
    }

}
