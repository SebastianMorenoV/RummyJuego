package Entidades;

/**
 *
 * @author chris
 */
public class Jugador {

    String nickname;
    String grupoColor;
    Mano manoJugador;
    private boolean haHechoPrimerMovimiento; // <-- AÑADIR ESTO

    public Jugador() {
        this.manoJugador = new Mano();
        this.haHechoPrimerMovimiento = false;
    }

    public Jugador(String nickname, String grupoColor, Mano manoJugador) {
        this.nickname = nickname;
        this.grupoColor = grupoColor;
        this.manoJugador = manoJugador;
        this.haHechoPrimerMovimiento = false;
    }

    /**
     * Agrega una ficha a la mano del jugador
     *
     * @param ficha ficha que se agrega a la mano
     */
    public void agregarFichaAJugador(Ficha ficha) {
        this.manoJugador.getFichasEnMano().add(ficha);
    }
    
    public boolean isHaHechoPrimerMovimiento() {
        return haHechoPrimerMovimiento;
    }

    public void setHaHechoPrimerMovimiento(boolean haHechoPrimerMovimiento) {
        this.haHechoPrimerMovimiento = haHechoPrimerMovimiento;
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
