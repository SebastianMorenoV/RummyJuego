package Controlador;

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

    /**
     * Metodo que le habla al modelo para iniciar el juego.
     */
    public void iniciarJuego() {
        modelo.iniciarJuego();
    }

    /**
     * Metodo que le habla al modelo para pasar el Turno.
     */
    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    /**
     * Metodo que le habla al modelo para colocar fichas.
     * @param grupos lista de grupos con las fichas a colocar.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        modelo.colocarFicha(grupos);

    }

    /**
     * Metodo que le habla al modelo para terminar el turno de un jugador.
     */
    public void terminarTurno() {
        modelo.terminarTurno();
    }

    /**
     * Metodo que le habla al modelo para regresar una ficha desde el tablero a la mano.
     * @param idFicha id de la ficha a regresar.
     */
    public void regresarFichaAMano(int idFicha) {
        modelo.regresarFichaAMano(idFicha);
    }

}
