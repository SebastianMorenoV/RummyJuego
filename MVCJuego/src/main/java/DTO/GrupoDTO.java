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
public class GrupoDTO {
    private String tipo;       
    private int cantidad;     
    private List<FichaJuegoDTO> fichasGrupo; 
    private int fila;
    private int columna;

    public GrupoDTO() {
    }

    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo, int fila, int columna) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<FichaJuegoDTO> getFichasGrupo() {
        return fichasGrupo;
    }

    public void setFichasGrupo(List<FichaJuegoDTO> fichasGrupo) {
        this.fichasGrupo = fichasGrupo;
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
        return "GrupoDTO{" + "tipo=" + tipo + ", cantidad=" + cantidad + ", fichasGrupo=" + fichasGrupo + '}';
    }
}