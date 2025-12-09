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

    int limiteFichas = 13; // Valor default Rummy

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
        // 1. Separar evento de posibles argumentos (ID)
        String eventoPuro = evento;
        String idAfectado = "";

        if (evento.contains(":")) {
            String[] partes = evento.split(":", 2); // Dividir en máximo 2 partes
            eventoPuro = partes[0];
            idAfectado = partes[1];
        }
        switch (eventoPuro) {
            case "SOLICITUD_ENTRANTE":
                String[] datos = pizarra.getCandidatoTemporal();
                String idCandidato = datos[0];
                String ip = datos[1];
                int puerto = Integer.parseInt(datos[2]);

                directorio.agregarCandidato(idCandidato, ip, puerto);

                System.out.println("[Controlador] Pizarra aceptó solicitud. Enviando petición de votos a la sala.");

                List<String> jugadoresEnSala = pizarra.getOrdenDeTurnos();
                
                for (String idJugador : jugadoresEnSala) {
                    enviarMensajeDirecto(idJugador, "PETICION_VOTO:" + idCandidato);
                }
                break;

            case "SOLICITUD_RECHAZADA_INICIADA":
                manejarRechazoInmediato(pizarra, "ERROR_PARTIDA_INICIADA");
                break;

            case "SOLICITUD_RECHAZADA_LLENA":
                manejarRechazoInmediato(pizarra, "ERROR_SALA_LLENA");
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
                        iDirectorio.ClienteInfoDatos info = directorio.getCandidatoInfo(id);
                        if (info != null) {
                            enviarMensajeCandidato(id, "UNION_ACEPTADA");
                        }
                        directorio.removerCandidato(id);
                    } else {
                        enviarMensajeCandidato(id, "UNION_RECHAZADA");
                        directorio.removerCandidato(id);
                    }
                }
                break;
            case "SOLICITUD_RECHAZADA_VACIA":
                manejarRechazoInmediato(pizarra, "SOLICITUD_RECHAZADA_VACIA");
                break;
            case "ACCESO_DENEGADO":
                enviarMensajeDirecto(idAfectado, "ACCESO_DENEGADO");
                break;

            case "REGISTRO_EXITOSO":
                System.out.println("[Controlador] Confirmando registro a " + idAfectado);
                enviarMensajeDirecto(idAfectado, "REGISTRO_EXITOSO");
                String listaActualizada = ((EstadoJuegoPizarra) pizarra).getMetadatosJugadores();
                enviarATodos("ACTUALIZAR_SALA:" + listaActualizada);
                break;
            case "PERMISO_CREAR":
                enviarMensajeDirecto(idAfectado, "PUEDES_CONFIGURAR");
                break;
            case "PARTIDA_EXISTENTE":
                enviarMensajeDirecto(idAfectado, "PARTIDA-EXISTENTE");
                break;
            case "CONFIGURAR_PARTIDA":
                // 1. Usamos idAfectado (que viene del evento) en lugar de buscar en la lista vacía
                String idHost = idAfectado;

                if (idHost != null && !idHost.isEmpty()) {
                    // 2. Buscamos en el Directorio (donde ya debería estar gracias al Paso 1)
                    iDirectorio.ClienteInfoDatos datosHost = directorio.getClienteInfo(idHost);

                    if (datosHost != null) {
                        String ipJugador = datosHost.getHost();
                        System.out.println("[Controlador] IP obtenida del Host (" + idHost + "): " + ipJugador);

                        // 3. Enviamos el éxito solo al creador
                        enviarMensajeDirecto(idHost, "PARTIDA-CREADA-EXITO");
                    } else {
                        System.err.println("[Controlador] Error: No se encontró info de red para el jugador " + idHost);
                    }
                } else {
                    System.err.println("[Controlador] Error: ID de Host nulo en evento CONFIGURAR_PARTIDA");
                }
                break;
            case "REGISTRAR_CANDIDATO":
                String[] datosCandidato = pizarra.getIpCliente();
                if (datosCandidato != null) {
                    directorio.addJugador(
                            datosCandidato[0], // ID
                            datosCandidato[1], // IP
                            Integer.parseInt(datosCandidato[2]) // Puerto
                    );
                    System.out.println("[Controlador] Candidato añadido al Directorio: " + datosCandidato[0]);
                }
                break;
            case "JUGADOR_UNIDO":
                String[] datosJugador = pizarra.getIpCliente();
                String id = datosJugador[0];

                String avatar = datosJugador[3];

                int c1 = Integer.parseInt(datosJugador[4]);
                int c2 = Integer.parseInt(datosJugador[5]);
                int c3 = Integer.parseInt(datosJugador[6]);
                int c4 = Integer.parseInt(datosJugador[7]);

                directorio.addJugador(datosJugador[0], datosJugador[1], Integer.parseInt(datosJugador[2]));
                System.out.println("[Controlador] Jugador registrado: " + id);

                String confirmacion = "CONFIRMACION_REGISTRO:" + id + ":" + avatar + ":"
                        + c1 + ":" + c2 + ":" + c3 + ":" + c4;
                enviarMensajeDirecto(id, confirmacion);

                String listaJugadores = ((EstadoJuegoPizarra) pizarra).getMetadatosJugadores();

                // Protocolo: "ACTUALIZAR_SALA:Jugador1,Avatar1;Jugador2,Avatar2"
                enviarATodos("ACTUALIZAR_SALA:" + listaJugadores);

                break;

            case "NOMBRE_REPETIDO":
                System.out.println("[Controlador] Nombre repetido detectado para " + idAfectado);
                enviarMensajeDirecto(idAfectado, "NOMBRE_REPETIDO");
                break;

            case "EVENTO_PARTIDA_INICIADA":
                System.out.println("[Controlador] Evento PARTIDA_INICIADA detectado. Creando juego...");
                List<String> jugadoresIds = pizarra.getOrdenDeTurnos();

                String listaJugadoresString = String.join(",", jugadoresIds);

                String[] configuracion = pizarra.getConfiguracionPartida();
                int numFichas = Integer.parseInt(configuracion[1]);
                int numComodines = Integer.parseInt(configuracion[0]);
                Map<String, String> manosSerializadas = agentePartida.repartirManos(jugadoresIds, numFichas, numComodines);

                String mazoSerializado = agentePartida.getMazoSerializado();

                // Evitamos error si el mazo está vacío o string vacio
                int mazoCount = 0;
                if (mazoSerializado != null && !mazoSerializado.isEmpty()) {
                    mazoCount = mazoSerializado.split("\\|").length;
                }

                agentePartida.setMazoSerializado(mazoSerializado);

                // Mandamos los metadatos completos (Nombres, Avatares, etc.)
                String listaJugadoresCompleta = pizarra.getMetadatosJugadores();

                // Datos al cliente
                for (Map.Entry<String, String> entry : manosSerializadas.entrySet()) {
                    String idJugador = entry.getKey();
                    String manoPayload = entry.getValue();

                    String mensajeMano = "MANO_INICIAL:" + manoPayload + "$" + mazoCount + "$" + listaJugadoresString;

                    enviarMensajeDirecto(idJugador, mensajeMano);
                }

                notificarCambioDeTurno(pizarra);
                break;

            case "PERMISO_UNIRSE":
                if (idAfectado != null && !idAfectado.isEmpty()) {
                    System.out.println("[Controlador] Autorizando ingreso a: " + idAfectado);
                    // Enviamos la señal para que el cliente vaya a la pantalla de Registro
                    enviarMensajeDirecto(idAfectado, "UNIRSE_PARTIDA");
                }
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
                    ((EstadoJuegoPizarra) pizarra).incrementarFichasJugador(jugadorQueMovio);
                }
                pizarra.avanzarTurno();
                break;
            //ESTI TIENE QUE SER CAMBIADO.
