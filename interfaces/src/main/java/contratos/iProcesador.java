package contratos;

/**
 * Contrato para la LÓGICA de la aplicación.
 * Define "qué hacer" cuando se recibe un mensaje.
 * 
 * @author chris
 */
public interface iProcesador {
    
    /**
     * Procesa un mensaje entrante de un cliente específico.
     * @param ipCliente La IP del remitente.
     * @param mensaje El contenido del mensaje.
     * @return Una respuesta para enviar de vuelta al remitente original.
     */
    String procesar(String ipCliente, String mensaje);
    
}