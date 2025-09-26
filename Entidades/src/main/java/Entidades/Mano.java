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

    private int cantidadFichasEnMano;
    private List<Ficha> fichasEnMano;

    public Mano() {
        fichasEnMano = new ArrayList<>();
    }

    public Mano(int cantidadFichasEnMano, List<Ficha> fichasEnMano) {
        this.cantidadFichasEnMano = cantidadFichasEnMano;
        this.fichasEnMano = fichasEnMano;
    }

    public int getCantidadFichasEnMano() {
        return cantidadFichasEnMano;
    }

    public void setCantidadFichasEnMano(int cantidadFichasEnMano) {
        this.cantidadFichasEnMano = cantidadFichasEnMano;
    }

    public List<Ficha> getFichasEnMano() {
        return fichasEnMano;
    }

    public void setFichasEnMano(List<Ficha> fichasEnMano) {
        this.fichasEnMano = fichasEnMano;
    }

  


}
