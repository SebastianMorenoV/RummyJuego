package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;


/**
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal{

    ModeloCUPrincipal modelo;
    iControlConfig controladorConfig;

    public ControlCUPrincipal(ModeloCUPrincipal modelo) {
        this.modelo = modelo;
    }
 
    public void pantallaInicial(){
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
}
