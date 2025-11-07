package contratos;

import java.util.List;

/**
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

    boolean procesarComando(String idCliente, String comando, String payload);

    String[] getIpCliente();
}
