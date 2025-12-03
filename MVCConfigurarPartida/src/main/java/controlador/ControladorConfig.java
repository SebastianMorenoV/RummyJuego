/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import modelo.ModeloConfig;

/**
 *
 * @author moren
 */
public class ControladorConfig implements iControlConfig {

    iControlCUPrincipal controladorCUPrincipal;
    ModeloConfig modelo;

    public ControladorConfig(ModeloConfig modelo) {
        this.modelo = modelo;
    }

    @Override
    public void configurarPartida(int comodines, int fichas) {
        modelo.configurarPartida(comodines, fichas);
        controladorCUPrincipal.solicitarRegistro();
    }

    @Override
    public void iniciarConfiguracion() {
        modelo.iniciarCU();
    }
    
    public void regresarPantallaPrincipal(){
        controladorCUPrincipal.pantallaInicial();
    }

    @Override
    public void setControladorCUPrincipal(iControlCUPrincipal controladorCUPrincipal) {
        this.controladorCUPrincipal = controladorCUPrincipal;
    }
    
    @Override
    public void setConfiguracion(String ipServidor,int puertoServidor, String ipCliente){
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setIpCliente(ipCliente);
    }

}
