package vista;

import TipoEventos.EventoConfig;
import modelo.iModeloConfig;



/**
 * Clase que implementa la vista para obtener datos.
 * Si es necesario.
 * @author Sebastian Moreno
 */
public interface ObservadorConfig {

    void actualiza(iModeloConfig modelo, EventoConfig evento);
}
