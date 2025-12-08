/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSolicitarInicio;
import modelo.ModeloSala;
import vista.VistaSalaEspera;

/**
 *
 * @author benja
 */
public class ControlSala implements iControlSolicitarInicio{
    
    iControlCUPrincipal controlCUPrincipal;
    ModeloSala modelo;
    private VistaSalaEspera vista;

    public ControlSala(ModeloSala modelo) {
        this.modelo = modelo;
    }
 
    public void setVista(VistaSalaEspera vista) {
        this.vista = vista;
    }
    
    @Override
    public void notificarEstoyListo() {
        modelo.enviarSolicitudInicio();
    }

    @Override
    public void setControladorCUPrincipal(iControlCUPrincipal controlCUPrincipal) {
        this.controlCUPrincipal=controlCUPrincipal;
    }
    
    @Override
    public void mostrarVista() {
        if (vista != null) {
            vista.setVisible(true);
        } else {
            System.err.println("Error: La vista de Sala de Espera no fue inyectada al controlador.");
        }
    }

    @Override
    public void unirseASala() {
        System.out.println("[ControlSala] Iniciando conexión con el servidor...");
        modelo.iniciarConexionRed(); 
    }

}
