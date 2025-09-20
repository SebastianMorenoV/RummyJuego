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
    int numeroFicha;
    Color color;
    boolean comodin;
    int x;
    int y;

    public FichaJuegoDTO(int numeroFicha, Color color, boolean comodin) {
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
    }
    
    public FichaJuegoDTO() {
        
    }

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
}
