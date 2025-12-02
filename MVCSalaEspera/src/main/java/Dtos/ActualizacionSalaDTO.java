/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dtos;

import vista.TipoEventoSala;

/**
 *
 * @author gael_
 */
public class ActualizacionSalaDTO {
    private final TipoEventoSala tipoEvento;

    public ActualizacionSalaDTO(TipoEventoSala tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public TipoEventoSala getTipoEvento() {
        return tipoEvento;
    }
}
