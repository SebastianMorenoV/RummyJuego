package resultados;

import contratos.iResultadoComando;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Sebastian Moreno
 */
public class ResultadoComando implements iResultadoComando {

    private final String respuestaAlRemitente;
    private final List<String> mensajesBroadcast;

    public ResultadoComando(String respuestaAlRemitente) {
        this.respuestaAlRemitente = respuestaAlRemitente;
        this.mensajesBroadcast = new ArrayList<>();
    }

    public void agregarBroadcast(String mensaje) {
        this.mensajesBroadcast.add(mensaje);
    }

    // Métodos del contrato
    @Override
    public String getRespuestaAlRemitente() {
        return respuestaAlRemitente;
    }

    @Override
    public List<String> getMensajesBroadcast() {
        return mensajesBroadcast;
    }
}
