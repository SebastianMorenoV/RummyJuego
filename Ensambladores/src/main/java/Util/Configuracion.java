package Util;

/**
 * Clase de soporte que almacena la configuración de conexión global y fija de
 * la aplicación, como la dirección IP y el puerto del servidor.
 *
 * @author Sebastian Moreno
 */
public class Configuracion {

    private static final String ipServidor = "192.168.100.98";
    private static final int puerto = 5000;

    public Configuracion() {
    }

    /**
     * Obtiene la dirección IP estática del servidor central del juego.
     *
     * @return La IP del servidor como una cadena de texto (String).
     *
     */
    public static String getIpServidor() {
        return ipServidor;
    }

    /**
     * Obtiene el número de puerto estático que usa el servidor para escuchar
     * las conexiones de los clientes.
     *
     * @return El número de puerto como un entero (int).
     */
    public static int getPuerto() {
        return puerto;
    }

}
