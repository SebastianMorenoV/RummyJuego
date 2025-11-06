package control;

import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iDirectorio;
import contratos.iObservador;
import contratos.iPizarraJuego;
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

    public ControladorBlackboard(List<iAgenteConocimiento> listaDeAgentes, iDirectorio directorio) {
        this.agentes = new HashMap<>();
        this.directorio = directorio; // Recibe el directorio

        for (iAgenteConocimiento agente : listaDeAgentes) {
            this.agentes.put(agente.getComandoQueManeja(), agente);
        }
    }

    @Override
    public void actualiza(iPizarraJuego pizarra, String evento) {
        // Hacemos cast una sola vez para acceder a métodos específicos
        EstadoJuegoPizarra pizarraConcreta = (EstadoJuegoPizarra) pizarra;

        // Obtenemos los datos ANTES del switch
        String jugadorQueMovio = pizarraConcreta.getUltimoJugadorQueMovio();
        if (jugadorQueMovio == null) {
            jugadorQueMovio = "ID_DESCONOCIDO"; // Solo como fallback
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
                // --- SÍ REENVIAMOS LOS MOVIMIENTOS TEMPORALES ---
                // Esto es para que los otros jugadores vean el "arrastre"
                String mensajeMovimiento = "MOVIMIENTO_RECIBIDO:" + ultimoPayload;
                System.out.println("[Controlador] Reenviando MOVIMIENTO (temporal) a inactivos.");
                directorio.enviarATurnosInactivos(jugadorQueMovio, mensajeMovimiento);
                break;

            
            case "AVANZAR_TURNO":
                // Esto se llama DESPUÉS de FINALIZAR_TURNO o TOMAR_FICHA
                
                // --- ¡NUEVA LÓGICA! ---
                // 1. Transmitir el ESTADO FINAL con un *nuevo comando*.
                //    (ultimoPayload contiene el estado final de FINALIZAR_TURNO
                //    o el estado revertido de TOMAR_FICHA).
                String mensajeMovimientoFinal = "ESTADO_FINAL_TABLERO:" + ultimoPayload;
                System.out.println("[Controlador] Transmitiendo ESTADO_FINAL_TABLERO a inactivos.");
                directorio.enviarATurnosInactivos(jugadorQueMovio, mensajeMovimientoFinal);
                // --- FIN DE NUEVA LÓGICA ---

                
                // 2. Notificar a TODOS quién es el NUEVO jugador en turno.
                notificarCambioDeTurno(pizarra);
                break;

            default:
                throw new AssertionError("Evento desconocido: " + evento);
        }
    }

    /**
     * (MÉTODO PRIVADO) Lee el jugador actual
     * de la pizarra y envía el broadcast a todos.
     */
    private void notificarCambioDeTurno(iPizarraJuego pizarra) {
        String nuevoJugadorEnTurno = pizarra.getJugador(); // Ya es el *nuevo* jugador

        if (nuevoJugadorEnTurno != null) {
            System.out.println("[Controlador] Notificando cambio de turno a: " + nuevoJugadorEnTurno);
            String mensajeTurno = "TURNO_CAMBIADO:" + nuevoJugadorEnTurno;

            // Notifica a TODOS (incluido el nuevo) de quién es el turno.
            directorio.enviarATodos(mensajeTurno);
        }
    }
}