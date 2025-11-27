package Entidades;

/**
 * Representa a un jugador dentro del juego, almacenando su información básica
 * (como nickname y color de grupo), así como su mano de fichas.
 *
 * Entidad principal para gestionar al jugador en la partida.
 *
 * @author chris
 */
public class Jugador {

    String nickname;
    String grupoColor;
    Mano manoJugador;
    private boolean haHechoPrimerMovimiento;

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

    /**
     * Obtiene el estado de si el jugador ha realizado su movimiento inicial
     * de 30 o más puntos.
     *
     * @return true si el jugador ya ha hecho su primer movimiento, false en caso contrario.
     */
    public boolean isHaHechoPrimerMovimiento() {
        return haHechoPrimerMovimiento;
    }

    /**
     * Establece si el jugador ha realizado su primer movimiento de 30 o más puntos.
     *
     * @param haHechoPrimerMovimiento El nuevo estado del primer movimiento.
     */
    public void setHaHechoPrimerMovimiento(boolean haHechoPrimerMovimiento) {
        this.haHechoPrimerMovimiento = haHechoPrimerMovimiento;
    }

    /**
     * Verifica si el jugador ha ganado la partida.
     * Un jugador gana si se ha quedado sin fichas en su mano.
     *
     * @return true si la mano del jugador está vacía (ha ganado), false en caso contrario.
     */
    public boolean haGanado() {
        return this.manoJugador.estaVacia();
    }

    // Getters y Setters
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
