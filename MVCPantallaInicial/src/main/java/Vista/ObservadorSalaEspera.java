package Vista;

import Modelo.IModeloPantallaInicial;
import eventos.Evento;

/**
 * Clase que implementa la vista para obtener datos.
 *
 * @author benja
 */
public interface ObservadorSalaEspera {

    void actualiza(IModeloPantallaInicial modelo, Evento evento);
}
