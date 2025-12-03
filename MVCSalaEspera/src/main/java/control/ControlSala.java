/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSolicitarInicio;
import modelo.ModeloSala;

/**
 *
 * @author benja
 */
public class ControlSala implements iControlSolicitarInicio{
    
    //???
    iControlCUPrincipal controladorCUPrincipal;
    ModeloSala modelo;

    public ControlSala(ModeloSala modelo) {
        this.modelo = modelo;
    }
    
    @Override
    public void notificarEstoyListo() {
        modelo.enviarSolicitudInicio();
    }

    @Override
    public void setControladorCUPrincipal(iControlCUPrincipal controladorCUPrincipal) {
        this.controladorCUPrincipal=controladorCUPrincipal;
    }
    
    @Override
    public void setConfiguracion(String ipServidor,int puertoServidor, String ipCliente){
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setIpCliente(ipCliente);
    }
}
