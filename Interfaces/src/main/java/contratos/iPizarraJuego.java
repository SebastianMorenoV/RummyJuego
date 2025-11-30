package contratos;

import java.util.List;

/**
 * Interfaz de la pizarra del juego (Blackboard). Define las operaciones básicas
 * para gestionar el estado del juego y notificar acciones
 *
 * @author benja
 */
public interface iPizarraJuego {

    /**
     * Extrae y devuelve la primera ficha del mazo serializado, actualizando el
     * mazo restante.
     *
     * * @return La cadena serializada de la ficha tomada, o null si el mazo
     * está vacío.
     */
    String tomarFichaDelMazoSerializado();

    /**
     * Obtiene la lista de IDs de los jugadores en el orden en que se repartirán
     * los turnos.
     *
     * * @return Una lista de {@code String} con los identificadores de los
     * jugadores.
     */
    List<String> getOrdenDeTurnos();

    /**
     * Obtiene el último estado del tablero enviado por un cliente, representado
     * como una cadena serializada.
     *
     * * @return La cadena serializada del último tablero conocido.
     */
    String getUltimoTableroSerializado();

    /**
     * Obtiene el ID del último jugador que ejecutó un comando de acción o
     * movimiento.
     *
     * * @return El ID del jugador.
     */
    String getUltimoJugadorQueMovio();

    /**
     * Registra un nuevo jugador en la Pizarra.
     *
     * * @param id El ID del jugador (cliente).
     * @param payloadMano Una cadena que contiene la IP y el Puerto de escucha
     * del cliente.
     */
    void registrarJugador(String id, String payloadMano);

    /**
     * Verifica si el jugador identificado por 'id' es el jugador activo en el
     * turno actual.
     *
     * * @param id El ID del jugador a verificar.
     * @return true si es su turno, false en caso contrario.
     */
    boolean esTurnoDe(String id);

    /**
     * Avanza el índice de turno al siguiente jugador en la lista
     * preestablecida.
     */
    void avanzarTurno();

    /**
     * Intenta iniciar la partida si las condiciones (p.ej., número de
     * jugadores) son adecuadas, y establece el primer turno.
     *
     * * @return true si la partida se inicia, false si no es posible.
     */
    boolean iniciarPartidaSiCorresponde();

    /**
     * Obtiene el ID del jugador que tiene el turno actual.
     *
     * * @return El ID del jugador en turno.
     */
    String getJugador();

    /**
     * Punto de entrada para que el Procesador (Servidor) envíe comandos de
     * cliente a la Pizarra para su procesamiento y actualización de estado.
     *
     * * @param idCliente El ID del cliente que envió el comando.
     * @param comando La acción a ejecutar (p.ej., "MOVER", "FINALIZAR_TURNO").
     * @param payload Los datos asociados al comando (p.ej., tablero
     * serializado).
     */
    void procesarComando(String idCliente, String comando, String payload);

    /**
     * Obtiene la información temporal (IP y Puerto) del último cliente que se
     * intentó registrar.
     *
     * * @return Un array de String conteniendo la IP y el Puerto de un
     * cliente.
     */
    String[] getIpCliente();

    /**
     * Metodo para obtener la configuracion de la partida.
     * @return arreglo con String [comodines,fichas de cada mano.]
     */
    String[] getConfiguracionPartida();
    
    String getCandidatoIP();
    
    String getCandidatoPuerto();
    
    public String getCandidatoID();
}
