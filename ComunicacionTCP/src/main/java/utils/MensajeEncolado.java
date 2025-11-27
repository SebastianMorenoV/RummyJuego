package utils;

/**
 * Clase de utilidad  utilizada para encapsular y transportar 
 * un mensaje junto con su destino de red (`host` y `puerto`). 
 * Se utiliza principalmente en el ClienteTCP (Despachador) para 
 * almacenar mensajes en la cola de salida antes de su envío asíncrono.
 *
 * @author Sebastian Moreno
 */
public class MensajeEncolado {

    public final String host;
    public final int puerto;
    public final String mensaje;

    public MensajeEncolado(String host, int puerto, String mensaje) {
        this.host = host;
        this.puerto = puerto;
        this.mensaje = mensaje;
    }
}
