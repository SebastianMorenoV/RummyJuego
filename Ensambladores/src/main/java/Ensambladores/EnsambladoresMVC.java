package Ensambladores;

import Control.ControlCUPrincipal;
import Controlador.Controlador;
import Modelo.Modelo;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import Vista.VistaTablero;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import control.ControlSalaEspera;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ModeloSalaEspera;
import vista.VistaSalaEspera;

/**
 * Esta clase ensambla los mvcs necesarios en el sistema. por ultimo utiliza el
 * metodo run.
 *
 * @author Sebastian Moreno
 */
public class EnsambladoresMVC {

    public EnsambladoresMVC() {
        iEnsambladorCliente ensambladorCliente= new EnsambladorCliente();
        iDespachador despachador = ensambladorCliente.crearDespachador(Configuracion.getIpServidor(), Configuracion.getPuerto());
        try {
            ensamblarMVCPrincipal(despachador,ensambladorCliente);
        } catch (UnknownHostException ex) {
            Logger.getLogger(EnsambladoresMVC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  public void ensamblarMVCPrincipal(iDespachador despachador, iEnsambladorCliente ensamblador) throws UnknownHostException {
        
        // 1. Identidad
        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        String miId = "Jugador_" + System.currentTimeMillis(); 
        int miPuerto = 9000 + (int)(Math.random() * 1000); 

        System.out.println("=== INICIANDO CLIENTE [" + miId + "] ===");

        // 2. Construcción de MVCs
        
        // A) Sala de Espera
        ModeloSalaEspera modeloSala = new ModeloSalaEspera();
        modeloSala.setDatosRed(despachador, miId, Configuracion.getIpServidor(), Configuracion.getPuerto());
        ControlSalaEspera controlSala = new ControlSalaEspera(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala); 
        modeloSala.agregarObservador(vistaSala);

        // B) Lobby (Pantalla Inicial)
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        modeloPrincipal.setDatosRed(despachador, miId, Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, miPuerto);
        ControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

       //EJERCER TURNO
        Modelo modeloEjercerTurno= new Modelo();
        modeloEjercerTurno.setDatosRed(despachador, miId, Configuracion.getIpServidor(), Configuracion.getPuerto());
        Controlador controlEjercerTurno = new Controlador (modeloEjercerTurno);
        VistaTablero vistaTablero = new VistaTablero(controlEjercerTurno);
        modeloEjercerTurno.agregarObservador(vistaTablero);
        // 3. Inyección de Navegación
        
        controlPrincipal.setControladorSalaEspera(controlSala);
        controlSala.setEjercerTurno(controlEjercerTurno);
        PropertyChangeListener[] modelos= {modeloSala,modeloPrincipal,modeloEjercerTurno};
        iListener listener = ensamblador.crearListener(miId, modelos);

        // 5. Iniciar Listener
        new Thread(() -> {
            try {
                listener.iniciar(miPuerto);
            } catch (IOException ex) {
                Logger.getLogger(EnsambladoresMVC.class.getName()).log(Level.SEVERE, "Error red", ex);
            }
        }).start();

        // 6. Arrancar UI
        vistaLobby.setVisible(true);
        vistaSala.setVisible(false);
    }

    public static void main(String[] args) {
        new EnsambladoresMVC();
    }
}
