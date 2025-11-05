package contratos;

/**
 *
 * @author benja
 */
public interface iPizarraJuego {

    void registrarJugador(String id, String payloadMano); // Guardas la mano serializada

    boolean esTurnoDe(String id);

    void avanzarTurno();

    boolean iniciarPartidaSiCorresponde();

    String getMano(String id);
    
    String getJugador();

    boolean procesarComando(String idCliente, String comando, String payload);
}
