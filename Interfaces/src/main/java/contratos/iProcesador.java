package contratos;

import java.beans.PropertyChangeListener;

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
    void procesar(String ipCliente, String mensaje);
    
    /**
     * Permite registrar observadores (Modelos) que escucharán los eventos de red.
     * Necesario para el modo Cliente.
     * @param listener El oyente a agregar.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Permite remover observadores.
     * @param listener El oyente a remover.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}