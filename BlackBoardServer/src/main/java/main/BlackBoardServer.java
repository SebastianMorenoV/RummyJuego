package main;

import control.ControladorBlackboard;
import directorio.Directorio;
import pizarra.EstadoJuegoPizarra;
import com.mycompany.tcpejemplo.DespachadorAsincrono;
import Ensambladores.EnsambladorServidor;
import FuentesConocimiento.AgenteIniciarPartida;
import contratos.iEnsambladorServidor;
import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iListener;
import contratos.iObservador;
import java.io.IOException;

/**
 *
 * @author Sebastian Moreno
 */
public class BlackBoardServer {

    public static void main(String[] args) {
        final int PUERTO_DE_ESCUCHA = 5000;
        System.out.println("Iniciando Servidor de Rummy...");

        // 1. CREACIÓN DE COMPONENTES
        // 1.A. Crear Despachador (El que envía)
        iDespachador despachador = new DespachadorAsincrono();

        // 1.B. Crear Directorio (El que sabe "dónde viven")
        iDirectorio directorio = new Directorio();

        // 1.C. Crear Pizarra (El estado)
        EstadoJuegoPizarra pizarra = new EstadoJuegoPizarra();

        AgenteIniciarPartida agentePartida = new AgenteIniciarPartida(pizarra);

        // ¡MODIFICADO! Ahora se le inyecta el despachador.
        iControladorBlackboard controladorBlackboard = new ControladorBlackboard(agentePartida, directorio, despachador);

        // 1.E. Conectar Pizarra -> Controlador (Observer)
        pizarra.addObservador((iObservador) controladorBlackboard);

        // 2. ENSAMBLAJE DE RED (Usando el módulo externo)
        iEnsambladorServidor ensamblador = new EnsambladorServidor();

        // La llamada al ensamblador sigue igual, ya que este
        // solo se encarga de conectar el Procesador al Listener.
        iListener listenerServidor = ensamblador.ensamblarRedServidor(
                pizarra
        );

        // 3. Iniciar el Servidor
        try {
            System.out.println("[Servidor Main] Escuchando en el puerto " + PUERTO_DE_ESCUCHA);
            listenerServidor.iniciar(PUERTO_DE_ESCUCHA);
        } catch (IOException e) {
            System.err.println("[Servidor Main] ERROR FATAL: No se pudo iniciar el servidor.");
            e.printStackTrace();
        }
    }
}
