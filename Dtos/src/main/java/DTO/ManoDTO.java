package DTO;

import java.util.List;

/**
 * DTO que representa la mano del jugador
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
