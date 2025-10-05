/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.awt.Color;

/**
 *
 * @author benja
 */
public class FichaJuegoDTO {

    int idFicha;
    int numeroFicha;
    Color color;
    boolean comodin;
    int fila;
    int columna;

    public FichaJuegoDTO(int numeroFicha, Color color, boolean comodin) {
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
    }

    public FichaJuegoDTO(int idFicha, int numeroFicha, Color color, boolean comodin) {
        this.idFicha = idFicha;
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
    }

    public FichaJuegoDTO(int idFicha, int numeroFicha, Color color, boolean comodin, int fila, int columna) {
        this.idFicha = idFicha;
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
        this.fila = fila;
        this.columna = columna;
    }

    public FichaJuegoDTO() {

    }

    // Getters and setters
    public int getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(int numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isComodin() {
        return comodin;
    }

    public void setComodin(boolean comodin) {
        this.comodin = comodin;
    }

    public int getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(int idFicha) {
        this.idFicha = idFicha;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public String toString() {
        return "FichaJuegoDTO{" + "idFicha=" + idFicha + ", numeroFicha=" + numeroFicha + ", color=" + color + ", comodin=" + comodin + '}';
    }
}