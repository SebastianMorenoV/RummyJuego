package Ensambladores;

import Controlador.Controlador;
import Modelo.Modelo;
import Vista.VistaTablero;
import Vista.TipoEvento;
import Dtos.ActualizacionDTO;
import Vista.Observador;
import Modelo.IModelo;
import modelo.ModeloRegistro;
import controlador.ControladorRegistro;
import vista.RegistrarUsuario;
import Util.Configuracion;
import Vista.Observador;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.iDespachador;
import contratos.iListener;
import sockets.ClienteTCP;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

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
        //Tomar la ip del cliente
        String ipCliente = InetAddress.getLocalHost().getHostAddress();
        int puertoCliente = 9090;
        System.out.println("[Ensamblador] IP Cliente: " + ipCliente);
        System.out.println("[Ensamblador] Puerto Cliente (Escucha): " + puertoCliente);

        //Ensamblar MVC JUEGO
        System.out.println("[Ensamblador] Preparando MVC Juego (Oculto)...");
        Modelo modeloJuego = new Modelo();
        modeloJuego.setMiId(ipCliente);
        modeloJuego.setDespachador(despachador);

        Controlador controladorJuego = new Controlador(modeloJuego);
        VistaTablero vistaTablero = new VistaTablero(controladorJuego);

        vistaTablero.setVisible(false);

        // Conectamos el observador natural del juego
        modeloJuego.agregarObservador(vistaTablero);

        // Listener del Client
        EnsambladorCliente fabricaCliente = new EnsambladorCliente();
        iListener listener = fabricaCliente.crearListener("ClienteMain", modeloJuego);

        Thread hiloListener = new Thread(() -> {
            try {
                System.out.println("[Ensamblador] Iniciando escucha en puerto " + puertoCliente + "...");
                listener.iniciar(puertoCliente);
            } catch (IOException e) {
                System.err.println("[Ensamblador] Error CRÍTICO en Listener: " + e.getMessage());
            }
        });
        hiloListener.start();

        // Ensamblar MVC REGISTRO
        System.out.println("[Ensamblador] Iniciando MVC Registro...");
        RegistrarUsuario vistaRegistro = ensamblarMVCRegistro(despachador, ipCliente, null, puertoCliente);

        // Lógica de Navegación MOCK
        modeloJuego.agregarObservador(new Observador() {
            @Override
            public void actualiza(IModelo modelo, ActualizacionDTO actualizacion) {

                // Si recibimos fichas (Mano inicial) o el juego arranca...
                if (actualizacion.getTipoEvento() == TipoEvento.REPINTAR_MANO
                        || actualizacion.getTipoEvento() == TipoEvento.INCIALIZAR_FICHAS
                        || actualizacion.getTipoEvento() == TipoEvento.TOMO_FICHA) {

                    if (vistaRegistro.isVisible()) {
                        System.out.println("[Navegacion Mock] ¡Datos recibidos! Cambiando a Tablero.");
                        vistaRegistro.setVisible(false); // Cierra Registro
                        vistaTablero.setVisible(true);   // Abre Tablero
                    }
                }
            }
        });

        // Arrancar
        vistaTablero.setVisible(false);
        vistaRegistro.setVisible(true);

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
