/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import Entidades.Ficha;
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
    int x;
    int y;

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

    public FichaJuegoDTO(int idFicha, int numeroFicha, Color color, boolean comodin, int x, int y) {
        this.idFicha = idFicha;
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
        this.x = x;
        this.y = y;
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

    public int getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(int idFicha) {
        this.idFicha = idFicha;
    }

    public Ficha toFicha(int x, int y) {
        return new Ficha(this.idFicha, this.numeroFicha, this.color, this.comodin, x, y);
    }

    @Override
    public String toString() {
        return "FichaJuegoDTO{" + "idFicha=" + idFicha + ", numeroFicha=" + numeroFicha + ", color=" + color + ", comodin=" + comodin + ", x=" + x + ", y=" + y + '}';
    }

}
