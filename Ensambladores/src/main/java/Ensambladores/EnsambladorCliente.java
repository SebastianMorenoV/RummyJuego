package Ensambladores;

import sockets.ClienteTCP;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import java.beans.PropertyChangeListener;
import java.util.List;
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
     * Ensambla el componente de Despacho para el cliente, 
     * preparando la comunicación hacia la dirección del servidor especificada.
     *
     * @param ipServidor La dirección IP del servidor.
     * @param puertoServidor El puerto de conexión del servidor.
     * @return Una instancia de {@link iDespachador} (ClienteTCP) funcional.
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
     * Ensambla el componente de Escucha para el cliente.
     * Configura el Procesador en modo cliente (sin pizarra) y lo conecta 
     * al oyente provisto (generalmente el Modelo), creando el componente de red 
     * que recibe mensajes entrantes (ServerTCP).
     *
     * @param miId El identificador único del cliente.
     * @param oyente La clase que implementa {@link PropertyChangeListener} (el Modelo) 
     * que recibirá los eventos procesados.
     * @return Una instancia de {@link iListener} (ServerTCP) lista para recibir conexiones.
     */
    @Override
     public iListener crearListener(String miId, List<PropertyChangeListener> oyentes) {

        Procesador logicaCliente = new Procesador();

        for (PropertyChangeListener oyente : oyentes) {
            logicaCliente.addPropertyChangeListener(oyente);
            System.out.println("[Ensamblador] Conectando Oyente (" + oyente.getClass().getSimpleName()
                    + ") -> ProcesadorCliente");
        }

        iListener listener = new ServerTCP(logicaCliente);
        return listener;
    }
}
