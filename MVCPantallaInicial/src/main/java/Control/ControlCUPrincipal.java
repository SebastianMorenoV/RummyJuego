package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import contratos.controladoresMVC.iControlEjercerTurno;

/**
 * Esta clase representa el control del caso de uso Principal.
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal {

    ModeloCUPrincipal modelo;
    iControlConfig controladorConfig;
    iControlEjercerTurno controladorEjercerTurno;

    public ControlCUPrincipal(ModeloCUPrincipal modelo) {
        this.modelo = modelo;
    }

    @Override
    public void pantallaInicial() {
        modelo.iniciarLobby();
    }

    @Override
    public void iniciarCreacionPartida() {
        modelo.iniciarCreacionPartida();
    }

    @Override
    public void SolicitarUnirseAPartida() {
        modelo.SolicitarUnirseApartida();
    }

    @Override
    public void ejercerTurno() {
        controladorEjercerTurno.abrirCU();
    }

    @Override
    public void iniciarCU() {
        modelo.iniciarCU();
    }

    @Override
    public void cerrarCU() {
        modelo.cerrarCU();
    }

    @Override
    public void casoUsoConfigurarPartida() {
        if (this.controladorConfig != null) {
            System.out.println("[ControlPrincipal] Navegando a Configurar Partida...");
            this.controladorConfig.iniciarConfiguracion();
        } else {
            System.err.println("Error: ControladorConfig no ha sido ensamblado.");
        }
    }

    @Override
    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente) {
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setMiIp(ipCliente);
        modelo.setMiPuerto(puertoCliente);
    }

    @Override
    public void setControladorConfig(iControlConfig controladorConfig) {
        this.controladorConfig = controladorConfig;
    }

    @Override
    public void setControladorEjercerTurno(iControlEjercerTurno control) {
        this.controladorEjercerTurno = control;
    }
}
