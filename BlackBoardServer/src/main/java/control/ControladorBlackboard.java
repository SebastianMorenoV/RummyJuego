package control;

import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iObservador;
import contratos.iPizarraJuego;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pizarra.EstadoJuegoPizarra;

/**
 *
 * @author benja
 */
public class ControladorBlackboard implements iControladorBlackboard, iObservador {

    private final Map<String, iAgenteConocimiento> agentes;
    private final iDirectorio directorio;
    private final iDespachador despachador;

    public ControladorBlackboard(List<iAgenteConocimiento> listaDeAgentes,
            iDirectorio directorio,
            iDespachador despachador) {
        this.agentes = new HashMap<>();
        this.directorio = directorio;
        this.despachador = despachador;

        for (iAgenteConocimiento agente : listaDeAgentes) {
            this.agentes.put(agente.getComandoQueManeja(), agente);
        }
    }

    @Override
    public void actualiza(iPizarraJuego pizarra, String evento) {
        EstadoJuegoPizarra pizarraConcreta = (EstadoJuegoPizarra) pizarra;

        String jugadorQueMovio = pizarraConcreta.getUltimoJugadorQueMovio();
        if (jugadorQueMovio == null) {
            jugadorQueMovio = "ID_DESCONOCIDO";
        }
        String ultimoPayload = pizarraConcreta.getUltimoTableroSerializado();

        switch (evento) {
            case "REGISTRAR":
                System.out.println("[Controlador] Pizarra notificó REGISTRO");
                boolean partidaIniciada = pizarra.iniciarPartidaSiCorresponde();
                if (partidaIniciada) {
                    notificarCambioDeTurno(pizarra);
                }
                break;

            case "MOVIMIENTO":
                String mensajeMovimiento = "MOVIMIENTO_RECIBIDO:" + ultimoPayload;
                System.out.println("[Controlador] Reenviando MOVIMIENTO (temporal) a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimiento);
                break;

            case "AVANZAR_TURNO":
                String mensajeMovimientoFinal = "ESTADO_FINAL_TABLERO:" + ultimoPayload;
                System.out.println("[Controlador] Transmitiendo ESTADO_FINAL_TABLERO a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimientoFinal);

                notificarCambioDeTurno(pizarra);
                break;

            default:
                throw new AssertionError("Evento desconocido: " + evento);
        }
    }

    /**
     * Lee el jugador actual de la pizarra y envía el broadcast a todos.
     */
    private void notificarCambioDeTurno(iPizarraJuego pizarra) {
        String nuevoJugadorEnTurno = pizarra.getJugador();

        if (nuevoJugadorEnTurno != null) {
            System.out.println("[Controlador] Notificando cambio de turno a: " + nuevoJugadorEnTurno);
            String mensajeTurno = "TURNO_CAMBIADO:" + nuevoJugadorEnTurno;

            enviarATodos(mensajeTurno);
        }
    }

    /**
     * Envía un mensaje a todos los jugadores registrados usando el despachador.
     */
    private void enviarATodos(String mensaje) {
        System.out.println("[Controlador] Preparando envío a TODOS de: " + mensaje);

        // 1. Obtiene la lista de direcciones del Directorio
        for (Map.Entry<String, iDirectorio.ClienteInfoDatos> entry : directorio.getAllClienteInfo().entrySet()) {
            try {
                iDirectorio.ClienteInfoDatos destino = entry.getValue();

                // 2. Usa el Despachador para enviar el mensaje
                this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
            } catch (IOException e) {
                System.err.println("[Controlador->Despachador] Error al enviar a " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Envía un mensaje a todos excepto al jugador que originó la acción.
     */
    private void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje) {
        System.out.println("[Controlador] Preparando envío a INACTIVOS de: " + mensaje);
        // 1. Obtiene la lista de direcciones del Directorio
        for (Map.Entry<String, iDirectorio.ClienteInfoDatos> entry : directorio.getAllClienteInfo().entrySet()) {

            // 2. Filtra al jugador que envío
            if (!entry.getKey().equals(jugadorQueEnvio)) {
                
                try {
                    iDirectorio.ClienteInfoDatos destino = entry.getValue();
                    
                    // 3. Usa el Despachador para enviar el mensaje
                    this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
                } catch (IOException e) {
                    System.err.println("[Controlador->Despachador] Error al enviar a (inactivo) " + entry.getKey() + ": " + e.getMessage());
                }
            }
        }
    }
}
