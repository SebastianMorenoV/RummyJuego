package Controlador;

import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import Modelo.Modelo;
import java.util.List;

/**
 *
 * @author moren
 */
public class Controlador {

    Modelo modelo;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }

    public void iniciarJuego() {
        modelo.iniciarJuego();
//        modelo.iniciarTurno();
    }

    public void fichaSoltada(FichaJuegoDTO ficha, int x, int y) {
//        modelo.colocarFicha(ficha, x, y);
    }

    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    public void colocarFicha(List<GrupoDTO> grupos) {
        modelo.colocarFicha(grupos);
    }

    public void terminarTurno() {
        modelo.terminarTurno();
    }
    public void regresarFichaAMano(int idFicha) {
        modelo.regresarFichaAMano(idFicha);
    }
}
