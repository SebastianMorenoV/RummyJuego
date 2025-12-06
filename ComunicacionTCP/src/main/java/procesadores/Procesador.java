package procesadores;

import contratos.iPizarraJuego;
import contratos.iProcesador;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Componente unificado para el procesamiento de mensajes de red.
 * Opera en dos modos, determinado por su construcción:
 * - Modo Servidor: Dirige comandos de clientes a la Pizarra de Juego (Blackboard).
 * - Modo Cliente: Transforma eventos de red del servidor en eventos locales (PropertyChange) 
 * para que el Modelo los consuma.
 * * @author benjamin y sebastian
 */
    public class Procesador implements iProcesador {

    private final iPizarraJuego pizarra;
    
    private final PropertyChangeSupport support;

    private final boolean esModoServidor;

    /**
     * Constructor para el CLIENTE.
     * No recibe pizarra, por lo tanto, actuará reenviando eventos al Modelo.
     */
    public Procesador() {
        this.pizarra = null;
        this.support = new PropertyChangeSupport(this);
        this.esModoServidor = false; 
    }

    /**
     * Constructor para el SERVIDOR.
     * Recibe la pizarra, por lo tanto, actuará procesando lógica de juego.
     * @param pizarra La pizarra del Blackboard.
     */
    public Procesador(iPizarraJuego pizarra) {
        this.pizarra = pizarra;
        this.support = new PropertyChangeSupport(this);
        this.esModoServidor = true; 
    }

    /**
     * Agrega un PropertyChangeListener (oyente) al procesador.
     * En el Modo Cliente, esta función es utilizada por el Modelo para registrarse 
     * y escuchar los eventos de la red.
     * * @param pcl El oyente de cambio de propiedad a registrar.
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.support.addPropertyChangeListener(pcl);
    }

    /**
     * Elimina un oyente de cambio de propiedad previamente registrado.
     * * @param pcl El oyente de cambio de propiedad a eliminar.
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.support.removePropertyChangeListener(pcl);
    }

    /**
     * Implementación del método central de la interfaz iProcesador.
     * Desacopla el mensaje de red entrante y lo dirige a la lógica apropiada 
     * según el modo de operación del procesador.
     * * @param ipRemitente La dirección IP del remitente del mensaje.
     * @param mensaje El contenido serializado del mensaje recibido a través de la red.
     */
    @Override
    public void procesar(String ipRemitente, String mensaje) {
        
        if (esModoServidor) {    
            String[] partes = mensaje.split(":", 3); 
            
            if (partes.length < 2) {
                System.out.println("ERROR: Formato de mensaje servidor inválido.");
            }

            String idCliente = partes[0];
            String comando = partes[1];
            String payload = (partes.length > 2) ? partes[2] : "";

            System.out.println("[Procesador Server] Comando recibido: " + comando);

           pizarra.procesarComando(idCliente, comando, payload);

            

        } else {
            String[] partes = mensaje.split(":", 2);
            
            String evento = partes[0];
            String payload = (partes.length > 1) ? partes[1] : "";

            support.firePropertyChange(evento, null, payload);

        }
    }
}