
package Ensambladores;

import Control.ControlCUPrincipal;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.iDespachador;
import controlador.ControladorConfig;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ModeloConfig;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;

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

//    public void ensamblarMVCPrincipal(iDespachador despachador) throws UnknownHostException {
//
//        //Tomar la ip del cliente ejecutando el MVC.
//        String ipCliente = InetAddress.getLocalHost().toString();
//        System.out.println("ip cliente: " + ipCliente);
//
//        //Instanciar los elementos de el CU de Solicitar unirse a partida.
//        
//        ////
//        
//        ModeloConfig modeloConfiguracion = new ModeloConfig();
//        modeloConfiguracion.setDespachador(despachador);
//        ControladorConfig controladorConfiguracion = new ControladorConfig(modeloConfiguracion);
//        ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfiguracion);
//        modeloConfiguracion.añadirObservador(vistaConfig);
//
//        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
//        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
//        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
//        modeloPrincipal.añadirObservador(vistaLobby);
//
//        controlPrincipal.setControladorConfig(controladorConfiguracion);
//        controladorConfiguracion.setControladorCUPrincipal(controlPrincipal);
//        
//        //Le paso la ip y puerto al control para que se lo pase a modelo
//        controladorConfiguracion.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(),ipCliente);
//
//        System.out.println("[Ensamblador] Iniciando aplicación...");
//        vistaLobby.setVisible(true);
//        vistaConfig.setVisible(false);
//
//    }
    
    public void ensamblarMVCPrincipal(iDespachador despachador) throws UnknownHostException {

        //Tomar la ip del cliente ejecutando el MVC.
        String ipCliente = InetAddress.getLocalHost().toString();
        System.out.println("ip cliente: " + ipCliente);

        //Instanciar los elementos de el CU de Solicitar unirse a partida.
        
        
        ModeloConfig modeloConfiguracion = new ModeloConfig();
        modeloConfiguracion.setDespachador(despachador);
        ControladorConfig controladorConfiguracion = new ControladorConfig(modeloConfiguracion);
        ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfiguracion);
        modeloConfiguracion.añadirObservador(vistaConfig);

        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

        controlPrincipal.setControladorConfig(controladorConfiguracion);
        controladorConfiguracion.setControladorCUPrincipal(controlPrincipal);
        
        //Le paso la ip y puerto al control para que se lo pase a modelo
        controladorConfiguracion.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(),ipCliente);

        System.out.println("[Ensamblador] Iniciando aplicación...");
        vistaLobby.setVisible(true);
        vistaConfig.setVisible(false);

    }
    
    public void ensamblarMVC2(iDespachador despachador){
        //no se que hacer aqui a la verga
        
                ////
        
//        ModeloSala modeloSala = new ModeloSala();
//        modeloSala.setDespachador(despachador);
//        ControlSala controlSala = new ControlSala(modeloSala);
//        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);
//        modeloSala.añadirObservador(vistaSala);


    }
    

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
