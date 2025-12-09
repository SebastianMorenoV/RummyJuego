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

import contratos.controladoresMVC.iControlRegistro;
import contratos.controladoresMVC.iControlSalaEspera;
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
import modelo.ModeloConfig;
import modelo.ModeloRegistro;
import modelo.ModeloSalaDeEspera;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;
import vista.RegistrarUsuario;
import vista.VistaSalaEspera;

/**
 * Esta clase ensambla los mvcs necesarios en el sistema atraves de inyeccion de dependencias.
 * conociendo a las clases especificas, para evitar referencias circulares entre proyectos.
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



        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        System.out.println("ip cliente: " + ipCliente);
        String idAleatorio = UUID.randomUUID().toString().substring(0, 5);
        String idCliente = "Jugador_" + idAleatorio;

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

        controlEjercerTurno.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), idCliente, puertoLocalEscucha);
        modeloEjercerTurno.setDespachador(despachador);
        modeloEjercerTurno.agregarObservador(vistaEjercerTurno);
        modeloEjercerTurno.setIdCliente(idCliente); // El juego necesita saber quién soy

        //ENSAMBLAJE: MVC PANTALLA PRINCIPAL LOBBY ------------
        ModeloCUPrincipal modeloPrincipal = new ModeloCUPrincipal();
        modeloPrincipal.setDespachador(despachador);
        iControlCUPrincipal controlPrincipal = new ControlCUPrincipal(modeloPrincipal);
        modeloPrincipal.setIdCliente(idCliente);

        VistaLobby vistaLobby = new VistaLobby(controlPrincipal);
        modeloPrincipal.añadirObservador(vistaLobby);
        modeloPrincipal.setIdCliente(idCliente);

        ModeloRegistro modeloRegistro = new ModeloRegistro();
        iControlRegistro controladorRegistro = new ControladorRegistro(modeloRegistro);
        controlPrincipal.setControladorRegistro(controladorRegistro);
        RegistrarUsuario vistaRegistro = new RegistrarUsuario(controladorRegistro);

        // Configuracion de registro
        modeloRegistro.setDespachador(despachador);
        modeloRegistro.setIdCliente(idCliente);
        modeloRegistro.agregarObservador(vistaRegistro);
        
          // ENSAMBLAJE: MVC SALA DE ESPERA ------
        IModeloSalaDeEspera modeloSalaDeEspera = new ModeloSalaDeEspera();
        ModeloSalaDeEspera modeloSala = new ModeloSalaDeEspera();
        iControlSalaEspera controlSala = new ControlSalaDeEspera(modeloSala);
        VistaSalaEspera vistaSala = new VistaSalaEspera(controlSala);
 
        modeloSala.agregarObservador(vistaSala);
        modeloSala.setIdCliente(idCliente);

        // Inyectar red a la sala para que pueda enviar el VOTO
        controlSala.setConfiguracionRed(Configuracion.getIpServidor(), Configuracion.getPuerto(), idCliente, despachador);

        // El Lobby necesita saber del Registro para abrirlo cuando des clic en "Crear Partida"
        controlPrincipal.setControladorRegistro(controladorRegistro);

        // NAVEGACIÓN REGISTRO: Cuando el registro sea exitoso, el controlador llamará a controlPrincipal ---------
        controladorRegistro.setNavegacion(controlPrincipal);

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

       


       

      
        // Inyecciones para Registro --
        System.out.println("IP CONFIGURADA: " + Configuracion.getIpServidor()); //para ver hasta donde llegamos antes de un error

        

        controlPrincipal.setControladorRegistro(controladorRegistro);
        controlPrincipal.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente, puertoLocalEscucha);
        controladorRegistro.setConfiguracion(Configuracion.getIpServidor(), Configuracion.getPuerto(), ipCliente);

      

        // 5. CONEXIONES CRUZADAS Y CONFIGURACIÓN FINAL
        controlPrincipal.setControladorEjercerTurno(controlEjercerTurno);
        controlEjercerTurno.setControlPantallaPrincipal(controlPrincipal);
        controlEjercerTurno.setControlSalaEspera(controlSala);
        controlPrincipal.setControlSalaEspera(controlSala);

       

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

            return 9999;
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
