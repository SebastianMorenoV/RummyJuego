package contratos;

import contratos.iPizarraJuego;

/**
 * Observador del patrón Observer aplicado a la pizarra del juego. Cualquier
 * clase que implemente esta interfaz será notificada de eventos.
 *
 * @author Sebastian Moreno
 */
public interface iObservador {

    void actualiza(iPizarraJuego pizarra, String evento);

}
