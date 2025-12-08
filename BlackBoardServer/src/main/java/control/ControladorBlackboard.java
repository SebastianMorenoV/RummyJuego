package control;

import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iObservador;
import contratos.iPizarraJuego;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import pizarra.EstadoJuegoPizarra;
import contratos.iAgentePartida;

/**
 * Controlador principal para la arquitectura Blackboard en el servidor. Escucha
 * los cambios notificados por la Pizarra de Juego y reacciona a estos eventos
 * orquestando acciones entre el Directorio, el Agente de Partida y el
 * Despachador de red.
 *
 * @author Sebas
 */
public class ControladorBlackboard implements iControladorBlackboard, iObservador {

    private final iDirectorio directorio;
    private final iDespachador despachador;
    private final iAgentePartida agentePartida;

    public ControladorBlackboard(
            iAgentePartida agentePartida,
            iDirectorio directorio,
            iDespachador despachador) {

        this.directorio = directorio;
        this.despachador = despachador;
        this.agentePartida = agentePartida;

    }

    /**
     * Método invocado por la Pizarra (Blackboard) cuando ocurre un cambio de
     * estado significativo que requiere una respuesta del servidor. Implementa
     * el patrón Observer.
     *
     * @param pizarra La Pizarra de Juego que notifica el cambio.
     * @param evento El tipo de evento ocurrido.
     */
    @Override
    public void actualiza(iPizarraJuego pizarra, String evento) {

        String jugadorQueMovio = pizarra.getUltimoJugadorQueMovio();
        String ultimoPayload = pizarra.getUltimoTableroSerializado();

        switch (evento) {

            case "CONFIGURAR_PARTIDA":
                System.out.println("Si llego hasta aqui significa que ya termine el caso de uso.");
                //que deberia hacer aqui si todavia nadie se une?
                break;

            case "JUGADOR_UNIDO":
                String[] ipJugador = pizarra.getIpCliente();
                directorio.addJugador(ipJugador[0], ipJugador[1], Integer.parseInt(ipJugador[2].toString()));
                System.out.println("[Controlador] Pizarra notificó JUGADOR_UNIDO.");

                int numJugadores = pizarra.getOrdenDeTurnos().size();

                if (numJugadores == 4) {
                    String idHost = pizarra.getOrdenDeTurnos().get(0);
                    System.out.println("[Controlador] Hay " + numJugadores + " jugadores. Pidiendo al Host (" + idHost + ") que inicie el juego si lo desea.");
                    enviarMensajeDirecto(idHost, "COMANDO_INICIAR_PARTIDA");
                }
                break;

            case "JUGADOR_LISTO":
                System.out.println("[Controlador] Pizarra notificó JUGADOR_LISTO. Verificando condición de inicio por aceptación.");

                EstadoJuegoPizarra estadoPizarra = (EstadoJuegoPizarra) pizarra;

                // 1. OBTENER EL ESTADO SERIALIZADO Y ENVIARLO A TODOS (SINCRONIZACIÓN)
                String estadoListosPayload = estadoPizarra.getJugadoresListosSerializado();
                enviarATodos("SALA_ACTUALIZADA:" + estadoListosPayload);

                // 2. VERIFICAR CONDICIÓN DE INICIO
                if (estadoPizarra.cumplenCondicionInicioPorListo()) { // Condición: Todos listos...
                    if (pizarra.iniciarPartidaSiCorresponde()) {
                        estadoPizarra.notificarObservadores("EVENTO_PARTIDA_INICIADA");
                    } else {
                        System.err.println("[Controlador] No se pudo iniciar la partida, pero la condición de LISTO se cumplió.");
                    }
                }
                break;

            case "EVENTO_PARTIDA_INICIADA":
                System.out.println("[Controlador] EVENTO_PARTIDA_INICIADA: Inicializando reparto y estado.");

                EstadoJuegoPizarra estadoPizarra2 = (EstadoJuegoPizarra) pizarra;

                // 2. OBTENER LA LISTA DE JUGADORES REGISTRADOS DESDE LA PIZARRA
                List<String> idsJugadores = estadoPizarra2.getOrdenDeTurnos();

                // 3. REPARTIR FICHAS Y OBTENER MANOS SERIALIZADAS (CORRECCIÓN CRUCIAL)
                // Se llama con el argumento requerido: List<String>
                Map<String, String> manosSerializadas = agentePartida.repartirManos(idsJugadores);

                // 4. ENVIAR MANO INICIAL A CADA JUGADOR (Lógica de sincronización)
                for (Map.Entry<String, String> entry : manosSerializadas.entrySet()) {
                    String idJugador = entry.getKey();
                    String manoPayload = entry.getValue();
                    
                    int mazoCount = estadoPizarra2.getMazoSerializadoCount();

                    // El cliente espera: MANO_INICIAL:ManoData$MazoCount
                    enviarMensajeDirecto(idJugador, "MANO_INICIAL:" + manoPayload + "$" + mazoCount); // CORRECCIÓN
                    System.out.println("[Controlador] Mano inicial enviada a: " + idJugador);
                }

                // 5. ENVIAR ESTADO FINAL Y TURNO A TODOS
                notificarCambioDeTurno(pizarra);
                break;

            case "MOVIMIENTO":
                String mensajeMovimiento = "MOVIMIENTO_RECIBIDO:" + ultimoPayload;
                System.out.println("[Controlador] Reenviando MOVIMIENTO (temporal) a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimiento);
                break;

            case "AVANZAR_TURNO":
                System.out.println("[Controlador] Evento AVANZAR_TURNO detectado.");

                String mensajeMovimientoFinal = "ESTADO_FINAL_TABLERO:" + ultimoPayload;
                System.out.println("[Controlador] Transmitiendo ESTADO_FINAL_TABLERO a inactivos.");
                enviarATurnosInactivos(jugadorQueMovio, mensajeMovimientoFinal);

                notificarCambioDeTurno(pizarra);
                break;

            case "TOMAR_FICHA":
                System.out.println("[Controlador] " + jugadorQueMovio + " tomó ficha.");
                String fichaSerializada = pizarra.tomarFichaDelMazoSerializado();

                if (fichaSerializada != null) {
                    enviarMensajeDirecto(jugadorQueMovio, "FICHA_RECIBIDA:" + fichaSerializada);
                    // 2. NUEVO: Actualizar contador en pizarra (+1 ficha)
                    ((EstadoJuegoPizarra) pizarra).incrementarFichasJugador(jugadorQueMovio);
                }

                pizarra.avanzarTurno();
                break;

            default:
                System.err.println("[Controlador] Evento desconocido: " + evento);
        }
    }

    /**
     * Lee el jugador que tiene el turno actual y el conteo de fichas restantes
     * en el mazo, y notifica esta información a todos los clientes.
     *
     * @param pizarra La Pizarra de Juego para obtener el estado actual.
     */
    @Override
    public void notificarCambioDeTurno(iPizarraJuego pizarra) {
        String nuevoJugadorEnTurno = pizarra.getJugador();
        int mazoCount = ((EstadoJuegoPizarra) pizarra).getMazoSerializadoCount();

        String contadores = ((EstadoJuegoPizarra) pizarra).getFichasJugadoresSerializado();
        if (nuevoJugadorEnTurno != null) {
            System.out.println("[Controlador] Notificando cambio de turno a: " + nuevoJugadorEnTurno);
            String mensajeTurno = "TURNO_CAMBIADO:" + nuevoJugadorEnTurno + ":" + mazoCount + ":" + contadores;
            enviarATodos(mensajeTurno);
        }
    }

    /**
     * Envía un mensaje a todos los jugadores registrados en el directorio.
     *
     * Se usa para eventos globales como: inicio de partida, cambio de turno, y
     * el estado final del tablero.
     *
     * @param mensaje El contenido serializado del mensaje a enviar.
     */
    @Override
    public void enviarATodos(String mensaje) {
        System.out.println("[Controlador] Preparando envío a TODOS de: " + mensaje);
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

    /**
     * Envía un mensaje solo a los jugadores que no realizaron la jugada.
     *
     * Se utiliza para enviar movidas temporales a los observadores, o enviar el
     * estado final del tablero a los oponentes.
     *
     * @param jugadorQueEnvio El ID del jugador que inició la acción.
     * @param mensaje El contenido serializado del mensaje a enviar.
     */
    @Override
    public void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje) {
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

    /**
     * Envía un mensaje directo a un jugador específico utilizando su ID.
     *
     * Se usa para acciones privadas: enviar mano inicial, enviar ficha tomada
     * del mazo, o comandos exclusivos (como la orden de INICIAR_PARTIDA al
     * host).
     *
     * @param idJugador El ID del jugador al que se enviará el mensaje.
     * @param mensaje El contenido serializado del mensaje a enviar.
     */
    @Override
    public void enviarMensajeDirecto(String idJugador, String mensaje) {
        try {
            iDirectorio.ClienteInfoDatos destino = directorio.getClienteInfo(idJugador);
            if (destino != null) {
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
