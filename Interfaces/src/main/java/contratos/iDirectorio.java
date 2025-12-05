package contratos;

import java.util.Map;

/**
 * Contrato para el "Libro de Direcciones" del servidor. Su única
 * responsabilidad es almacenar y proveer información de conexión de los
 * jugadores.
 *
 * @author Sebastian Moreno (Refactorizado)
 */
public interface iDirectorio {

    /**
     * Interfaz simple para exponer los datos de conexión de un cliente sin
     * exponer la clase de implementación interna del Directorio.
     */
    public interface ClienteInfoDatos {

        String getHost();

        int getPuerto();
    }

    /**
     * Registra la información de conexión de un nuevo jugador.
     *
     * @param idJugador ID único del jugador.
     * @param ip Host o IP del jugador.
     * @param puerto Puerto de escucha del jugador.
     */
    void addJugador(String idJugador, String ip, int puerto, String avatar, int c1, int c2, int c3, int c4);

    /**
     * Elimina a un jugador del directorio.
     *
     * @param idJugador ID del jugador a eliminar.
     */
    public void removeJugador(String idJugador);

    /**
     * Obtiene la información de conexión de un jugador específico.
     *
     * @param idJugador ID del jugador.
     * @return Los datos de conexión, o null si no se encuentra.
     */
    public ClienteInfoDatos getClienteInfo(String idJugador);

    /**
     * Obtiene un mapa con la información de conexión de TODOS los jugadores.
     *
     * @return Un Mapa de [idJugador -> InfoDeConexion]
     */
    public Map<String, ClienteInfoDatos> getAllClienteInfo();

}
