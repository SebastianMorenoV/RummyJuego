package contratos;

import java.util.List;

/**
 * Contrato para el Resultado de un comando. Esto es lo único que el
 * ProcesadorServidor conocerá.
 *
 * @author Sebastian Moreno
 */
public interface iResultadoComando {

    //La respuesta para el cliente que originó el comando.
    String getRespuestaAlRemitente();

    // La lista de mensajes para enviar a todos los demás.
    List<String> getMensajesBroadcast();

}
