/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.util.List;

/**
 *
 * @author Sebastian Moreno
 */
public class ManoDTO {
    List<FichaJuegoDTO> fichasEnMano;

    public ManoDTO() {
    }

    public List<FichaJuegoDTO> getFichasEnMano() {
        return fichasEnMano;
    }

    public void setFichasEnMano(List<FichaJuegoDTO> fichasEnMano) {
        this.fichasEnMano = fichasEnMano;
    }
    
    
}
