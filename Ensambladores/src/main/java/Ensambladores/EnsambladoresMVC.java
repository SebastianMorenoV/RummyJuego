
package Ensambladores;

import Control.ControlCUPrincipal;
import Controlador.Controlador;
import Modelo.Modelo;
import Modelo.ModeloCUPrincipal;
import Util.Configuracion;
import Vista.VistaLobby;
import Vista.VistaTablero;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSolicitarInicio;
import contratos.controladoresMVC.iLanzadorJuego;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import contratos.vistasMVC.IVistaJuego;
import control.ControlSala;
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
import modelo.IModeloSala;
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

    public EnsambladoresMVC() {
        despachador = new ClienteTCP();
        try {
            ensamblarMVCPrincipal(despachador);
        } catch (UnknownHostException ex) {
            Logger.getLogger(EnsambladoresMVC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //de luciano
    public void ensamblarMVCPrincipal(iDespachador despachador) throws UnknownHostException {

        String miId = "Jugador1";
        int miPuertoDeEscucha = buscarPuertoLibre();

        String ipCliente = InetAddress.getLocalHost().getHostAddress();

        // ** Dependencias Globales **
        iEnsambladorCliente ensambladorCliente = new EnsambladorCliente();

        // 2. Ensamblar MVC Sala de Espera (Componente a Sincronizar)
        // Declaramos con la interfaz (IModeloSalaEspera) y la inicializamos con la clase concreta.
        ModeloSala modeliSalaEspera = new ModeloSala();

        // 2.1. INYECCIÓN DE RECURSOS NECESARIOS PARA EL MODELO
        modeliSalaEspera.setDespachador(despachador);
        modeliSalaEspera.setMiId(miId);
        modeliSalaEspera.setEnsambladorCliente(ensambladorCliente);
        modeliSalaEspera.setMiPuertoDeEscucha(miPuertoDeEscucha);

        // Se usa la interfaz IModeloSalaEspera en el constructor de ControlSalaEspera
        ControlSala controlSalaEspera = new ControlSala(modeliSalaEspera);
        VistaSalaEspera vistaSalaEspera = new VistaSalaEspera(controlSalaEspera);
        modeliSalaEspera.añadirObservador(vistaSalaEspera);

        // 3. Ensamblar CU Principal (Lobby)
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

        // 4. Ensamblar CU Ejercer Turno (MVCJuego)
        Modelo modeloEjercerTurno = new Modelo();
        Controlador controlador = new Controlador(modeloEjercerTurno);

        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.setMiId(miId);
        
        List<PropertyChangeListener> escuchadores = new ArrayList<>();
//        escuchadores.add(modeloPrincipal);
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

        // CORRECCIÓN CLAVE: Creamos UNA SOLA instancia de VistaTablero
        VistaTablero vistaTablero = new VistaTablero(controlador);

        // La variable de interfaz apunta a la única instancia creada. 
        // Esto funciona porque VistaTablero implementa IVistaJuego.
        IVistaJuego vistaJuego = vistaTablero;

        modeloEjercerTurno.agregarObservador(vistaTablero); // La instancia concreta escucha al Modelo

        // 4. Inyectar dependencias de navegación
        // El orquestador necesita los controladores y la vista de juego
        ((ControlCUPrincipal) controlPrincipal).setControladorSalaEspera((iControlSolicitarInicio) controlSalaEspera);
        ((ControlCUPrincipal) controlPrincipal).setControladorJuego(controlador);
        controlador.setControlSalaEspera(controlSalaEspera);

        // Inyectamos la única instancia de la vista de juego (como interfaz)
        ((ControlCUPrincipal) controlPrincipal).setVistaTableroJuego(vistaJuego);

        // Inyectar el modelo de sala al modelo de juego para delegación de eventos de red
//        modelo.setModeloSalaEspera(modeliSalaEspera);
        // Inyectar el orquestador en el controlador de Sala de Espera para la navegación final
        controlSalaEspera.setControladorCUPrincipal(controlPrincipal);

        // 5. ** ORDENAR AL MODELO QUE INICIE SU RED ** (Dispara la lógica de hilos/sockets dentro del Modelo)
        modeliSalaEspera.iniciarConexionRed();

        System.out.println("[Ensamblador] Iniciando aplicación para ID: " + miId);
        vistaLobby.setVisible(true);
        vistaSalaEspera.setVisible(false);
        vistaTablero.setVisible(false); // Controlamos la visibilidad de la única instancia
    }
   
    // EN Ensambladores/EnsambladoresMVC.java

    
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
