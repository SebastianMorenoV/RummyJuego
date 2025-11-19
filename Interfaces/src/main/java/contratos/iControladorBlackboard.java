package contratos;


/**
 * Contrato para el Controlador del Blackboard.
 * Es el único punto de entrada que el Componente de Red conocerá.
 * 
 * @author Sebastian Moreno
 */
public interface iControladorBlackboard {
    
    /**
     * Procesa un comando genérico.
     * Recibe los datos "crudos" y devuelve un resultado "genérico".
     * @return un iResultadoComando con las instrucciones para la red.
     * 
     * Es probable que tenga a futuro un metodo para hablarle a los agentes (MVCs)
     */
}