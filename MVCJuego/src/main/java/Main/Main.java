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
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);

        // Creas la vista para el Jugador 1 (ID 0)
        VistaTablero vistaJugador1 = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugador1);

        // Creas la vista para el Jugador 2 (ID 1)
        VistaTablero vistaJugador2 = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugador2);

        controlador.crearYUnirseAPartida();
        controlador.iniciarJuego();
    }

}
