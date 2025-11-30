/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package control;

import Ensambladores.EnsambladorCliente;
import contratos.Configuracion;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import java.io.IOException;
import java.net.InetAddress;
import modelo.ModeloSalaEspera;
import vista.VistaSalaEspera;

/**
 *
 * @author benja
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int miPuertoEscucha = 9003;
        String miId = "Host_Jugador1";

        System.out.println("Iniciando Sala de Espera...");

        ModeloSalaEspera modelo = new ModeloSalaEspera();
        modelo.setMiId(miId);
        ControlSalaEspera control = new ControlSalaEspera(modelo);
        VistaSalaEspera vista = new VistaSalaEspera(control);
        modelo.agregarObservador(vista);

        iEnsambladorCliente ensamblador = new EnsambladorCliente();

        iDespachador despachador = ensamblador.crearDespachador(Configuracion.getIpServidor(), Configuracion.getPuerto());
        modelo.setDespachador(despachador);

        iListener listener = ensamblador.crearListener(miId, modelo);

        new Thread(() -> {
            try {
                System.out.println("Sala de Espera escuchando en puerto " + miPuertoEscucha);
                listener.iniciar(miPuertoEscucha);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            String msgRegistro = miId + ":REGISTRAR:" + ip + "$" + miPuertoEscucha;
            despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), msgRegistro);
        } catch (Exception e) {
            e.printStackTrace();
        }

        vista.setVisible(true);
    }
}
