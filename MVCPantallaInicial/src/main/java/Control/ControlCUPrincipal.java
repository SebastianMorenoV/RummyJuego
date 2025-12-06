package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSalaEspera;

/**
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal {

    ModeloCUPrincipal modelo;
    iControlSalaEspera controlSalaEspera;

    public ControlCUPrincipal(ModeloCUPrincipal modelo) {
        this.modelo = modelo;
    }


    @Override
    public void procesarNavegacionSalaEspera() {
        if (controlSalaEspera != null) {
            System.out.println("[ControlPrincipal] Delegando flujo a ControladorSalaEspera...");
            controlSalaEspera.iniciarSalaDeEspera();
        } else {
            System.err.println("[ControlPrincipal] Error CRÍTICO: Controlador Sala Espera no inyectado.");
        }
    }

    @Override
    public void SolicitarUnirseAPartida() {
        modelo.SolicitarUnirseApartida();
    }

    @Override
    public void setControladorSalaEspera(iControlSalaEspera controlSalaEspera) {
        this.controlSalaEspera = controlSalaEspera;
                
    }

}
