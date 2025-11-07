package contratos;

import java.beans.PropertyChangeListener;

/**
 *
 * @author benja
 */
public interface iEnsambladorCliente {

    iDespachador crearDespachador(String ipServidor, int puertoServidor);

    iListener crearListener(String miId, PropertyChangeListener oyente);
}
