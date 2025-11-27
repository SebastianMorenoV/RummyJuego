package contratos;

import contratos.iPizarraJuego;

/**
 * Observador del patrón Observer aplicado a la pizarra del juego. Cualquier
 * clase que implemente esta interfaz será notificada de eventos.
 *
 * @author Sebastian Moreno
 */
public interface iObservador {

    /**
     * Método invocado por la Pizarra para notificar al observador sobre una actualización
     * de estado o la ocurrencia de un evento.
     * * @param pizarra La instancia de la Pizarra de Juego que notifica el cambio.
     * @param evento El nombre del evento que ha ocurrido.
     */
    void actualiza(iPizarraJuego pizarra, String evento);

}
