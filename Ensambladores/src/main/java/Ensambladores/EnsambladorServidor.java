package Ensambladores;

import procesadores.Procesador;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iListener;
import contratos.iPizarraJuego;
import contratos.iProcesador;
import contratos.iEnsambladorServidor;

/**
 * Clase de utilidad (Factory) responsable de ensamblar la lógica del servidor
 * con los componentes de la capa de comunicación (Red).
 *
 * @author chris
 */
public class EnsambladorServidor implements iEnsambladorServidor {

    /**
     * Este método conecta la lógica central del servidor mediante Procesador 
     * configurado con la iPizarraJuego con el componente de escucha de red (ServerTCP).
     *
     * @param pizarra La Pizarra de Juego que contiene el estado compartido y la lógica a procesar.
     * @return Una instancia de {@link iListener} (ServerTCP) que gestionará la recepción 
     * de comandos de los clientes.
     */
    @Override
    public iListener ensamblarRedServidor(
            iPizarraJuego pizarra
    ) {
        System.out.println("[EnsambladorServidor] Conectando componentes de red...");

        iProcesador logicaServidor = new Procesador(pizarra);

        iListener listener = new ServerTCP(logicaServidor);

        System.out.println("[EnsambladorServidor] Conexión finalizada.");

        return listener;
    }
}
