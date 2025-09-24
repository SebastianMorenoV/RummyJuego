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
    private String tipo;       // "escalera" o "numero"
    private int cantidad;      // en vez de mandar todas las fichas
    private List<FichaJuegoDTO> fichasGrupo; // un String para mostrar fácil en UI (ej: "1R, 2R, 3R")

    public GrupoDTO() {
    }

    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
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

    @Override
    public String toString() {
        return "GrupoDTO{" + "tipo=" + tipo + ", cantidad=" + cantidad + ", fichasGrupo=" + fichasGrupo + '}';
    }

    
    
}
