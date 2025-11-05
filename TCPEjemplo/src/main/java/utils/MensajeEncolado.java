package utils;

/**
 * Esta clase es solo un paquete para guardar el mensaje y su destino juntos.
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