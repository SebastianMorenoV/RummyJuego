package Vista;

import eventos.Evento;
import Modelo.iModelo;

/**
 * Clase que implementa la vista para obtener datos.
 *
 * @author benja
 */
public interface Observador {

    void actualiza(iModelo modelo, Evento evento);
}
