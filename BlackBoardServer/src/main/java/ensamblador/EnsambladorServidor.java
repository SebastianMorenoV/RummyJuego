package ensamblador;

import com.mycompany.tcpejemplo.DespachadorAsincrono;
import procesadores.ProcesadorServidor;
import sockets.ServerTCP;
import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iListener;
import contratos.iPizarraJuego;
import contratos.iProcesador;
import control.ControladorBlackboard;
import directorio.Directorio;
import java.util.ArrayList;
import java.util.List;
import observer.iObservador;
import pizarra.EstadoJuegoPizarra;

/**
 * El "Jefe de Obra" 👷 del SERVIDOR. Vive en el proyecto de más alto nivel
 * (BlackBoardServer) y es el ÚNICO que conoce TODAS las clases concretas.
 */
public class EnsambladorServidor {

    public static iListener ensamblarServidor() {
        System.out.println("[EnsambladorServidor] Ensamblando componentes...");

        // 1. Crear el Despachador (El que envía)
        iDespachador despachador = new DespachadorAsincrono();

        // 2. Crear el Directorio (y darle el despachador)
        iDirectorio directorio = new Directorio(despachador);

        // 3. Crear la Pizarra
        EstadoJuegoPizarra pizarra = new EstadoJuegoPizarra(); // Usamos la clase concreta

        // 4. Crear los Agentes/Controlador (y darles el directorio)
        List<iAgenteConocimiento> agentes = new ArrayList<>();
        // ... (Aquí agregarías tus agentes: new AgenteMovimiento(pizarra), etc.)
        iControladorBlackboard controladorBlackboard = new ControladorBlackboard(agentes, directorio);

        // 5. Conectar Pizarra -> Controlador (Observer)
        // (Asegúrate de que EstadoJuegoPizarra tenga el método addObservador)
        pizarra.addObservador((iObservador) controladorBlackboard);

        // 6. Crear el Procesador (y darle Pizarra, Despachador y Directorio)
        iProcesador logicaServidor = new ProcesadorServidor(pizarra, despachador, directorio);

        // 7. Crear el Listener (y darle el Procesador)
        iListener listener = new ServerTCP(logicaServidor);

        System.out.println("[EnsambladorServidor] Ensamblaje finalizado.");
        return listener;
    }
}
