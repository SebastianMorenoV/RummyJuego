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
    private int jugadoresListos;
    private int jugadoresTotales;

    public ActualizacionSalaDTO(TipoEventoSala tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public ActualizacionSalaDTO(TipoEventoSala tipoEvento, int jugadoresListos, int jugadoresTotales) {
        this.tipoEvento = tipoEvento;
        this.jugadoresListos = jugadoresListos;
        this.jugadoresTotales = jugadoresTotales;
    }
    
    public TipoEventoSala getTipoEvento() {
        return tipoEvento;
    }

    public int getJugadoresListos() {
        return jugadoresListos;
    }

    public int getJugadoresTotales() {
        return jugadoresTotales;
    }
        
        
}