//            case "PERMISO_CREAR":
//                if (idAfectado == null || idAfectado.isEmpty()) {
//                    System.err.println("[Error Controlador] ID afectado es nulo en PERMISO_CREAR");
//                    break;
//                }
//                System.out.println("[Controlador] Enviando autorización a: " + idAfectado);
//                enviarMensajeDirecto(idAfectado, "CREAR_PARTIDA");
//                break;

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

    public synchronized int getLimiteFichas() {
        return this.limiteFichas;
    }

    public synchronized boolean isPartidaConfigurada() {
        return true; // Siempre true, porque usamos defaults
    }

    public void manejarRechazoInmediato(iPizarraJuego pizarra, String mensajeError) {
        String[] datos = pizarra.getCandidatoTemporal();
        if (datos != null) {
            String id = datos[0];
            String ip = datos[1];
            int puerto = Integer.parseInt(datos[2]);

            registrarYRechazar(id, ip, puerto, mensajeError);
        }
    }

    public void registrarYRechazar(String id, String ip, int puerto, String msg) {
        directorio.agregarCandidato(id, ip, puerto);
        enviarMensajeCandidato(id, msg);
        directorio.removerCandidato(id);
    }

    public void enviarMensajeCandidato(String idCandidato, String mensaje) {
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
}
