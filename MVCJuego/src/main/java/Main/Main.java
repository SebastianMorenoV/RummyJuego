package Main;

import Controlador.Controlador;
import Modelo.Modelo;
import Vista.VistaTablero;
import contratos.iDespachador;
import contratos.iListener;
import contratos.iEnsambladorCliente;
import Ensambladores.EnsambladorCliente;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Punto de entrada de la aplicación. Configura MVC, conexión en red y arranca
 * el juego/local listener.
 *
 * @author chris
 */
public class Main {

    public static void main(String[] args) {
        // 1. Creación de Componentes MVC
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);
        VistaTablero vistaJugador1 = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugador1);

        // 2. Configuración de Red
        String miId = "Jugador1";
        String ipServidor = "192.168.100.98";
        int puertoServidor = 5000;
        int miPuertoDeEscucha = 9005;

        iEnsambladorCliente ensamblador = new EnsambladorCliente();
        iDespachador despachador = ensamblador.crearDespachador(ipServidor, puertoServidor);
        iListener listener = ensamblador.crearListener(miId, modelo);

        modelo.setDespachador(despachador);
        modelo.setMiId(miId);

        new Thread(() -> {
            try {
                listener.iniciar(miPuertoDeEscucha);
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: "
                        + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        //Registrarse en el Servidor e Iniciar el Juego
        try {

            String ipCliente = InetAddress.getLocalHost().getHostAddress();
            String mensajeRegistro = miId + ":REGISTRAR:" + ipCliente + "$" + miPuertoDeEscucha;

            despachador.enviar(mensajeRegistro);
        } catch (IOException ex) {
            System.err.println("[Main] No se pudo conectar con el servidor para registrarse: "
                    + ex.getMessage());
        }

        vistaJugador1.setVisible(true);
        controlador.iniciarJuego();
    }
}
