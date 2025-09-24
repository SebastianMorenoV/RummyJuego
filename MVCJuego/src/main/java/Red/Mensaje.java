/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import DTO.FichaJuegoDTO;
import DTO.JuegoDTO;
import java.io.Serializable;
/**
 *
 * @author Admin
 */
public class Mensaje implements Serializable {

    private TipoMensaje tipo;
    private JuegoDTO juego;
    private FichaJuegoDTO ficha;
    private int x;
    private int y;

    // Constructor para estado completo
    public Mensaje(JuegoDTO juego) {
        this.tipo = TipoMensaje.ESTADO_COMPLETO;
        this.juego = juego;
    }

    // Constructor para movimiento de ficha
    public Mensaje(FichaJuegoDTO ficha, int x, int y) {
        this.tipo = TipoMensaje.MOVER_FICHA;
        this.ficha = ficha;
        this.x = x;
        this.y = y;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public JuegoDTO getJuego() {
        return juego;
    }

    public FichaJuegoDTO getFicha() {
        return ficha;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
