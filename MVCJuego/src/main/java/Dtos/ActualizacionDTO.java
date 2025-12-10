package Dtos;

import DTO.FichaJuegoDTO;
import Vista.TipoEvento;
import java.util.List;

/**
 * DTO para enviar al cliente la actualización del estado del juego. Incluye el
 * tipo de evento, si es turno del jugador y su mano actual.
 *
 * @author Sebastian Moreno
 */
public class ActualizacionDTO {

    private final TipoEvento tipoEvento;
    private final boolean esMiTurno;
    private final List<FichaJuegoDTO> manoDelJugador;
    private String mensaje;

    public ActualizacionDTO(TipoEvento tipoEvento, boolean esMiTurno,
            List<FichaJuegoDTO> manoDelJugador) {
        this.tipoEvento = tipoEvento;
        this.esMiTurno = esMiTurno;
        this.manoDelJugador = manoDelJugador;
        this.mensaje = null;
    }

    public ActualizacionDTO(TipoEvento tipoEvento, String mensaje) {
        this.tipoEvento = tipoEvento;
        this.mensaje = mensaje;
        // Valores por defecto para lo demás
        this.esMiTurno = false;
        this.manoDelJugador = null;
    }

    /**
     * Metodo que obtiene el tipo de evento que se realizo en el juego.
     *
     * @return TipoEvento
     */
    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    /**
     * Metodo que indica el si es el turno de un jugador.
     *
     * @return boolean
     */
    public boolean esMiTurno() {
        return esMiTurno;
    }

    /**
     * Metodo que obtiene la mano de un jugador.
     *
     * @return Lista de fichasJuegoDTO
     */
    public List<FichaJuegoDTO> getManoDelJugador() {
        return manoDelJugador;
    }

    /**
     * Obtiene el mensaje de chat o notificación asociado al evento.
     *
     * @return String con el mensaje (ej: "Juan: Hola a todos")
     */
    public String getMensaje() {
        return mensaje;
    }
}
