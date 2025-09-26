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

    public void actualizarGruposEnTablero(List<GrupoDTO> grupos) {
        modelo.actualizarGruposEnTablero(grupos);
    }

    public void terminarTurno(List<GrupoDTO> gruposPropuestos) {
        // El controlador recibe datos puros, sin conocer a la Vista.
        // Su única tarea es pasarle estos datos al Modelo.
        modelo.actualizarGruposEnTablero(gruposPropuestos);
    }
}
