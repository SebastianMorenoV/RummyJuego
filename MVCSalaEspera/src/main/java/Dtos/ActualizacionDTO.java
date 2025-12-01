/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dtos;

import vista.TipoEvento;

/**
 *
 * @author gael_
 */
public class ActualizacionDTO {
    private final TipoEvento tipoEvento;

    public ActualizacionDTO(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
}
