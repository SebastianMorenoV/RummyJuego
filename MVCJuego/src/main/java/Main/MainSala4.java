/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Controlador.Controlador;
import Ensambladores.EnsambladorCliente;
import Ensambladores.EnsambladoresMVC;
import Modelo.Modelo;
import Util.Configuracion;
import Vista.VistaTablero;
import contratos.controladoresMVC.iLanzadorJuego;
import contratos.iDespachador;

/**
 *
 * @author gael_
 */
public class MainSala4 {
    public static void main(String[] args) {
        try {
            // CONFIGURACIÓN ÚNICA DE ESTE JUGADOR
            String miId = "Benja"; 
            int miPuerto = 9008;

            // 1. Crear Juego (Oculto)
            Modelo modeloJuego = new Modelo();
            // IMPORTANTE: Pasarle el ID al modelo del juego también
            modeloJuego.setMiId(miId); 
            
            Controlador controladorJuego = new Controlador(modeloJuego);
            VistaTablero vistaJuego = new VistaTablero(controladorJuego);
            modeloJuego.agregarObservador(vistaJuego);
            vistaJuego.setVisible(false);

            // 2. Callback
            iLanzadorJuego accionLanzar = () -> {
                System.out.println("--- JUGADOR 4: INICIANDO ---");
                controladorJuego.iniciarJuego();
                vistaJuego.setVisible(true);
            };

            // 3. Red
            EnsambladorCliente factoryRed = new EnsambladorCliente();
            iDespachador despachador = factoryRed.crearDespachador(Configuracion.getIpServidor(), Configuracion.getPuerto());
            modeloJuego.setDespachador(despachador);

            // 4. ENSAMBLAR CON PARÁMETROS ESPECÍFICOS
            EnsambladoresMVC ensamblador = new EnsambladoresMVC();
            
            // Pasamos ID y PUERTO aquí
            ensamblador.ensamblarJuegoCompleto(despachador, modeloJuego, accionLanzar, miId, miPuerto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
