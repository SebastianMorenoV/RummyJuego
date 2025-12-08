package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import contratos.controladoresMVC.iControlJuego;
import contratos.controladoresMVC.iControlSolicitarInicio;
import contratos.vistasMVC.IVistaJuego;


/**
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal{

    ModeloCUPrincipal modelo;
    iControlSolicitarInicio controlSala;
    private iControlJuego controladorJuego;
    private IVistaJuego vistaTableroJuego;
    
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
    public void casoUsoSolicitarUnirseAPartida() {
        if (this.controlSala != null) {
            System.out.println("[ControlPrincipal] Navegando a Sala de Espera...");
            this.controlSala.mostrarVista();
        } else {
            System.err.println("Error: ControladorSalaEspera no ha sido ensamblado.");
        }
    }
    
    @Override
    public void casoUsoIniciarPartida() {
        System.out.println("[Orquestador] INICIANDO PARTIDA: Condición cumplida. Lanzando VistaTablero.");

        // 1. Delegar al Controlador de Juego la orden de inicio (Modelo envía el comando COMANDO_INICIAR_PARTIDA)
        this.controladorJuego.iniciarPartida();

        // 2. MOSTRAR LA VISTA INYECTADA
        if (this.vistaTableroJuego != null) {
            this.vistaTableroJuego.setVisible(true);
        }
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
    
    public void setControladorJuego(iControlJuego controladorJuego) {
        this.controladorJuego = controladorJuego;
    }
    
    public void setVistaTableroJuego(IVistaJuego vistaTableroJuego) {
        this.vistaTableroJuego = vistaTableroJuego;
    }

    @Override
    public void setControladorSalaEspera(iControlSolicitarInicio controlSala) {
        this.controlSala=controlSala;
    }
}
