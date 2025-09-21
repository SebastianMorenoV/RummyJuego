/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.util.List;

/**
 *
 * @author benja
 */
public class JuegoDTO {
    private String jugadorActual;
    private List<JugadorDTO> jugadores;
    private List<GrupoDTO> gruposEnTablero;
    private int fichasMazo;     // solo cantidad, no toda la lista
    private String mensaje;     // para mostrar en la UI
    private String evento;
    public JuegoDTO() {
    }

    public JuegoDTO(String jugadorActual, List<JugadorDTO> jugadores, List<GrupoDTO> gruposEnTablero, int fichasMazo, String mensaje) {
        this.jugadorActual = jugadorActual;
        this.jugadores = jugadores;
        this.gruposEnTablero = gruposEnTablero;
        this.fichasMazo = fichasMazo;
        this.mensaje = mensaje;
    }

    public String getJugadorActual() {
        return jugadorActual;
    }

    public void setJugadorActual(String jugadorActual) {
        this.jugadorActual = jugadorActual;
    }

    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<JugadorDTO> jugadores) {
        this.jugadores = jugadores;
    }

    public List<GrupoDTO> getGruposEnTablero() {
        return gruposEnTablero;
    }

    public void setGruposEnTablero(List<GrupoDTO> gruposEnTablero) {
        this.gruposEnTablero = gruposEnTablero;
    }

    public int getFichasMazo() {
        return fichasMazo;
    }

    public void setFichasMazo(int fichasMazo) {
        this.fichasMazo = fichasMazo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }
    
}
