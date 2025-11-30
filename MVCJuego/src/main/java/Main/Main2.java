package Main;

import Controlador.Controlador;
import Modelo.Modelo;
import Vista.VistaTablero;
import contratos.iDespachador;
import contratos.iListener;
import contratos.iEnsambladorCliente;
import Ensambladores.EnsambladorCliente;
import Util.Configuracion;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Punto de entrada alterno para iniciar el cliente del juego. Configura MVC,
 * ensambla módulos de red y arranca la interfaz.
 *
 * @author Sebastian Moreno
 */
public class Main2 {

    public static void main(String[] args) {

        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);
        VistaTablero vistaJugador1 = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugador1);

        String miId = "Jugador2";
        int puertoServidor = 5000;
        int miPuertoDeEscucha = 9006;

        System.out.println("[Main] Iniciando ensamblaje de red...");

        iEnsambladorCliente ensamblador = new EnsambladorCliente();

        iDespachador despachador = ensamblador.crearDespachador(Configuracion.getIpServidor(), puertoServidor);

        iListener listener = ensamblador.crearListener(miId, modelo); 


        modelo.setDespachador(despachador);
        modelo.setMiId(miId);


        new Thread(() -> {
            try {
                System.out.println("[Main] Iniciando listener en el puerto "
                        + miPuertoDeEscucha);

                listener.iniciar(miPuertoDeEscucha); 
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: "
                        + e.getMessage());

                e.printStackTrace();
            }
        }).start();

        try {
            String ipCliente = InetAddress.getLocalHost().getHostAddress();
            String mensajeRegistro = miId
                    + ":REGISTRAR:"
                    + ipCliente
                    + "$"
                    + miPuertoDeEscucha;

            despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensajeRegistro);
        } catch (IOException ex) {
            System.err.println("[Main] No se pudo conectar con el servidor para registrarse: "
                    + ex.getMessage());
        }

        vistaJugador1.setVisible(true);

        controlador.iniciarJuego();
    }
}
