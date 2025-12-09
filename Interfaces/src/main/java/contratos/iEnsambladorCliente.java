package contratos;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Contrato para el Ensamblador de Componentes del Cliente (Factory). Define los
 * métodos necesarios para crear y configurar la infraestructura de comunicación
 * del lado del cliente (capas de envío y recepción).
 *
 * @author benja
 */
public interface iEnsambladorCliente {

    /**
     * Crea e inicializa el componente de envío de mensajes (Despachador) del
     * cliente.
     *
     * @param ipServidor La IP del servidor de destino.
     * @param puertoServidor El puerto del servidor de destino.
     * @return Una instancia de {@link iDespachador} lista para enviar comandos
     * al servidor.
     */
    iDespachador crearDespachador(String ipServidor, int puertoServidor);

    /**
     * Crea e inicializa el componente de escucha (Listener) del cliente,
     * conectándolo al procesador de lógica y al oyente del Modelo.
     *
     * @param miId El identificador único del cliente.
     * @param oyentes
     * @return Una instancia de {@link iListener} lista para recibir mensajes
     * del servidor.
     */
    iListener crearListener(String miId, List<PropertyChangeListener> oyentes);
}
