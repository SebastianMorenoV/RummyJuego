package Controlador;

import DTO.GrupoDTO;
import Modelo.Modelo;
import java.util.List;

/**
 * Controlador del sistema encargado de gestionar la comunicación entre la
 * interfaz de usuario (vista/cliente) y la lógica de negocio representada por
 * el Modelo.
 *
 * @author moren
 */
public class Controlador {

    Modelo modelo;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }

    /**
     * Metodo que le habla al modelo para iniciar el juego.
     */
    public void iniciarJuego() {
        modelo.iniciarJuego();
    }

    /**
     * Le dice al Modelo que coloque la ficha (para la UI local). Serializa la
     * LISTA ENTERA de DTOs a un solo String. 3 Le dice al Ensamblador que envíe
     * ESE ÚNICO String.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        modelo.colocarFicha(grupos);
    }

    /**
     * Metodo que le habla al modelo para regresar una ficha desde el tablero a
     * la mano.
     *
     * @param idFicha id de la ficha a regresar.
     */
    public void regresarFichaAMano(int idFicha) {
        modelo.regresarFichaAMano(idFicha);
    }

    /**
     * Metodo que le habla al modelo para pasar el Turno.
     */
    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    /**
     * Metodo que le habla al modelo para terminar el turno de un jugador.
     */
    public void terminarTurno() {
        modelo.terminarTurno();
    }

}
