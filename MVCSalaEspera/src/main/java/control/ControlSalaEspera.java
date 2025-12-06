/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlSalaEspera;
import modelo.ModeloSalaEspera;

/**
 *
 * @author benja
 */
public class ControlSalaEspera implements iControlSalaEspera {
    
    private ModeloSalaEspera modelo;
    
    public ControlSalaEspera(ModeloSalaEspera modelo) {
        this.modelo = modelo;
    }
    
    @Override
    public void iniciarSalaDeEspera() {
        System.out.println("[ControlSalaEspera] Recibida orden de iniciar. Notificando al modelo.");
        modelo.indicarMostrarPantalla();
    }
    
    @Override
    public void enviarVoto(boolean aceptado) {
        System.out.println("[ControlSalaEspera] Procesando voto: " + aceptado);
        modelo.enviarVoto(aceptado);
    }

    
}
