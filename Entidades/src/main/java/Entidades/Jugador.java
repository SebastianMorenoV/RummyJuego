package Entidades;

public class Jugador {

    String nickname;
    String grupoColor;
    Mano manoJugador;
    boolean estaEnTurno;//proximamente...

    public Jugador() {
        this.manoJugador = new Mano();
    }

    public Jugador(String nickname, String grupoColor, Mano manoJugador) {
        this.nickname = nickname;
        this.grupoColor = grupoColor;
        this.manoJugador = manoJugador;
    }

    public void agregarFichaAJugador(Ficha ficha) {
        this.manoJugador.getFichasEnMano().add(ficha);
    }

    /**
     * Verifica si el jugador ha ganado (no tiene fichas en la mano).
     */
    public boolean haGanado() {
        return this.manoJugador.estaVacia();
    }

    // --- Getters y Setters ---
    public Mano getManoJugador() {
        return manoJugador;
    }

    public void setManoJugador(Mano manoJugador) {
        this.manoJugador = manoJugador;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
