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
        public final String avatar;
        public final int c1, c2, c3, c4;

        public ClienteInfo(String host, int puerto, String avatar, int c1, int c2, int c3, int c4) {
            this.host = host;
            this.puerto = puerto;
            this.avatar = avatar;
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
            this.c4 = c4;
        }

        /**
         * @return La dirección IP o nombre de host del cliente.
         */
        @Override
        public String getHost() {
            return host;
        }

        /**
         * @return El número de puerto en el que el cliente está escuchando para
         * recibir mensajes (puerto de retorno del cliente).
         */
        @Override
        public int getPuerto() {
            return puerto;
        }

        public String getAvatar() {
            return avatar;
        }

        public int getC1() {
            return c1;
        }

        public int getC2() {
            return c2;
        }

        public int getC3() {
            return c3;
        }

        public int getC4() {
            return c4;
        }

    }

    /**
     * Agrega un nuevo jugador al directorio. La información se almacena usando
     * el ID del jugador como clave.
     *
     * @param idJugador
     * @param ip
     * @param puerto
     * @param avatar
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     */
    @Override
    public void addJugador(String idJugador, String ip, int puerto, String avatar, int c1, int c2, int c3, int c4) {
        // Pasamos todos los datos al constructor
        directorioJugadores.put(idJugador, new ClienteInfo(ip, puerto, avatar, c1, c2, c3, c4));
        System.out.println("[Directorio] Jugador " + idJugador + " registrado con Avatar: " + avatar);
    }

    /**
     * Elimina un jugador y su información de conexión del directorio.
     *
     * * @param idJugador El ID del jugador a eliminar.
     * @param idJugador
     */
    @Override
    public void removeJugador(String idJugador) {
        directorioJugadores.remove(idJugador);
    }

    /**
     * Devuelve un mapa de solo lectura (una copia) con la información de
     * conexión de todos los jugadores registrados.
     *
     * @return Un {@code Map<String, ClienteInfoDatos>} con el ID del jugador
     * como clave.
     */
    @Override
    public Map<String, ClienteInfoDatos> getAllClienteInfo() {
        return new java.util.HashMap<>(directorioJugadores);
    }

    /**
     * Devuelve la información de conexión (IP y Puerto) de un jugador
     * específico.
     *
     * @param idJugador El ID del jugador cuya información se desea obtener.
     * @return El objeto {@link ClienteInfoDatos} con la IP y el Puerto, o null
     * si el ID no existe.
     */
    @Override
    public ClienteInfoDatos getClienteInfo(String idJugador) {
        return directorioJugadores.get(idJugador);
    }

}
