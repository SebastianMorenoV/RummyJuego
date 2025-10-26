package ensamblador;

import agentes.AgenteMovimiento;
import agentes.AgenteRegistro;
import com.mycompany.tcpejemplo.DespachadorAsincrono;
import procesadores.ProcesadorServidor;
import sockets.ServerTCP;
import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iListener;
import contratos.iPizarraJuego;
import contratos.iProcesador;
import control.ControladorBlackboard;
import java.util.ArrayList;
import java.util.List;
import pizarra.EstadoJuegoPizarra;

/**
 * El "Jefe de Obra" 👷 del SERVIDOR. Vive en el proyecto de más alto nivel
 * (BlackBoardServer) y es el ÚNICO que conoce TODAS las clases concretas.
 */
public class EnsambladorServidor {

    // Hacemos el método estático para que 'main' pueda llamarlo
    public static iListener ensamblarServidor() {
        System.out.println("[EnsambladorServidor] Ensamblando componentes...");

        // --- Componente Blackboard ---
        iPizarraJuego pizarra = new EstadoJuegoPizarra(); // Concreto -> Interfaz

        List<iAgenteConocimiento> agentes = new ArrayList<>();
        agentes.add(new AgenteRegistro(pizarra));     // Concreto -> Interfaz
        agentes.add(new AgenteMovimiento(pizarra)); // Concreto -> Interfaz
        // (Aquí puedes añadir más agentes sin que nadie más se entere)

        iControladorBlackboard controladorBlackboard = new ControladorBlackboard(agentes); // Concreto -> Interfaz

        // --- Componente de Red ---
        iDespachador despachador = new DespachadorAsincrono(); // Concreto -> Interfaz

        iProcesador logicaServidor = new ProcesadorServidor(controladorBlackboard, despachador); // Concreto -> Interfaz

        iListener listener = new ServerTCP(logicaServidor); // Concreto -> Interfaz

        System.out.println("[EnsambladorServidor] Ensamblaje finalizado.");
        return listener;
    }
}
