package contratos;

public interface iPizarraJuego {

    void registrarJugador(String id, String payloadMano); // Guardas la mano serializada

    void actualizarMano(String id, String payloadMano);

    boolean esTurnoDe(String id);

    void avanzarTurno();

    boolean iniciarPartidaSiCorresponde();

    String getMano(String id);
    String getJugador();

    boolean procesarComando(String idCliente, String comando, String payload);
}
