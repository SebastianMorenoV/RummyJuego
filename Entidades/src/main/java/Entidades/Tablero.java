/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author moren
 */
public class Tablero {

    List<Jugador> jugadores;
    List<Grupo> fichasEnTablero = new ArrayList<>();
    List<Ficha> mazo;

    public Tablero() {
        this.mazo = new ArrayList<>();
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

    public List<Ficha> getTodasFichas() {
        List<Ficha> todas = new ArrayList<>();
        for (Grupo g : fichasEnTablero) {
            todas.addAll(g.getFichas());
        }
        return todas;
    }

    ////////////////////////////////////////
    public Ficha tomarFichaMazo() {
        List<Ficha> mazo = getMazo();
        if (mazo.isEmpty()) {
            return null; // si no hay fichas, nada que hacer
        }
        Ficha ficha = mazo.remove(0); // tomar la primera ficha del mazo (ya está barajada)
        System.out.println("Tamaño de mazo: " + mazo.size());
        return ficha;

    }

    public List<Ficha> crearMazoCompleto() {
        List<Ficha> mazo = getMazo();
        Color[] colores = {Color.RED, Color.BLUE, Color.BLACK, Color.ORANGE};

        // IDs únicos del 1 al 108
        List<Integer> idsDisponibles = new ArrayList<>();
        for (int i = 1; i <= 108; i++) {
            idsDisponibles.add(i);
        }
        Collections.shuffle(idsDisponibles);

        Random random = new Random();

        // Crear 104 fichas normales (2 sets de 13 números por color)
        for (Color color : colores) {
            for (int set = 0; set < 2; set++) {
                for (int numero = 1; numero <= 13; numero++) {
                    int id = idsDisponibles.remove(0);
                    mazo.add(new Ficha(id, numero, color, false));
                }
            }
        }

        // Crear 4 comodines
        for (int i = 0; i < 4; i++) {
            int id = idsDisponibles.remove(0);
            mazo.add(new Ficha(id, 0, Color.GRAY, true)); // comodines
        }

        Collections.shuffle(mazo); // barajar
        setMazo(mazo);

        return mazo;
    }

    @Override
    public String toString() {
        return "Tablero{" + "jugadores=" + jugadores + ", fichasEnTablero=" + fichasEnTablero + ", mazo=" + mazo + '}';
    }

}
