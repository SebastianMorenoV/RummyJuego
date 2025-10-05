/*
 
Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template*/
package DTO;

import Vista.TipoEvento;
import java.util.List;

/**
 *
 *
 * @author hp
 */
public class ActualizacionDTO {

    private final TipoEvento tipoEvento;
    private final boolean esMiTurno;
    private final List<FichaJuegoDTO> manoDelJugador; 

    public ActualizacionDTO(TipoEvento tipoEvento, boolean esMiTurno, List<FichaJuegoDTO> manoDelJugador) {
        this.tipoEvento = tipoEvento;
        this.esMiTurno = esMiTurno;
        this.manoDelJugador = manoDelJugador;
    }

    /**
     * Metodo que obtiene el tipo de evento que se realizo en el juego.
     * @return 
     */
    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    /**
     * Metodo que indica el si es el turno de un jugador.
     * @return 
     */
    public boolean esMiTurno() {
        return esMiTurno;
    }

    /**
     * Metodo que obtiene la mano de un jugador.
     * @return 
     */
    public List<FichaJuegoDTO> getManoDelJugador() {
        return manoDelJugador;
    }
}
