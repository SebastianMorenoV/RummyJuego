package controlador;

import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import modelo.ModeloConfig;

/**
 * Esta clase representa el controlador de el MVC Configuracion.
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
    }

    @Override
    public void iniciarConfiguracion() {
        modelo.iniciarCU();
    }

    public void regresarPantallaPrincipal() {
        controladorCUPrincipal.pantallaInicial();
    }

    @Override
    public void cerrarCU() {
        modelo.cerrarCU();
    }

    @Override
    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente) {
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setIpCliente(ipCliente);
        modelo.setPuertoCliente(puertoCliente);
    }

    @Override
    public void setControladorCUPrincipal(iControlCUPrincipal controladorCUPrincipal) {
        this.controladorCUPrincipal = controladorCUPrincipal;
    }

}
