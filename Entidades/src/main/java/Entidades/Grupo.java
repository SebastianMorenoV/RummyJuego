/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.ArrayList;
import java.awt.Color;
import java.util.List;
import java.util.Random;

/**
 * Clase que representa un grupo de fichas
 *
 * @author moren
 */
public class Grupo {

    private String tipo;
    private int numFichas;
    private List<Ficha> fichas;
    private Random random = new Random();

    public Grupo() {
        this.fichas = new ArrayList<>();
    }

    public Grupo(String tipo, int numFichas, List<Ficha> fichas) {
        this.tipo = tipo;
        this.numFichas = numFichas;
        this.fichas = fichas;
    }

    public String getTipo() {
        return tipo;
    }

    public int getNumFichas() {
        return numFichas;
    }

    public List<Ficha> getFichas() {
        return fichas;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNumFichas(int numFichas) {
        this.numFichas = numFichas;
    }

    public void setFichas(List<Ficha> fichas) {
        this.fichas = fichas;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public String toString() {
        return "Grupo{" + "tipo=" + tipo + ", numFichas=" + numFichas + ", fichas=" + fichas + '}';
    }

}
