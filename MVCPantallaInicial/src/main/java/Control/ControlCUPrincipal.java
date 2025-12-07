package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlJuego;
import contratos.controladoresMVC.iControlSalaEspera;
import contratos.vistasMVC.IVistaJuego;// Importación necesaria

/**
 * Orquestador principal de la aplicación, responsable de la navegación entre
 * los Casos de Uso.
 */
public class ControlCUPrincipal implements iControlCUPrincipal {

    ModeloCUPrincipal modelo;
    iControlSalaEspera controladorSalaEspera;
    private iControlJuego controladorJuego;
    private IVistaJuego vistaTableroJuego; // CAMPO FALTANTE AÑADIDO (Referencia a la Vista)

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
    public void casoUsoSolicitarUnirseAPartida() {
        if (this.controladorSalaEspera != null) {
            System.out.println("[ControlPrincipal] Navegando a Sala de Espera...");
            this.controladorSalaEspera.iniciarSalaEspera();
        } else {
            System.err.println("Error: ControladorSalaEspera no ha sido ensamblado.");
        }
    }

    /**
     * CU: INICIAR PARTIDA (Transición final) La VistaTablero se muestra en el
     * momento de la transición.
     */
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

    public void setControladorSalaEspera(iControlSalaEspera controladorSalaEspera) {
        this.controladorSalaEspera = controladorSalaEspera;
    }

    public void setControladorJuego(iControlJuego controladorJuego) {
        this.controladorJuego = controladorJuego;
    }

    /**
     * MÉTODO FALTANTE (SOLUCIÓN AL ERROR): El Ensamblador usa este setter para
     * inyectar la Vista concreta del juego.
     * @param vistaTableroJuego
     */
    public void setVistaTableroJuego(IVistaJuego vistaTableroJuego) {
        this.vistaTableroJuego = vistaTableroJuego;
    }

    @Override
    public void casoUsoConfigurarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
