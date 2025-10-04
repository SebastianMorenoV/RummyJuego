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
    private final List<FichaJuegoDTO> manoDelJugador; // <-- NUEVO CAMPO

    public ActualizacionDTO(TipoEvento tipoEvento, boolean esMiTurno, List<FichaJuegoDTO> manoDelJugador) {
        this.tipoEvento = tipoEvento;
        this.esMiTurno = esMiTurno;
        this.manoDelJugador = manoDelJugador;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public boolean esMiTurno() {
        return esMiTurno;
    }

    public List<FichaJuegoDTO> getManoDelJugador() { // <-- NUEVO GETTER
        return manoDelJugador;
    }
}
