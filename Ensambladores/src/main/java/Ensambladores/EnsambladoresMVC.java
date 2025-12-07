package Ensambladores;

import Control.ControlCUPrincipal;
import Controlador.Controlador;
import Modelo.Modelo;
import Modelo.ModeloCUPrincipal;
import Vista.VistaLobby;
import Vista.VistaTablero;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlSalaEspera;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import control.ControlSalaEspera;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sockets.ClienteTCP;

// Importaciones de Interfaces de los módulos inferiores (la clave del desacoplamiento)
import contratos.modelosMVC.IModeloSalaEspera;
import contratos.vistasMVC.IVistaJuego;
import modelo.ModeloSalaEspera;
import vista.VistaSalaEspera;

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

        // 🚨 CRUCIAL: CAMBIAR ESTA LÍNEA MANUALMENTE PARA CADA CLIENTE (Jugador1, Jugador2, etc.)
        String miId = "Jugador4";
        int miPuertoDeEscucha = 9008;

        String ipCliente = InetAddress.getLocalHost().getHostAddress();

        // ** Dependencias Globales **
        iEnsambladorCliente ensambladorCliente = new EnsambladorCliente();

        // 2. Ensamblar MVC Sala de Espera (Componente a Sincronizar)
        // Declaramos con la interfaz (IModeloSalaEspera) y la inicializamos con la clase concreta.
        IModeloSalaEspera modeliSalaEspera = new ModeloSalaEspera();

        // 2.1. INYECCIÓN DE RECURSOS NECESARIOS PARA EL MODELO
        modeliSalaEspera.setDespachador(despachador);
        modeliSalaEspera.setMiId(miId);
        modeliSalaEspera.setEnsambladorCliente(ensambladorCliente);
        modeliSalaEspera.setMiPuertoDeEscucha(miPuertoDeEscucha);

        // Se usa la interfaz IModeloSalaEspera en el constructor de ControlSalaEspera
        ControlSalaEspera controlSalaEspera = new ControlSalaEspera(modeliSalaEspera);
        VistaSalaEspera vistaSalaEspera = new VistaSalaEspera(controlSalaEspera);
        modeliSalaEspera.agregarObservador(vistaSalaEspera);

        // 3. Ensamblar CU Principal (Lobby)
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);

        // 4. Ensamblar CU Ejercer Turno (MVCJuego)
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);
        
        modelo.setDespachador(despachador);
        modelo.setMiId(miId);

        // CORRECCIÓN CLAVE: Creamos UNA SOLA instancia de VistaTablero
        VistaTablero vistaTablero = new VistaTablero(controlador);

        // La variable de interfaz apunta a la única instancia creada. 
        // Esto funciona porque VistaTablero implementa IVistaJuego.
        IVistaJuego vistaJuego = vistaTablero;

        modelo.agregarObservador(vistaTablero); // La instancia concreta escucha al Modelo

        // 4. Inyectar dependencias de navegación
        // El orquestador necesita los controladores y la vista de juego
        ((ControlCUPrincipal) controlPrincipal).setControladorSalaEspera((iControlSalaEspera) controlSalaEspera);
        ((ControlCUPrincipal) controlPrincipal).setControladorJuego(controlador);

        // Inyectamos la única instancia de la vista de juego (como interfaz)
        ((ControlCUPrincipal) controlPrincipal).setVistaTableroJuego(vistaJuego);

        // Inyectar el modelo de sala al modelo de juego para delegación de eventos de red
        modelo.setModeloSalaEspera(modeliSalaEspera);

        // Inyectar el orquestador en el controlador de Sala de Espera para la navegación final
        controlSalaEspera.setControlCUPrincipal(controlPrincipal);

        // 5. ** ORDENAR AL MODELO QUE INICIE SU RED ** (Dispara la lógica de hilos/sockets dentro del Modelo)
        modeliSalaEspera.iniciarConexionRed();

        System.out.println("[Ensamblador] Iniciando aplicación para ID: " + miId);
        vistaLobby.setVisible(true);
        vistaSalaEspera.setVisible(false);
        vistaTablero.setVisible(false); // Controlamos la visibilidad de la única instancia
    }

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
