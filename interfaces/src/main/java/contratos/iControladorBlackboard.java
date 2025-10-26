package contratos;


/**
 * Contrato para el Controlador del Blackboard.
 * Es el único punto de entrada que el Componente de Red conocerá.
 */
public interface iControladorBlackboard {
    
    /**
     * Procesa un comando genérico.
     * Recibe los datos "crudos" y devuelve un resultado "genérico".
     * @return un iResultadoComando con las instrucciones para la red.
     */
    iResultadoComando procesarComando(String idCliente, String comando, String payload);
}