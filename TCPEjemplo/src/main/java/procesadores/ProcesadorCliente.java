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

    @Override
    public String procesar(String ipServidor, String mensaje) {
        // El servidor envía: "MOVIMIENTO_RECIBIDO:ID_JUGADOR:PAYLOAD"
        String[] partes = mensaje.split(":", 3);

        // Si no es un mensaje de movimiento, solo log
        if (partes.length < 3 || !partes[0].equals("MOVIMIENTO_RECIBIDO")) {
            System.out.println("\n[" + miId + " NOTIFICACIÓN]: " + mensaje);
            return "CLIENTE_RECIBIDO_OK";
        }

        // Es un movimiento
        String idJugadorQueMovio = partes[1];
        String payload = partes[2]; // Este es el string crudo

        System.out.println("\n[" + miId + " MOVIMIENTO RECIBIDO] de " + idJugadorQueMovio);
        System.out.println("[" + miId + "] Disparando evento 'MOVIMIENTO_RECIBIDO' con payload: " + payload);

        // Dispara el evento pasando el PAYLOAD (STRING) crudo.
        // El Modelo (que está escuchando) se encargará de deserializarlo.
        support.firePropertyChange("MOVIMIENTO_RECIBIDO", null, payload);

        return "CLIENTE_EVENTO_DISPARADO_OK";
    }
}
