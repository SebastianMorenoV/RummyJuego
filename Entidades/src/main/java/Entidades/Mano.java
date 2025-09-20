/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author moren
 */
public class Mano {

    private int fichasEnMano;
    private List<Grupo> grupos;

    public Mano() {
        grupos = new ArrayList<>();
    }

    public int getFichasEnMano() {
        return fichasEnMano;
    }

    public List<Grupo> getGruposMano() {
        return grupos;
    }

    public void setFichasEnMano(int fichasEnMano) {
        this.fichasEnMano = fichasEnMano;
    }

    public void setGruposMano(List<Grupo> grupos) {
        this.grupos = grupos;
    }

}
