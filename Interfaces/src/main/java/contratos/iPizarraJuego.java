package contratos;

import java.util.List;

/**
 * Interfaz de la pizarra del juego (Blackboard). Define las operaciones básicas
 * para gestionar el estado del juego y notificar acciones
 *
 * @author benja
 */
public interface iPizarraJuego {

    String tomarFichaDelMazoSerializado();

    List<String> getOrdenDeTurnos();

    String getUltimoTableroSerializado();

    String getUltimoJugadorQueMovio();

    void registrarJugador(String id, String payloadMano); // Guardas la mano serializada

    boolean esTurnoDe(String id);

    void avanzarTurno();

    boolean iniciarPartidaSiCorresponde();

    String getJugador();

    void procesarComando(String idCliente, String comando, String payload);

    String[] getIpCliente();
}
