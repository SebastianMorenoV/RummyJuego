package main;

import control.ControladorBlackboard;
import directorio.Directorio;
import pizarra.EstadoJuegoPizarra;
import sockets.ClienteTCP;
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
 * Arranca el servidor del sistema Blackboard para Rummy. Ensambla módulos:
 * directorio, pizarra, controlador y comunicación TCP. Gestiona jugadores
 * conectados, turnos y distribución de eventos en red.
 *
 * @author Sebastian Moreno
 */
public class BlackBoardServer {

    public static void main(String[] args) {
        final int PUERTO_DE_ESCUCHA = 5000;
        System.out.println("Iniciando Servidor de Rummy...");

        iDespachador despachador = new ClienteTCP();

        iDirectorio directorio = new Directorio();

        EstadoJuegoPizarra pizarra = new EstadoJuegoPizarra();

        AgenteIniciarPartida agentePartida = new AgenteIniciarPartida(pizarra);

        iControladorBlackboard controladorBlackboard = new ControladorBlackboard(agentePartida, directorio, despachador);

        pizarra.addObservador((iObservador) controladorBlackboard);

        iEnsambladorServidor ensamblador = new EnsambladorServidor();

        iListener listenerServidor = ensamblador.ensamblarRedServidor(
                pizarra
        );

        try {
            System.out.println("[Servidor Main] Escuchando en el puerto " + PUERTO_DE_ESCUCHA);
            listenerServidor.iniciar(PUERTO_DE_ESCUCHA);
        } catch (IOException e) {
            System.err.println("[Servidor Main] ERROR FATAL: No se pudo iniciar el servidor.");
            e.printStackTrace();
        }
    }
}
