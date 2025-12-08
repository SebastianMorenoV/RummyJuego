
package Ensambladores;

import Control.ControlCUPrincipal;
import Controlador.Controlador;
import Modelo.Modelo;
import Modelo.ModeloCUPrincipal;
import Vista.VistaLobby;
import Vista.VistaTablero;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSolicitarInicio;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import contratos.vistasMVC.IVistaJuego;
import control.ControlSala;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ModeloSala;
import sockets.ClienteTCP;
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

        String miId = "Gael Guerra";
        int miPuertoDeEscucha = buscarPuertoLibre();

        String ipCliente = InetAddress.getLocalHost().getHostAddress();

        iEnsambladorCliente ensambladorCliente = new EnsambladorCliente();

        ModeloSala modeliSalaEspera = new ModeloSala();

        modeliSalaEspera.setDespachador(despachador);
        modeliSalaEspera.setMiId(miId);
        modeliSalaEspera.setEnsambladorCliente(ensambladorCliente);
        modeliSalaEspera.setMiPuertoDeEscucha(miPuertoDeEscucha);

        ControlSala controlSalaEspera = new ControlSala(modeliSalaEspera);
        VistaSalaEspera vistaSalaEspera = new VistaSalaEspera(controlSalaEspera);
        controlSalaEspera.setVista(vistaSalaEspera);
        
        modeliSalaEspera.añadirObservador(vistaSalaEspera);

        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

        Modelo modeloEjercerTurno = new Modelo();
        Controlador controlador = new Controlador(modeloEjercerTurno);

        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.setMiId(miId);
        
        List<PropertyChangeListener> escuchadores = new ArrayList<>();
        escuchadores.add(modeloEjercerTurno);
        escuchadores.add(modeliSalaEspera);

        EnsambladorCliente ensambladorRed = new EnsambladorCliente();
        iListener listener = ensambladorRed.crearListener(ipCliente, escuchadores);

        new Thread(() -> {
            try {
                listener.iniciar(miPuertoDeEscucha);
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: "
                        + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        VistaTablero vistaTablero = new VistaTablero(controlador);

        IVistaJuego vistaJuego = vistaTablero;

        modeloEjercerTurno.agregarObservador(vistaTablero); 
        
        vistaSalaEspera.setLanzador(() -> {
        controlPrincipal.casoUsoIniciarPartida();
    });

        ((ControlCUPrincipal) controlPrincipal).setControladorSalaEspera((iControlSolicitarInicio) controlSalaEspera);
        ((ControlCUPrincipal) controlPrincipal).setControladorJuego(controlador);
        controlador.setControlSalaEspera(controlSalaEspera);

        ((ControlCUPrincipal) controlPrincipal).setVistaTableroJuego(vistaJuego);

        controlSalaEspera.setControladorCUPrincipal(controlPrincipal);

        //modeliSalaEspera.iniciarConexionRed();

        System.out.println("[Ensamblador] Iniciando aplicación para ID: " + miId);
        vistaLobby.setVisible(true);
        vistaSalaEspera.setVisible(false);
        vistaTablero.setVisible(false); 
        
    }
   
    
    private int buscarPuertoLibre() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            System.err.println("No se pudo encontrar un puerto libre, usando por defecto 9999");
            return 9999; // Fallback en caso de error extremo
        }
    }
    

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
