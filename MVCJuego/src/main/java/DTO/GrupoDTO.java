package DTO;

import java.util.List;

public class GrupoDTO {

    private String tipo;
    private int cantidad;
    private List<FichaJuegoDTO> fichasGrupo;

    // --- CAMPOS NUEVOS ---
    private int fila;
    private int columna;

    // --- CONSTRUCTORES ---
    public GrupoDTO() {
    }

    // Constructor principal que incluye la posición de anclaje
    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo, int fila, int columna) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
        this.fila = fila;
        this.columna = columna;
    }

    // Constructor antiguo para compatibilidad
    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo) {
        this(tipo, cantidad, fichasGrupo, -1, -1);
    }

    // --- GETTERS Y SETTERS ---
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

    // --- GETTERS NUEVOS ---
    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return "GrupoDTO{" + "tipo=" + tipo + ", fichas=" + fichasGrupo + ", fila=" + fila + ", col=" + columna + '}';
    }
}
