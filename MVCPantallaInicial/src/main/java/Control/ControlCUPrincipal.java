package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import contratos.controladoresMVC.iControlRegistro;

/**
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal {

    ModeloCUPrincipal modelo;
    iControlConfig controladorConfig;
    private iControlRegistro controlRegistro;

    public ControlCUPrincipal(ModeloCUPrincipal modelo) {
        this.modelo = modelo;
    }

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
    public void casoUsoConfigurarPartida() {
        if (this.controladorConfig != null) {
            System.out.println("[ControlPrincipal] Navegando a Configurar Partida...");
            this.controladorConfig.iniciarConfiguracion();
        } else {
            System.err.println("Error: ControladorConfig no ha sido ensamblado.");
        }
    }

    public void setControladorConfig(iControlConfig controladorConfig) {
        this.controladorConfig = controladorConfig;
    }

    @Override
    public void setControladorRegistro(iControlRegistro controlRegistro) {
        this.controlRegistro = controlRegistro;
    }

    @Override
    public void solicitarRegistro() {
        System.out.println("[ControlPrincipal] Delegando a ControladorRegistro...");
        if (controlRegistro != null) {
            controlRegistro.iniciarRegistro();
        }
    }

}
