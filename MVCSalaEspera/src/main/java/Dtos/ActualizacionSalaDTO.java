/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dtos;

import java.util.List;
import vista.TipoEventoSala;

/**
 *
 * @author gael_
 */
public class ActualizacionSalaDTO {
    private final TipoEventoSala tipoEvento;
    private List<JugadorInfo> jugadores;
    
    public static class JugadorInfo {
        public String nombre;
        public boolean estaListo;

        public JugadorInfo(String nombre, boolean estaListo) {
            this.nombre = nombre;
            this.estaListo = estaListo;
        }
    }

    public ActualizacionSalaDTO(TipoEventoSala tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public ActualizacionSalaDTO(TipoEventoSala tipoEvento, List<JugadorInfo> jugadores) {
        this.tipoEvento = tipoEvento;
        this.jugadores = jugadores;
    }
    
    public TipoEventoSala getTipoEvento() {
        return tipoEvento;
    }

    public List<JugadorInfo> getJugadores() {
        return jugadores;
    }
        
        
}
