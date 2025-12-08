/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSalaEspera;
import modelo.IModeloSalaEspera;

/**
 *
 * @author benja
 */
public class ControlSalaEspera implements iControlSalaEspera{

    IModeloSalaEspera modelo;
    private iControlCUPrincipal controlCUPrincipal;

    public ControlSalaEspera(IModeloSalaEspera modelo) {
        this.modelo = modelo;
    }
    
    @Override
    public void iniciarSalaEspera() {
        System.out.println("[ControlSalaEspera] Simulación de entrada de jugadores e inicio de Sala de Espera.");
        this.modelo.simularEntradaDeJugadores();
    }

    public void jugadorPulsaListo() {
        modelo.jugadorPulsaListo();
    }
    
    public void iniciarPartidaFinal() {
        if (controlCUPrincipal != null) {
            controlCUPrincipal.casoUsoIniciarPartida();
        } else {
            System.err.println("Error: ControladorCUPrincipal no inyectado. No se puede iniciar la partida.");
        }
    }

    public void setControlCUPrincipal(iControlCUPrincipal controlCUPrincipal) {
        this.controlCUPrincipal = controlCUPrincipal;
    }

    @Override
    public void cerrarCU() {
        modelo.cerrarCU();
    }
    
    
}
