package Control;

import Modelo.ModeloCUPrincipal;
import contratos.controladoresMVC.iControlCUPrincipal;

import contratos.controladoresMVC.iControlConfig;
import contratos.controladoresMVC.iControlEjercerTurno;
import contratos.controladoresMVC.iControlRegistro;
import contratos.controladoresMVC.iControlSalaEspera;
import contratos.iNavegacion;

/**
 * Esta clase representa el control del caso de uso Principal.
 *
 * @author benja
 */
public class ControlCUPrincipal implements iControlCUPrincipal {

    ModeloCUPrincipal modelo;
    iControlConfig controladorConfig;
    iControlEjercerTurno controladorEjercerTurno;
    iControlRegistro controladorRegistro;
    iControlSalaEspera controlSalaEspera;

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
        if (controladorConfig != null) {
            modelo.cerrarCU(); // Oculta el Lobby
            controladorConfig.iniciarConfiguracion(); // Abre la config
        } else {
            System.err.println("Error: ControladorConfig es nulo.");
        }
    }

    @Override
    public void setControladorEjercerTurno(iControlEjercerTurno control) {
        this.controladorEjercerTurno = control;
    }

    @Override
    public void solicitarRegistro() {
        
    }
    @Override
    public void procesarNavegacionRegistrarJugador (){
        modelo.cerrarCU();
        controladorRegistro.iniciarRegistro();
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

    public void notificarListo() {
        modelo.enviarEstoyListo();
    }

    @Override
    public void setControladorRegistro(iControlRegistro controlRegistro) {
        this.controladorRegistro = controlRegistro;
    }

    @Override
    public void setControlSalaEspera(iControlSalaEspera controlSalaEspera) {
        this.controlSalaEspera = controlSalaEspera;
    }

    @Override
    public void entrarSalaEspera() {

        controlSalaEspera.iniciarCU();
    }

    public void iniciarConfiguracionPartida() {
        this.casoUsoConfigurarPartida();
    }

    /**
     * Logica de navegacion
     *
     */
    public void procesarActualizacionSala() {
        // 1. Obtener los datos sucios del modelo 
        String datosSala = modelo.getDatosSala();

//        // 2. Cerrar la pantalla de registro
//        if (controladorRegistro != null) {
//            ((controlador.ControladorRegistro) controladorRegistro).cerrarVista();
//        }
        // 3. Abrir y actualizar Sala de Espera TODO ESTO MAL
//        if (vistaSalaEspera != null) {
//            if (!vistaSalaEspera.isVisible()) {
//                System.out.println("[ControlPrincipal] Abriendo Sala de Espera...");
//                vistaSalaEspera.setVisible(true);
//            }
//            // 4. Mandar los datos a la vista para que pinte los avatares
//            vistaSalaEspera.actualizarJugadores(datosSala);
//        }
    }

}
