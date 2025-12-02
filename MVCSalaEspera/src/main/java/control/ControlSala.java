/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlCUPrincipal;
import modelo.ModeloSala;

/**
 *
 * @author benja
 */
public class ControlSala {
    
    //???
    iControlCUPrincipal controladorCUPrincipal;
    private ModeloSala modelo;

    public ControlSala(ModeloSala modelo) {
        this.modelo = modelo;
    }
    
    public void notificarEstoyListo() {
        modelo.enviarSolicitudInicio();
    }
}
