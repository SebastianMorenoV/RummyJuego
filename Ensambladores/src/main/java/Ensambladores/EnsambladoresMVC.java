package Ensambladores;

import Control.ControlCUPrincipal;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlRegistro;
import contratos.iDespachador;
import contratos.iNavegacion;
import controlador.Controlador;
import controlador.ControladorConfig;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ModeloConfig;
import modelo.ModeloRegistro;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;
import vista.ObservadorRegistro;
import vista.RegistrarUsuario;
import vista.VistaSalaEspera;


/**
 * Esta clase ensambla los mvcs necesarios en el sistema. por ultimo utiliza el
 * metodo run.
 *
 * @author Sebastian Moreno
 */
public class EnsambladoresMVC {

    iDespachador despachador;

    public EnsambladoresMVC() {
        despachador = new ClienteTCP();
        try {
            ensamblarMVCPrincipal(despachador);
        } catch (UnknownHostException ex) {
            Logger.getLogger(EnsambladoresMVC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ensamblarMVCPrincipal(iDespachador despachador) throws UnknownHostException {
        // Tomar la ip del cliente
        String ipCliente = InetAddress.getLocalHost().getHostAddress(); // getHostAddress es mejor que toString
        System.out.println("ip cliente: " + ipCliente);

        //MVCConfigurarpartida
        ModeloConfig modeloConfiguracion = new ModeloConfig();
        modeloConfiguracion.setDespachador(despachador);

        ControladorConfig controladorConfiguracion = new ControladorConfig(modeloConfiguracion);
        controladorConfiguracion.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente);

        ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfiguracion);
        modeloConfiguracion.añadirObservador(vistaConfig);

        //MVCLobby
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

        controlPrincipal.setControladorConfig(controladorConfiguracion);
        controladorConfiguracion.setControladorCUPrincipal(controlPrincipal);

        //MVCRegistro registrar usuario
        RegistrarUsuario vistaRegistro = ensamblarMVCRegistro(despachador, ipCliente, controlPrincipal);

        /////////////MOCK DE REGISTRAR USUARIO A SALA DE ESPERA (maso)////////////
        // Creamos la Sala de Espera (Mock visual, sin controlador complejo por ahora si no lo necesitas)
        VistaSalaEspera vistaSalaEspera = new VistaSalaEspera();

        // Creamos la implementación de navegación "al vuelo"
        iNavegacion navegacion = new iNavegacion() {
            @Override
            public void iniciarConfiguracionPartida() {
                // Lógica existente o futura para config
                vistaLobby.setVisible(false);
                vistaConfig.setVisible(true);
            }

            @Override
            public void iniciarSalaEspera() {
                System.out.println("[Navegacion] Yendo a Sala de Espera...");
                // Aquí ocurre el cambio de pantalla
                vistaRegistro.setVisible(false); // Aseguramos que se cierre registro
                vistaLobby.setVisible(false);    // Aseguramos que se cierre lobby anterior
                
                vistaSalaEspera.setVisible(true);
            }
        };

        // Inyectamos la navegación en la vista de registro
        vistaRegistro.setNavegacion(navegacion);//////////

        System.out.println("[Ensamblador] Iniciando aplicación...");

        vistaLobby.setVisible(true);
        vistaConfig.setVisible(false);
        vistaRegistro.setVisible(false);
    }

    public RegistrarUsuario ensamblarMVCRegistro(iDespachador despachador, String ipCliente, iControlCUPrincipal controlPrincipal) {

        System.out.println("[Ensamblador] Ensamblando MVC Registro...");

        ModeloRegistro modeloRegistro = new ModeloRegistro();
        modeloRegistro.setDespachador(despachador);

        Controlador controladorRegistro = new Controlador(modeloRegistro);

        controladorRegistro.setConfiguracion(
                Configuracion.getIpServidor(),
                Configuracion.getPuerto(),
                ipCliente
        );

        if (controlPrincipal != null) {
            controlPrincipal.setControladorRegistro(controladorRegistro);
        }

        RegistrarUsuario vistaRegistro = new RegistrarUsuario((iControlRegistro) controladorRegistro);

        modeloRegistro.agregarObservador((ObservadorRegistro) vistaRegistro);

        //vista creada
        return vistaRegistro;
    }

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
