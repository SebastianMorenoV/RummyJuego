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
// ¡Importamos nuestro agente "experto"!
import FuentesConocimiento.AgenteIniciarPartida;

/**
 * El Controlador. Escucha a la Pizarra "tonta" y reacciona
 * llamando a Agentes "expertos" o despachando mensajes.
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

        // Estos se leen de la pizarra DESPUÉS de que el comando los haya escrito
        String jugadorQueMovio = pizarraConcreta.getUltimoJugadorQueMovio();
        String ultimoPayload = pizarraConcreta.getUltimoTableroSerializado();

        switch (evento) {
            
            case "JUGADOR_UNIDO":
                System.out.println("[Controlador] Pizarra notificó JUGADOR_UNIDO.");
                // A futuro: enviarATodos("LOBBY_ACTUALIZADO:" + pizarraConcreta.getOrdenDeTurnos());
                
                int numJugadores = pizarraConcreta.getOrdenDeTurnos().size();
                // Simulación de Lobby: Si se conecta el 2do jugador, le ordenamos al 1ro (Host) que inicie.
                if (numJugadores == 3) { 
                    String idHost = pizarraConcreta.getOrdenDeTurnos().get(0);
                    System.out.println("[Controlador] Hay 2 jugadores. Pidiendo al Host (" + idHost + ") que inicie el juego.");
                    enviarMensajeDirecto(idHost, "COMANDO_INICIAR_PARTIDA");
                }
                break;

            case "EVENTO_PARTIDA_INICIADA":
                System.out.println("[Controlador] Evento PARTIDA_INICIADA detectado. Creando juego...");
                
                List<String> jugadoresIds = pizarraConcreta.getOrdenDeTurnos();
                System.out.println("[Controlador] Repartiendo para " + jugadoresIds.size() + " jugadores.");

                // 1. Llamar al Agente "Experto"
                AgenteIniciarPartida agentePartida = new AgenteIniciarPartida();
                
                // 2. El Agente crea manos y mazo
                Map<String, String> manosSerializadas = agentePartida.repartirManos(jugadoresIds);
                String mazoSerializado = agentePartida.getMazoSerializado();
                int mazoCount = mazoSerializado.split("\\|").length; // Contamos fichas

                // 3. Guardar el mazo "tonto" en la Pizarra
                pizarraConcreta.setMazoSerializado(mazoSerializado);

                // 4. Enviar a CADA jugador su mano inicial + el contador del mazo
                for (Map.Entry<String, String> entry : manosSerializadas.entrySet()) {
                    String idJugador = entry.getKey();
                    String manoPayload = entry.getValue();
                    // Formato: MANO_INICIAL:[payload_mano]$[conteo_mazo]
                    String mensajeMano = "MANO_INICIAL:" + manoPayload + "$" + mazoCount;
                    enviarMensajeDirecto(idJugador, mensajeMano);
                }

                // 5. Notifica a TODOS quién tiene el primer turno (incluye conteo de mazo)
                notificarCambioDeTurno(pizarra);
                break;

            case "MOVIMIENTO":
                String mensajeMovimiento = "MOVIMIENTO_RECIBIDO:" + ultimoPayload;
                System.out.println("[Controlador] Reenviando MOVIMIENTO (temporal) a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimiento);
                break;

            case "AVANZAR_TURNO": // Disparado por FINALIZAR_TURNO o TOMAR_FICHA
                System.out.println("[Controlador] Evento AVANZAR_TURNO detectado.");
                
                // 1. Enviar el estado final a los inactivos
                String mensajeMovimientoFinal = "ESTADO_FINAL_TABLERO:" + ultimoPayload;
                System.out.println("[Controlador] Transmitiendo ESTADO_FINAL_TABLERO a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimientoFinal);

                // 2. Notificar a TODOS del cambio de turno (con el mazo actualizado)
                notificarCambioDeTurno(pizarra);
                break;
                
            case "TOMAR_FICHA":
                System.out.println("[Controlador] " + jugadorQueMovio + " tomó ficha.");
                String fichaSerializada = pizarraConcreta.tomarFichaDelMazoSerializado();
                
                if(fichaSerializada != null) {
                    // 1. Envía la ficha solo al jugador que la pidió
                    enviarMensajeDirecto(jugadorQueMovio, "FICHA_RECIBIDA:" + fichaSerializada);
                }
                
                // 2. Avanza el turno (esto disparará el evento AVANZAR_TURNO)
                pizarra.avanzarTurno(); 
                break;

            default:
                System.err.println("[Controlador] Evento desconocido: " + evento);
        }
    }

    /**
     * Lee el jugador actual Y el conteo del mazo y notifica a todos.
     */
    private void notificarCambioDeTurno(iPizarraJuego pizarra) {
        String nuevoJugadorEnTurno = pizarra.getJugador();
        int mazoCount = ((EstadoJuegoPizarra) pizarra).getMazoSerializadoCount();

        if (nuevoJugadorEnTurno != null) {
            System.out.println("[Controlador] Notificando cambio de turno a: " + nuevoJugadorEnTurno);
            // Formato: TURNO_CAMBIADO:[ID_JUGADOR]:[CONTEO_MAZO]
            String mensajeTurno = "TURNO_CAMBIADO:" + nuevoJugadorEnTurno + ":" + mazoCount;
            enviarATodos(mensajeTurno);
        }
    }

    private void enviarATodos(String mensaje) {
        System.out.println("[Controlador] Preparando envío a TODOS de: " + mensaje);
        // (Usa startsWith para logs más limpios)
        String logMsg = mensaje.startsWith("MANO_INICIAL") ? "MANO_INICIAL:..." : mensaje; 
        
        for (Map.Entry<String, iDirectorio.ClienteInfoDatos> entry : directorio.getAllClienteInfo().entrySet()) {
            try {
                iDirectorio.ClienteInfoDatos destino = entry.getValue();
                System.out.println("[Controlador] Enviando a " + entry.getKey() + ": " + logMsg);
                this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
            } catch (IOException e) {
                System.err.println("[Controlador->Despachador] Error al enviar a " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    private void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje) {
        System.out.println("[Controlador] Preparando envío a INACTIVOS de: " + mensaje);
        for (Map.Entry<String, iDirectorio.ClienteInfoDatos> entry : directorio.getAllClienteInfo().entrySet()) {
            if (jugadorQueEnvio != null && !entry.getKey().equals(jugadorQueEnvio)) {
                try {
                    iDirectorio.ClienteInfoDatos destino = entry.getValue();
                    System.out.println("[Controlador] Enviando a (inactivo) " + entry.getKey() + ": " + mensaje);
                    this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
                } catch (IOException e) {
                    System.err.println("[Controlador->Despachador] Error al enviar a (inactivo) " + entry.getKey() + ": " + e.getMessage());
                }
            }
        }
    }
    
    private void enviarMensajeDirecto(String idJugador, String mensaje) {
        try {
            iDirectorio.ClienteInfoDatos destino = directorio.getClienteInfo(idJugador);
            if (destino != null) {
                // Log más limpio para manos grandes
                String logMsg = mensaje.startsWith("MANO_INICIAL") ? "MANO_INICIAL:..." : mensaje;
                System.out.println("[Controlador] Enviando a " + idJugador + ": " + logMsg);
                
                this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
            } else {
                 System.err.println("[Controlador] No se encontró destino para: " + idJugador);
            }
        } catch (IOException e) {
            System.err.println("[Controlador] Error al enviar mensaje directo a " + idJugador + ": " + e.getMessage());
        }
    }
}