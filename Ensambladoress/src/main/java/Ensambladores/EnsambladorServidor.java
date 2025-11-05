package Ensambladores;

// 1. YA NO IMPORTA NADA DE BlackBoardServer (ni Directorio, ni Pizarra, ni Controlador)
import procesadores.ProcesadorServidor;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iListener;
import contratos.iPizarraJuego;
import contratos.iProcesador;
import contratos.iEnsambladorServidor;
// (No necesita iObservador ni iAgenteConocimiento)

/**
 * (REFACTORIZADO)
 * El "Jefe de Obra" 👷 de la RED.
 * Recibe los componentes del Servidor y les conecta la red.
 */
public class EnsambladorServidor implements iEnsambladorServidor {

    /**
     * Este método ya NO crea los componentes, solo los "conecta".
     * La creación se hace en el Main.
     */
    @Override
    public iListener ensamblarRedServidor(
            iPizarraJuego pizarra, 
            iDespachador despachador, 
            iDirectorio directorio) 
    {
        System.out.println("[EnsambladorServidor] Conectando componentes de red...");

        // 1. Crear el Procesador (Lógica de Red)
        // (Le pasamos las piezas que nos dio el Main)
        iProcesador logicaServidor = new ProcesadorServidor(pizarra, despachador, directorio);

        // 2. Crear el Listener (El que escucha)
        // (Le inyectamos la lógica de red)
        iListener listener = new ServerTCP(logicaServidor);

        System.out.println("[EnsambladorServidor] Conexión finalizada.");
        return listener;
    }
}