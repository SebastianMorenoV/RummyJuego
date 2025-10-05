/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

/**
 *
 * @author benja
 */
public class JugadorDTO {
    private String nombre;
    private int fichasRestantes;
    private boolean esTurno;

    public JugadorDTO() {
    }

    public JugadorDTO(String nombre, int fichasRestantes, boolean esTurno) {
        this.nombre = nombre;
        this.fichasRestantes = fichasRestantes;
        this.esTurno = esTurno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFichasRestantes() {
        return fichasRestantes;
    }

    public void setFichasRestantes(int fichasRestantes) {
        this.fichasRestantes = fichasRestantes;
    }

    public boolean isEsTurno() {
        return esTurno;
    }

    public void setEsTurno(boolean esTurno) {
        this.esTurno = esTurno;
    }
    
}
