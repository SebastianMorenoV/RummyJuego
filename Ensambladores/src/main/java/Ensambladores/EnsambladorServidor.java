package Ensambladores;

import procesadores.Procesador;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iListener;
import contratos.iPizarraJuego;
import contratos.iProcesador;
import contratos.iEnsambladorServidor;

/**
 *
 * El "Jefe de Obra" de la red. Recibe los componentes del Servidor y les
 * conecta la red.
 *
 * @author chris
 */
public class EnsambladorServidor implements iEnsambladorServidor {

    /**
     * Este método conecta a los componentes La creación se hace en los Main.
     *
     * @param pizarra
     * @return
     */
    @Override
    public iListener ensamblarRedServidor(
            iPizarraJuego pizarra
    ) {
        System.out.println("[EnsambladorServidor] Conectando componentes de red...");

        // 1. Crear el Procesador (Lógica de Red)
        // con las piezas que nos dio el Main
        iProcesador logicaServidor = new Procesador(pizarra);

        // 2. Crear el Listener (El que escucha)
        iListener listener = new ServerTCP(logicaServidor);

        System.out.println("[EnsambladorServidor] Conexión finalizada.");

        return listener;
    }
}
