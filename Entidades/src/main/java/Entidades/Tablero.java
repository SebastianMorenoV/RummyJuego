/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moren
 */
public class Tablero {

    List<Jugador> jugadores;
    List<Grupo> fichasEnTablero = new ArrayList<>();
    List<Ficha> mazo;

    public Tablero() {
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public List<Grupo> getFichasEnTablero() {
        return fichasEnTablero;
    }

    public List<Ficha> getMazo() {
        return mazo;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public void setFichasEnTablero(List<Grupo> fichasEnTablero) {
        this.fichasEnTablero = fichasEnTablero;
    }

    public void setMazo(List<Ficha> mazo) {
        this.mazo = mazo;
    }

    @Override
    public String toString() {
        return "Tablero{" + "jugadores=" + jugadores + ", fichasEnTablero=" + fichasEnTablero + ", mazo=" + mazo + '}';
    }
    
    

}
