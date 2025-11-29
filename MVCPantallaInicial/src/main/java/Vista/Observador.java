package Vista;

import eventos.Evento;
import Modelo.IModelo;

/**
 * Clase que implementa la vista para obtener datos.
 *
 * @author benja
 */
public interface Observador {

    void actualiza(IModelo modelo, Evento evento);
}
