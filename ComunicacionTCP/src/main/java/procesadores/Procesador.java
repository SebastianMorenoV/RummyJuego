package procesadores;

import contratos.iPizarraJuego;
import contratos.iProcesador;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Procesador Unificado.
 * Funciona tanto para el Cliente (MVC) como para el Servidor (Blackboard).
 * - Modo Servidor: Se activa si se construye con una iPizarraJuego.
 * - Modo Cliente: Se activa si se construye sin pizarra (constructor vacío).
 * @author benjamin y SEBAS A LAS 5:20 AM 19/11/2025 LOL
 */
public class Procesador implements iProcesador {

    // Referencia exclusiva del Servidor (puede ser null en el cliente)
    private final iPizarraJuego pizarra;
    
    // Referencia exclusiva del Cliente (para notificar al Modelo)
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
     * Permite agregar oyentes (El Modelo usa esto en el lado del Cliente).
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.support.removePropertyChangeListener(pcl);
    }

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