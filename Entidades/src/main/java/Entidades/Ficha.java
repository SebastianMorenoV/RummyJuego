/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.awt.Color;

/**
 *
 * @author moren
 */
public class Ficha {

    int id;
    int numero;
    Color color;
    boolean comodin;
    
    public Ficha() {
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

    @Override
    public String toString() {
        return "Ficha{" + "id=" + id + ", numero=" + numero + ", color=" + color + ", comodin=" + comodin + '}';
    }


}
