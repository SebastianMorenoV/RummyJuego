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
        solicitarRegistro();
    }

    

    @Override
    public void setControladorEjercerTurno(iControlEjercerTurno control) {
        this.controladorEjercerTurno = control;
    }

 

    @Override
    public void solicitarRegistro() {
        System.out.println("[Control] Solicitando cambio a pantalla de registro..."); // Debug
        if (controladorRegistro != null) {
            modelo.cerrarCU();
            System.out.println("[ControlPrincipal] Ir a Registro...");
            controladorRegistro.iniciarRegistro();
        } else {
            System.err.println("Error: ControladorRegistro no inyectado. Revisa EnsambladoresMVC.");
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
//        if (controlSalaEspera != null) {
//            System.out.println("[ControlPrincipal] Abriendo Sala de Espera...");
//            ((control.ControlSalaDeEspera) controlSalaEspera).iniciarCU();
//        } else {
//            System.err.println("[ControlPrincipal] Error: No tengo referencia al ControlSalaEspera");
//        }
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
