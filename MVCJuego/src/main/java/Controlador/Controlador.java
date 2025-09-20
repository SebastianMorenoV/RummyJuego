
package Controlador;

import DTO.FichaJuegoDTO;
import Modelo.Modelo;

/**
 *
 * @author moren
 */
public class Controlador {
    Modelo modelo;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }
    
    public void iniciarJuego(){
        modelo.iniciarJuego();
    }
    
    public void fichaSoltada(FichaJuegoDTO ficha, int x, int y){
        modelo.colocarFicha(ficha, x, y);
    }
}
