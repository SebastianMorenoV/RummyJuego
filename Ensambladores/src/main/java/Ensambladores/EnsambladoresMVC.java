
package Ensambladores;

import Control.ControlCUPrincipal;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iLanzadorJuego;
import contratos.iDespachador;
import contratos.iListener;
import control.ControlSala;
import controlador.ControladorConfig;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ModeloConfig;
import modelo.ModeloSala;
import procesadores.Procesador;
import sockets.ClienteTCP;
import sockets.ServerTCP;
import vista.ConfigurarPartida;
import vista.VistaSalaEspera;

/**
 * Esta clase ensambla los mvcs necesarios en el sistema. por ultimo utiliza el
 * metodo run.
 *
 * @author Sebastian Moreno
 */
public class EnsambladoresMVC {

    iDespachador despachador;

//    public EnsambladoresMVC() {
//        despachador = new ClienteTCP();
//        try {
//            ensamblarMVCPrincipal(despachador);
//        } catch (UnknownHostException ex) {
//            Logger.getLogger(EnsambladoresMVC.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    //Constructor para CU solicitar inicio(no se abre el lobby)
    public EnsambladoresMVC() {
        despachador = new ClienteTCP();
    }

    
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
    
    public void ensamblarMVCSala(iDespachador despachador) throws UnknownHostException{

        String ipCliente = InetAddress.getLocalHost().toString();
        System.out.println("ip cliente: " + ipCliente);
       
        ModeloSala modeloSala = new ModeloSala();
        modeloSala.setDespachador(despachador);
        ControlSala controlSala = new ControlSala(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);
        modeloSala.añadirObservador(vistaSala);
        
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);
        
        controlPrincipal.setControladorSalaEspera(controlSala);
        controlSala.setControladorCUPrincipal(controlPrincipal);
        
        controlSala.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(),ipCliente);
        
        System.out.println("[Ensamblador] Iniciando aplicación...");
        vistaLobby.setVisible(false);
        vistaSala.setVisible(true);


    }
    
    //ENSAMBLAR EL JUEGO CON LA SALA DE ESPERA
    //(no se puede depender de MVCJuego por dependencia ciclica)
    public void ensamblarJuegoCompleto(
            iDespachador despachador, 
            PropertyChangeListener modeloJuego, 
            iLanzadorJuego accionAbrirJuego,
            String miId,      
            int puertoLocal   
    ) throws UnknownHostException {

        String ipCliente = InetAddress.getLocalHost().getHostAddress();

        Procesador procesadorRed = new Procesador(); 
        procesadorRed.addPropertyChangeListener(modeloJuego);

        iListener listener = new ServerTCP(procesadorRed);
        new Thread(() -> {
            try { 
                System.out.println("[Red] Escuchando en puerto: " + puertoLocal);
                listener.iniciar(puertoLocal);
            } 
            catch (IOException e) { e.printStackTrace(); }
        }).start();

        ModeloSala modeloSala = new ModeloSala();
        modeloSala.setDespachador(despachador);

        modeloSala.setMiId(miId); 
        modeloSala.setIpCliente(ipCliente);

        procesadorRed.addPropertyChangeListener(modeloSala); 

        ControlSala controlSala = new ControlSala(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);
        modeloSala.añadirObservador(vistaSala);

        vistaSala.setLanzador(accionAbrirJuego);

        try {
             String msg = miId + ":REGISTRAR:" + ipCliente + "$" + puertoLocal;
             despachador.enviar(Util.Configuracion.getIpServidor(), Util.Configuracion.getPuerto(), msg);
        } catch (Exception e) { e.printStackTrace(); }

        vistaSala.setVisible(true);
    }
    

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
