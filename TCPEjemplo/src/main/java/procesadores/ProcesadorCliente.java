package procesadores;

import contratos.iProcesador;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Lógica del Cliente. NO conoce DTOs. Recibe un mensaje, y si es
 * "MOVIMIENTO_RECIBIDO", dispara un evento PropertyChange con el PAYLOAD
 * (String) crudo.
 *
 * @author Sebastian Moreno
 */
public class ProcesadorCliente implements iProcesador {

    private final String miId;
    private final PropertyChangeSupport support;

    public ProcesadorCliente(String miId) {
        this.miId = miId;
        this.support = new PropertyChangeSupport(this);
    }

    /**
     * Permite que "Oyentes" (como el Modelo) se suscriban a este procesador.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    /**
     * Procesa un mensaje recibido desde el servidor.
     *
     * @param ipServidor
     * @param mensaje
     * @return
     */
    @Override
    public String procesar(String ipServidor, String mensaje) {
        String[] partes = mensaje.split(":", 2); // Dividir solo en 2: COMANDO y PAYLOAD
        String comando = partes[0];
        String payload = (partes.length > 1) ? partes[1] : "";

        support.firePropertyChange(comando, null, payload);

        return "CLIENTE_RECIBIDO_OK";
    }
}
