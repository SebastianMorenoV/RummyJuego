package contratos;


/**
 * Contrato para el Controlador del Blackboard.
 * Es el único punto de entrada que el Componente de Red conocerá.
 * 
 * @author Sebastian Moreno
 */
public interface iControladorBlackboard {
   
   /**
     * Notifica a todos los clientes sobre un cambio en el turno de juego, 
     * incluyendo la identificación del nuevo jugador activo.
     * * @param pizarra La instancia de la Pizarra de Juego de donde se obtiene el estado actual.
     */
    void notificarCambioDeTurno(iPizarraJuego pizarra);
    
    /**
     * Envía un mensaje específico a **todos** los jugadores registrados en el directorio.
     * * @param mensaje El contenido serializado del mensaje a transmitir.
     */
    void enviarATodos(String mensaje);
    
    /**
     * Envía un mensaje únicamente a los jugadores que están inactivos 
     * * @param jugadorQueEnvio El ID del jugador que generó la acción y que debe ser excluido.
     * @param mensaje El contenido serializado del mensaje a transmitir.
     */
    void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje);
    
    /**
     * Envía un mensaje directo a un jugador identificado por su ID único.
     * * @param idJugador El ID del jugador destino.
     * @param mensaje El contenido serializado del mensaje a transmitir.
     */
    void enviarMensajeDirecto(String idJugador, String mensaje);
    
    void enviarMensajeCandidato(String idCandidato, String mensaje);
    
    void manejarRechazoInmediato(iPizarraJuego pizarra, String mensajeError);
    
    void registrarYRechazar(String id, String ip, int puerto, String msg);
}