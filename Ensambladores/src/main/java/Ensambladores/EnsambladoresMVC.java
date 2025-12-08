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
import control.ControlSalaDeEspera;
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
import modelo.ModeloRegistro;
import modelo.ModeloSalaDeEspera;
import sockets.ClienteTCP;
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
        // 1. Identidad del Cliente
        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        System.out.println("ip cliente: " + ipCliente);
        // Generar ID temporal(luego se asociará al nombre en el Blackboard)
        String idAleatorio = UUID.randomUUID().toString().substring(0, 5);
        String idCliente = "Jugador" + idAleatorio; // <- cambiar Jugador_ por Jugador1 o Jugador2

        int puertoLocalEscucha = buscarPuertoLibre();

        // ENSAMBLAJE: MVC EJERCER TURNO --------
        Modelo modeloEjercerTurno = new Modelo();
        iControlEjercerTurno controlEjercerTurno = new Controlador(modeloEjercerTurno);
        VistaTablero vistaEjercerTurno = new VistaTablero(controlEjercerTurno);

        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.agregarObservador(vistaEjercerTurno);
        modeloEjercerTurno.setIdCliente(idCliente); // El juego necesita saber quién soy

        //ENSAMBLAJE: MVC PANTALLA PRINCIPAL LOBBY ------------
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        modeloPrincipal.setDespachador(despachador);
        modeloPrincipal.setIdCliente(idCliente);

        ControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);

        modeloPrincipal.añadirObservador(vistaLobby);

        // ENSAMBLAJE: MVC REGISTRAR USUARIO -----------
        ModeloRegistro modeloRegistro = new ModeloRegistro();
        ControladorRegistro controladorRegistro = new ControladorRegistro(modeloRegistro);

        controlPrincipal.setControladorRegistro(controladorRegistro);
        RegistrarUsuario vistaRegistro = new RegistrarUsuario(controladorRegistro);

        controladorRegistro.setVista(vistaRegistro);

        // Inyecciones para Registro --
        System.out.println("IP CONFIGURADA: " + Configuracion.getIpServidor()); //para ver hasta donde llegamos antes de un error

        // Configuracion de registro
        modeloRegistro.setDespachador(despachador);
        modeloRegistro.setIdCliente(idCliente);
        modeloRegistro.agregarObservador(vistaRegistro);
        modeloRegistro.agregarObservador(controladorRegistro);

        // NAVEGACIÓN REGISTRO: Cuando el registro sea exitoso, el controlador llamará a controlPrincipal ---------
        controladorRegistro.setNavegacion(controlPrincipal);

        controlPrincipal.setControladorRegistro(controladorRegistro);
        controlPrincipal.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);
        controladorRegistro.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente);

        // ENSAMBLAJE: MVC SALA DE ESPERA ------
        IModeloSalaDeEspera modeloSalaDeEspera = new ModeloSalaDeEspera();
        ModeloSalaDeEspera modeloSala = new ModeloSalaDeEspera();
        ControlSalaDeEspera controlSala = new ControlSalaDeEspera(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);

        modeloSala.agregarObservador(vistaSala);
        modeloSala.setIdCliente(idCliente);
        
        // Inyectar red a la sala para que pueda enviar el VOTO
        controlSala.setConfiguracionRed(Configuracion.getIpServidor(), Configuracion.getPuerto(), idCliente, despachador);

        // El Lobby necesita saber del Registro para abrirlo cuando des clic en "Crear Partida"
        controlPrincipal.setControladorRegistro(controladorRegistro);

        // 5. CONEXIONES CRUZADAS Y CONFIGURACIÓN FINAL
        controlPrincipal.setControladorEjercerTurno(controlEjercerTurno);
        controlEjercerTurno.setControlPantallaPrincipal(controlPrincipal);
        controlEjercerTurno.setControlSalaEspera(controlSala);
        controlPrincipal.setControlSalaEspera(controlSala);
        
        // Configuraciones de Red del Principal y Juego
        controlEjercerTurno.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);

        // LISTENER Y ARRANQUE (Receptor de Mensajes)
        List<PropertyChangeListener> escuchadores = new ArrayList<>();
        escuchadores.add(modeloPrincipal);
        escuchadores.add(modeloEjercerTurno);
        escuchadores.add(modeloRegistro);
        escuchadores.add(modeloSala);

        EnsambladorCliente ensambladorRed = new EnsambladorCliente();
        iListener listener = ensambladorRed.crearListener(idCliente, escuchadores);

        // Iniciar hilo de escucha
        new Thread(() -> {
            try {
                listener.iniciar(puertoLocalEscucha);
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // 7. Arranque de la Aplicación
        System.out.println("[Ensamblador] Arrancando aplicación para: " + idCliente);

        // FLUJO DE INICIO
        vistaLobby.setVisible(true);
        vistaRegistro.setVisible(false);
        vistaSala.setVisible(false);

        controlPrincipal.iniciarCU();
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

    public RegistrarUsuario ensamblarMVCRegistro(iDespachador despachador, String ipCliente, iControlCUPrincipal controlPrincipal, int puertoEscuchaCliente) {
        System.out.println("[Ensamblador] Ensamblando MVC Registro...");

        ModeloRegistro modeloRegistro = new ModeloRegistro();
        modeloRegistro.setDespachador(despachador);

        ControladorRegistro controladorRegistro = new ControladorRegistro(modeloRegistro);

        controladorRegistro.setConfiguracion(
                Configuracion.getIpServidor(), // IP Servidor
                puertoEscuchaCliente, // Puerto Cliente (Payload)
                ipCliente // IP Cliente
        );

        if (controlPrincipal != null) {
            controlPrincipal.setControladorRegistro(controladorRegistro);
        }

        RegistrarUsuario vistaRegistro = new RegistrarUsuario(controladorRegistro);
        modeloRegistro.agregarObservador(vistaRegistro);

        return vistaRegistro;
    }

    public static void main(String[] args) {
        EnsambladoresMVC e = new EnsambladoresMVC();
    }
}
