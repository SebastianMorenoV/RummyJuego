package contratos;

import java.io.IOException;

/**
 * Contrato para el TRANSPORTE DE ENVÍO. Define "cómo enviar" un mensaje a un
 * destino.
 *
 * @author Sebastian Moreno
 */
public interface iDespachador {

    // Para el Servidor (necesita especificar el destino cada vez)
    void enviar(String host, int puerto, String mensaje) throws IOException;
}
