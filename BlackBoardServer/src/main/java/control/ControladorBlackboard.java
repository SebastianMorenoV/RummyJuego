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

        String jugadorActual = pizarra.getJugador(); // El jugador que TIENE el turno
        String ultimoPayload = pizarraConcreta.getUltimoTableroSerializado();

        switch (evento) {
            case "REGISTRAR":
                System.out.println("[Controlador] Pizarra notificó REGISTRO");
                boolean partidaIniciada = pizarra.iniciarPartidaSiCorresponde();
                if (partidaIniciada) {
                    // Si la partida acaba de iniciar, notificamos a todos
                    // quién tiene el primer turno.
                    notificarCambioDeTurno(pizarra);
                }

                break;

            case "MOVIMIENTO":
                // El jugador movió, pero NO ha terminado el turno.
                String jugadorQueMovio = "ID_DESCONOCIDO"; // Necesitaríamos que la pizarra guarde quién movió

                // Extrae el ID del jugador del payload (si tu serialización lo incluye)
                // O modifica la pizarra para que guarde "ultimoJugadorQueMovio"
                // Por ahora, usamos tu lógica anterior:
                String mensajeMovimiento = "MOVIMIENTO_RECIBIDO:" + jugadorQueMovio + ":" + ultimoPayload;
                System.out.println("[Controlador] Reenviando MOVIMIENTO a inactivos.");
                directorio.enviarATurnosInactivos(jugadorQueMovio, mensajeMovimiento);
                break;

            // --- ¡NUEVO EVENTO! ---
            case "AVANZAR_TURNO":
                // Esto se llama DESPUÉS de FINALIZAR_TURNO o TOMAR_FICHA
                String nuevoJugadorEnTurno = pizarra.getJugador(); // Ya es el *nuevo* jugador

                if (nuevoJugadorEnTurno != null) {
                    System.out.println("[Controlador] Notificando cambio de turno a: " + nuevoJugadorEnTurno);
                    String mensajeTurno = "TURNO_CAMBIADO:" + nuevoJugadorEnTurno;

                    // Notifica a TODOS (incluido el nuevo) de quién es el turno.
                    directorio.enviarATodos(mensajeTurno);
                }
                break;

            default:
                throw new AssertionError("Evento desconocido: " + evento);
        }
    }

    /**
     * (NUEVO MÉTODO PRIVADO PARA EVITAR REPETIR CÓDIGO) Lee el jugador actual
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
