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
import contratos.controladoresMVC.iControlRegistro;
import contratos.controladoresMVC.iControlSalaEspera;
import contratos.iDespachador;
import contratos.iListener;
import control.ControlSalaDeEspera;
import controlador.ControladorConfig;
import controlador.ControladorRegistro;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.IModeloSalaDeEspera;
import modelo.ModeloConfig;
import modelo.ModeloRegistro;
import modelo.ModeloSalaDeEspera;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;
import vista.RegistrarUsuario;
import vista.VistaSalaEspera;

/**
 * Clase organizadora que ensambla todos los módulos MVC del sistema.
 * Estructurada por bloques lógicos para facilitar la lectura.
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

        // 1. PREPARACIÓN DE DATOS DE RED Y CLIENTE
        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        String idAleatorio = UUID.randomUUID().toString().substring(0, 5);
        String idCliente = "Jugador_" + idAleatorio;
        int puertoLocalEscucha = buscarPuertoLibre();
        String ipServidor = Configuracion.getIpServidor();
        int puertoServidor = Configuracion.getPuerto();

        System.out.println("[Ensamblador] Iniciando ensamblaje para: " + idCliente + " en " + ipCliente + ":" + puertoLocalEscucha);

        // 2. CREACIÓN DE INSTANCIAS (MODELO - CONTROLADOR - VISTA) -----------------------

        // --- Módulo Juego (Tablero) ---
        Modelo modeloEjercerTurno = new Modelo();
        iControlEjercerTurno controlEjercerTurno = new Controlador(modeloEjercerTurno);
        VistaTablero vistaEjercerTurno = new VistaTablero(controlEjercerTurno);

        // --- Módulo Configuración ---
        ModeloConfig modeloConfiguracion = new ModeloConfig();
        ControladorConfig controladorConfiguracion = new ControladorConfig(modeloConfiguracion);
        ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfiguracion);

        // --- Módulo Lobby (Pantalla Principal) ---
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);

        // --- Módulo Registro ---
        ModeloRegistro modeloRegistro = new ModeloRegistro();
        iControlRegistro controladorRegistro = new ControladorRegistro(modeloRegistro);
        RegistrarUsuario vistaRegistro = new RegistrarUsuario(controladorRegistro);

        // --- Módulo Sala de Espera ---
        ModeloSalaDeEspera modeloSala = new ModeloSalaDeEspera(); // Usamos la implementación concreta para poder agregarla al listener
        iControlSalaEspera controlSala = new ControlSalaDeEspera(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);

        // 3. CONFIGURACIÓN BÁSICA DE MÓDULOS (Observers, IDs, Despachadores) -------------

        // Configuración Juego
        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.setIdCliente(idCliente);
        modeloEjercerTurno.agregarObservador(vistaEjercerTurno);

        // Configuración Configurar Partida
        modeloConfiguracion.setDespachador(despachador);
        modeloConfiguracion.setIdCliente(idCliente);
        modeloConfiguracion.añadirObservador(vistaConfig);

        // Configuración Lobby
        modeloPrincipal.setDespachador(despachador);
        modeloPrincipal.setIdCliente(idCliente);
        modeloPrincipal.añadirObservador(vistaLobby);

        // Configuración Registro
        modeloRegistro.setDespachador(despachador);
        modeloRegistro.setIdCliente(idCliente);
        modeloRegistro.agregarObservador(vistaRegistro);
        ((ControladorRegistro) controladorRegistro).setVista(vistaRegistro);

        // Configuración Sala de Espera
        modeloSala.setIdCliente(idCliente);
        modeloSala.agregarObservador(vistaSala);
        // Inyectamos red a la sala para acciones como "Estoy Listo"
        controlSala.setConfiguracionRed(ipServidor, puertoServidor, idCliente, despachador);

        // 4. INYECCIÓN DE DEPENDENCIAS CRUZADAS (NAVEGACIÓN) -----------------------------

        // El Lobby conoce a todos para navegar hacia ellos
        controlPrincipal.setControladorRegistro(controladorRegistro);
        controlPrincipal.setControladorConfig(controladorConfiguracion);
        controlPrincipal.setControladorEjercerTurno(controlEjercerTurno);
        controlPrincipal.setControlSalaEspera(controlSala);

        // El Registro necesita volver al Lobby (o avanzar)
        controladorRegistro.setNavegacion(controlPrincipal);

        // Configuración necesita volver al Lobby
        controladorConfiguracion.setControladorCUPrincipal(controlPrincipal);

        // Juego necesita volver al Lobby o Configuración
        controlEjercerTurno.setControlPantallaPrincipal(controlPrincipal);
        controlEjercerTurno.setControlConfiguracion(controladorConfiguracion);
        controlEjercerTurno.setControlSalaEspera(controlSala);

        // 5. CONFIGURACIÓN DE RED EN CONTROLADORES ---------------------------------------
        // Se hace al final para asegurar consistencia
        controlPrincipal.setConfiguracion(ipServidor, puertoServidor, ipCliente, puertoLocalEscucha);
        controladorRegistro.setConfiguracion(ipServidor, puertoServidor, ipCliente);
        controladorConfiguracion.setConfiguracion(ipServidor, puertoServidor, ipCliente, puertoLocalEscucha);
        controlEjercerTurno.setConfiguracion(ipServidor, puertoServidor, ipCliente, puertoLocalEscucha);

        // 6. INICIO DEL LISTENER (RED) ---------------------------------------------------
        List<PropertyChangeListener> escuchadores = new ArrayList<>();
        
        // Agregamos los modelos que deben escuchar eventos del servidor
        escuchadores.add(modeloConfiguracion);
        escuchadores.add(modeloPrincipal);
        escuchadores.add(modeloEjercerTurno);
        escuchadores.add(modeloRegistro); // Para recibir confirmación de registro/nombre repetido
        
        // ¡IMPORTANTE! Agregamos la Sala de Espera para que se actualice la lista de jugadores
        escuchadores.add(modeloSala); 

        EnsambladorCliente ensambladorRed = new EnsambladorCliente();
        iListener listener = ensambladorRed.crearListener(ipCliente, escuchadores);

        new Thread(() -> {
            try {
                System.out.println("[Listener] Iniciando escucha en puerto: " + puertoLocalEscucha);
                listener.iniciar(puertoLocalEscucha);
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // 7. ARRANQUE DE LA INTERFAZ -----------------------------------------------------
        System.out.println("[Ensamblador] UI Lista. Abriendo Lobby...");

        vistaLobby.setVisible(true);
        vistaRegistro.setVisible(false);
        vistaSala.setVisible(false);
        vistaConfig.setVisible(false);

        controlPrincipal.iniciarCU();
    }

    /**
     * Busca un puerto disponible en el sistema operativo.
     *
     * @return int puerto libre
     */
    private int buscarPuertoLibre() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            System.err.println("No se pudo encontrar un puerto libre, usando por defecto 9999");
            return 9999;
        }
    }

    // Este método ya no es estrictamente necesario si todo se hace arriba, 
    // pero lo dejo por si lo usas en otro lado.
    public RegistrarUsuario ensamblarMVCRegistro(iDespachador despachador, String ipCliente, iControlCUPrincipal controlPrincipal, int puertoEscuchaCliente) {
        // ... (código existente sin cambios)
        return null; 
    }

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}