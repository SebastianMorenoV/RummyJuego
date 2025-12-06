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
            case "CANDIDATO_ACEPTADO_DIRECTAMENTE":
                String[] datosAceptado = pizarra.getCandidatoTemporal();
                if (datosAceptado != null) {
                    String id = datosAceptado[0];
                    System.out.println("[Controlador] El jugador " + id + " entró directo (Host).");
                    // Le avisamos al cliente que entre a la sala de espera
                    enviarMensajeDirecto(id, "UNION_ACEPTADA");
                }
                break;
            case "SOLICITUD_ENTRANTE":
                String[] datos = pizarra.getCandidatoTemporal();
                String idCandidato = datos[0];
                String ip = datos[1];
                int puerto = Integer.parseInt(datos[2]);

                directorio.agregarCandidato(idCandidato, ip, puerto);

                System.out.println("[Controlador] Pizarra aceptó solicitud. Enviando petición de votos.");

                enviarATodos("PETICION_VOTO:" + idCandidato);
                break;

            case "SOLICITUD_RECHAZADA_INICIADA":
                manejarRechazoInmediato(pizarra, "ERROR_PARTIDA_INICIADA");
                break;

            case "SOLICITUD_RECHAZADA_LLENA":
                manejarRechazoInmediato(pizarra, "UNION_RECHAZADA");
                break;

            case "SOLICITUD_RECHAZADA_VACIA":
                manejarRechazoInmediato(pizarra, "UNION_RECHAZADA");
                break;

            case "SOLICITUD_RECHAZADA_OCUPADO":
                String[] rechazado = pizarra.getCandidatoRechazado();
                if (rechazado != null) {
                    registrarYRechazar(rechazado[0], rechazado[1], Integer.parseInt(rechazado[2]), "ERROR_VOTACION_EN_CURSO");
                }
                break;

            case "VOTACION_FINALIZADA":
                String[] resultado = pizarra.getUltimoResultadoVotacion();
                if (resultado != null) {
                    String id = resultado[0];
                    if (pizarra.isVotacionAprobada()) {
                        // Recuperar info para registrar
                        iDirectorio.ClienteInfoDatos info = directorio.getCandidatoInfo(id);
                        if (info != null) {
                            // Registrar dispara JUGADOR_UNIDO después
                            pizarra.registrarJugador(id, info.getHost() + "$" + info.getPuerto());
                            enviarMensajeCandidato(id, "UNION_ACEPTADA");
                        }
                        directorio.removerCandidato(id);
                    } else {
                        enviarMensajeCandidato(id, "UNION_RECHAZADA");
                        directorio.removerCandidato(id);
                    }
                }
                break;
            case "JUGADOR_UNIDO":
                String[] ipJugador = pizarra.getIpCliente();
                // 1. Registrar oficialmente
                directorio.addJugador(ipJugador[0], ipJugador[1], Integer.parseInt(ipJugador[2]));
                System.out.println("[Controlador] Jugador " + ipJugador[0] + " unido.");

                // 2. CONSTRUIR LISTA DE JUGADORES
                StringBuilder listaNombres = new StringBuilder();
                for (String id : directorio.getAllClienteInfo().keySet()) {
                    listaNombres.append(id).append(",");
                }
                if (listaNombres.length() > 0) {
                    listaNombres.setLength(listaNombres.length() - 1); // Quitar última coma
                }

                // 3. ENVIAR A TODOS (Broadcast)
                enviarATodos("ACTUALIZAR_LISTA:" + listaNombres.toString());
                break;

            case "EVENTO_PARTIDA_INICIADA":
                System.out.println("[Controlador] Evento PARTIDA_INICIADA detectado. Creando juego...");

                List<String> jugadoresIds = pizarra.getOrdenDeTurnos();
                System.out.println("[Controlador] Repartiendo para " + jugadoresIds.size() + " jugadores.");

                Map<String, String> manosSerializadas = agentePartida.repartirManos(jugadoresIds);
                String mazoSerializado = agentePartida.getMazoSerializado();
                int mazoCount = mazoSerializado.split("\\|").length;

                agentePartida.setMazoSerializado(mazoSerializado);

                for (Map.Entry<String, String> entry : manosSerializadas.entrySet()) {
                    String idJugador = entry.getKey();
                    String manoPayload = entry.getValue();

                    String mensajeMano = "MANO_INICIAL:" + manoPayload + "$" + mazoCount;
                    enviarMensajeDirecto(idJugador, mensajeMano);
                }

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

    private void enviarMensajeCandidato(String idCandidato, String mensaje) {
        try {
            iDirectorio.ClienteInfoDatos destino = directorio.getCandidatoInfo(idCandidato);
            if (destino != null) {
                System.out.println("[Controlador] Respondiendo a candidato " + idCandidato + ": " + mensaje);
                this.despachador.enviar(destino.getHost(), destino.getPuerto(), mensaje);
            } else {
                System.err.println("[Controlador] No se encontró al candidato " + idCandidato + " en el directorio.");
            }
        } catch (IOException e) {
            System.err.println("[Controlador] Error al enviar a candidato: " + e.getMessage());
        }
    }

    private void manejarRechazoInmediato(iPizarraJuego pizarra, String mensajeError) {
        String[] datos = pizarra.getCandidatoTemporal();
        if (datos != null) {
            String id = datos[0];
            String ip = datos[1];
            int puerto = Integer.parseInt(datos[2]);

            registrarYRechazar(id, ip, puerto, mensajeError);
        }
    }

    private void registrarYRechazar(String id, String ip, int puerto, String msg) {
        directorio.agregarCandidato(id, ip, puerto);
        enviarMensajeCandidato(id, msg);
        directorio.removerCandidato(id);
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
