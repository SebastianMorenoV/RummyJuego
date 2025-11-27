package utils;

import java.net.Socket;

/**
 * Contenedor de datos utilizado para encapsular una petición 
 * de red recibida por el servidor. 
 * Se emplea para transferir los datos de la conexión desde el hilo de escucha 
 * (`ServerTCP.iniciar`) al hilo de procesamiento de la cola, manteniendo 
 * la referencia al socket para su posterior cierre.
 *
 * @author Sebastian Moreno
 */
public class PeticionCliente {

    public final Socket socketCliente;
    public final String mensajeRecibido;
    public final String ipCliente;

    public PeticionCliente(Socket socketCliente, String mensajeRecibido) {
        this.socketCliente = socketCliente;
        this.mensajeRecibido = mensajeRecibido;
        this.ipCliente = socketCliente.getInetAddress().getHostAddress();
    }
}
