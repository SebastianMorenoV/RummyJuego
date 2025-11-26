package contratos;


/**
 * Contrato para el Controlador del Blackboard.
 * Es el único punto de entrada que el Componente de Red conocerá.
 * 
 * @author Sebastian Moreno
 */
public interface iControladorBlackboard {
    
   void notificarCambioDeTurno(iPizarraJuego pizarra);
   
   void enviarATodos(String mensaje);
   
   void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje);
   
   void enviarMensajeDirecto(String idJugador, String mensaje);
}