package contratos;

import java.io.IOException;

/**
 * Contrato para el TRANSPORTE DE ENVÍO. Define "cómo enviar" un mensaje a un
 * destino.
 *
 * @author Sebastian Moreno
 */
public interface iDespachador {

    /**
     * Envía un mensaje de texto a una dirección de host y puerto específicos.
     * * @param host La dirección IP o nombre de host del destino.
     * @param puerto El puerto del destino.
     * @param mensaje El contenido de texto a enviar.
     * @throws IOException Si ocurre un error durante la transmisión del mensaje.
     */
    void enviar(String host, int puerto, String mensaje) throws IOException;
}
