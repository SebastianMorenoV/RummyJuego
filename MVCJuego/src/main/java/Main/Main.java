/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import Controlador.Controlador;
import Modelo.Modelo;
import Red.ClienteRummy;
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

        ClienteRummy cliente = new ClienteRummy(modelo);
        cliente.conectar("192.168.1.71", 5000); // tu IP del servidor y puerto
        controlador.setCliente(cliente);

        controlador.iniciarJuego();
    }
    
}
