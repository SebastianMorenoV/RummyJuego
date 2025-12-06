package Vista;

import eventos.Evento;
import Modelo.IModeloLobby;

/**
 * Clase que implementa la vista para obtener datos.
 *
 * @author benja
 */
public interface ObservadorLobby {

    void actualiza(IModeloLobby modelo, Evento evento);
}
