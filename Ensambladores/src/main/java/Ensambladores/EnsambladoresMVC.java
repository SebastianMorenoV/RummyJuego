package Ensambladores;

import Control.ControlCUPrincipal;
import Controlador.Controlador;
import Modelo.Modelo;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import Vista.VistaTablero;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlEjercerTurno;
import contratos.iDespachador;
import contratos.iListener;
import controlador.ControladorConfig;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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

    public void ensamblarMVCPrincipal(iDespachador despachador) throws UnknownHostException {

        //Tomar la ip del cliente ejecutando el MVC.
        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        System.out.println("ip cliente: " + ipCliente);
        String idCliente = "Jugador1";

        int puertoLocalEscucha = buscarPuertoLibre();

        Modelo modeloEjercerTurno = new Modelo();
        iControlEjercerTurno controlEjercerTurno = new Controlador(modeloEjercerTurno);
        VistaTablero vistaEjercerTurno = new VistaTablero(controlEjercerTurno);
        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.agregarObservador(vistaEjercerTurno);
        modeloEjercerTurno.setIdCliente(idCliente);

        ModeloConfig modeloConfiguracion = new ModeloConfig();
        modeloConfiguracion.setDespachador(despachador);
        ControladorConfig controladorConfiguracion = new ControladorConfig(modeloConfiguracion);
        ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfiguracion);
        modeloConfiguracion.añadirObservador(vistaConfig);
        modeloConfiguracion.setIdCliente(idCliente);

        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        modeloPrincipal.setDespachador(despachador);
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);
        modeloPrincipal.setIdCliente(idCliente);

        controlPrincipal.setControladorConfig(controladorConfiguracion);
        controladorConfiguracion.setControladorCUPrincipal(controlPrincipal);
        controlPrincipal.setControladorEjercerTurno(controlEjercerTurno);
        controlEjercerTurno.setControlConfiguracion(controladorConfiguracion);
        controlEjercerTurno.setControlPantallaPrincipal(controlPrincipal);
        
        //Le paso la ip y puerto al control para que se lo pase a modelo
        controladorConfiguracion.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);
        controlPrincipal.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);
        controlEjercerTurno.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);
        
        //INICIO EL LISTENER PARA LA APLICACION.
        List<PropertyChangeListener> escuchadores = new ArrayList<>();
        escuchadores.add(modeloConfiguracion);
        escuchadores.add(modeloPrincipal);
        escuchadores.add(modeloEjercerTurno); 

        EnsambladorCliente ensambladorRed = new EnsambladorCliente();
        iListener listener = ensambladorRed.crearListener(ipCliente, escuchadores);

        new Thread(() -> {
            try {
                listener.iniciar(puertoLocalEscucha);
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: "
                        + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        System.out.println("[Ensamblador] Iniciando aplicación...");
        vistaLobby.setVisible(true);
        vistaConfig.setVisible(false);

    }

    /**
     * Busca un puerto disponible en el sistema operativo. Abre un ServerSocket
     * en puerto 0 (el OS asigna uno libre), obtiene el número y lo cierra
     * inmediatamente para que pueda ser usado.
     *
     * @return int puerto libre
     */
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
