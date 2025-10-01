/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import Controlador.Controlador;
import Modelo.Modelo;
import Vista.VistaTablero;
/**
 *
 * @author benja
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);
        VistaTablero vista = new VistaTablero(controlador);
        modelo.agregarObservador(vista);
        
        VistaTablero vistaJugadoresEnEspera = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugadoresEnEspera);
        
        
        controlador.iniciarJuego();
      
        
    }
    
}
