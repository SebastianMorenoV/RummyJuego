package vista;

import eventos.Evento;
import modelo.IModelo;



/**
 * Clase que implementa la vista para obtener datos.
 *
 * @author benja
 */
public interface Observador {

    void actualiza(IModelo modelo, Evento evento);
}
