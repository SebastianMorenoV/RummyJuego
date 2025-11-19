package Ensambladores;

import sockets.ClienteTCP;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iEnsambladorCliente; // Interfaz
import contratos.iListener;
import java.beans.PropertyChangeListener;
import procesadores.Procesador;

/**
 * Clase de utilidad tipo "Fábrica" que construye y conecta los componentes de
 * red para el cliente.
 *
 * @author benja
 */
public final class EnsambladorCliente implements iEnsambladorCliente {

    public EnsambladorCliente() {

    }

    /**
     * Ensambla el componente de *envío* (Despachador) para el CLIENTE.
     */
    @Override
    public iDespachador crearDespachador(String ipServidor, int puertoServidor) {
        System.out.println("[Ensamblador] Creando Despachador -> (Servidor en "
                + ipServidor + ":" + puertoServidor + ")...");
        iDespachador despachador = new ClienteTCP();
        System.out.println("[Ensamblador] Despachador creado.");
        return despachador;
    }

    /**
     * Ensambla el componente de *escucha* (Listener) para el CLIENTE.
     */
    @Override
    public iListener crearListener(String miId, PropertyChangeListener oyente) {
        System.out.println("[Ensamblador] Ensamblando Listener para CLIENTE "
                + "(" + miId + ")...");

        // 1. Crear la "Lógica de Cliente"
        Procesador logicaCliente = new Procesador();

        // 2. Conectar el Modelo (oyente) para que reciba eventos de la red.
        System.out.println("[Ensamblador] Conectando Oyente (" + oyente.getClass().getSimpleName()
                + ") -> ProcesadorCliente");
        logicaCliente.addPropertyChangeListener(oyente);

        // 3. Crear el "Mesero" (Listener) e inyectarle la lógica ya conectada.
        iListener listener = new ServerTCP(logicaCliente);

        System.out.println("[Ensamblador] Listener ensamblado.");

        return listener;
    }
}
