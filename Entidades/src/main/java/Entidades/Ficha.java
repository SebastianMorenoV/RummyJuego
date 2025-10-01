package Entidades;

import java.awt.Color;

/**
 * Esta clase representa a una ficha en el sistema.
 *
 * @author Sebastian Moreno
 */
public class Ficha {

    int id;
    int numero;
    Color color;
    boolean comodin;
    private int fila = -1; // -1 indica que no está en el tablero
    private int columna = -1;

    public Ficha() {
    }

    public Ficha(int id, int numero, Color color, boolean comodin, int fila, int columna) {
        // ... tu código ...
        this.fila = fila;
        this.columna = columna;
    }

    public Ficha(int numero, Color color, boolean comodin) {
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
    }

    public Ficha(int id, int numero, Color color, boolean comodin) {
        this.id = id;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
    }

    public int getNumero() {
        return numero;
    }

    public Color getColor() {
        return color;
    }

    public boolean isComodin() {
        return comodin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public String toString() {
        return "Ficha{" + "id=" + id + ", numero=" + numero + ", color=" + color + ", comodin=" + comodin + '}';
    }

}
