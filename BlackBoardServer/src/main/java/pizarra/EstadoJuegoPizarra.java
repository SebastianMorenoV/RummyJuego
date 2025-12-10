package pizarra;

import contratos.iObservador;
import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * El Pizarrón (Blackboard) "tonto". Solo guarda datos (muchos como Strings) y
 * notifica al Controlador cuando hay un cambio de estado relevante. Es el
 * centro de datos y coordinación de eventos del lado del servidor.
 *
 * @author chris
 */
public class EstadoJuegoPizarra implements iPizarraJuego {

    private final List<iObservador> observadores;

    private Map<String, String> datosJugadores = new HashMap<>();

    private Map<String, String> perfilesJugadores = new HashMap<>();

    private String ultimoTableroSerializado = "";
    private final List<String> ordenDeTurnos;
    private String ultimoJugadorQueMovio;
    private int indiceTurnoActual;
    private String[] jugadorARegistrarTemporal;
    private String[] configuracionPartida;
    private boolean partidaConfigurada = false;
    private String mazoSerializado;
    private int numeroDeJugadoresRegistrados;
    private Map<String, Integer> fichasPorJugador = new HashMap<>();
    private Map<String, String> candidatos = new HashMap<>();
    private List<String> jugadoresListos = new ArrayList<>();
    private String[] candidatoTemporal;
    private String[] candidatoRechazado;
    private String[] ultimoResultadoVotacion;
    private boolean votacionEnCurso = false;
    private int votosAfirmativos = 0;
    private int votosRecibidos = 0;
    private int totalVotantesEsperados = 0;
    private boolean votacionAprobada = false;
    private boolean votacionInicioEnCurso = false;
    private int votosInicioAfirmativos = 0;
    private int votosInicioRecibidos = 0;
    private int totalVotantesInicio = 0;
    private String idSolicitanteInicio = "";
    private Map<String, Integer> puntajesFinales = new ConcurrentHashMap<>();
    private boolean recolectandoPuntajes = false;

    private String ultimoMensajeChat = "";

    private Map<String, Integer> votosTerminar = new ConcurrentHashMap<>();
    private boolean votacionTerminarEnCurso = false;

    public EstadoJuegoPizarra() {
        this.ordenDeTurnos = Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1;
        this.observadores = new ArrayList<>();
        this.mazoSerializado = "";
        this.numeroDeJugadoresRegistrados = 0;
    }

    /**
     * Agrega un nuevo observador (normalmente el ControladorBlackboard) para
     * recibir notificaciones de eventos de la Pizarra.
     *
     * @param obs El observador a registrar.
     */
    public void addObservador(iObservador obs) {
        if (obs != null && !this.observadores.contains(obs)) {
            this.observadores.add(obs);
        }
    }

    /**
     * Notifica a todos los observadores registrados sobre un evento que ha
     * ocurrido en la Pizarra.
     *
     * @param evento El nombre del evento ocurrido (p.ej., "JUGADOR_UNIDO").
     */
    private void notificarObservadores(String evento) {
        for (iObservador obs : this.observadores) {
            obs.actualiza(this, evento);
        }
    }

    /**
     * Registra temporalmente la información de conexión de un nuevo jugador
     * (ID, IP y Puerto) para que el Controlador/Directorio la procese. El ID
     * del jugador también se añade a la orden de turnos.
     *
     * @param id El ID del jugador a registrar.
     * @param payloadMano El payload que contiene la IP y el Puerto del cliente.
     */
    @Override
    public void registrarJugador(String id, String payloadMano) {
        this.datosJugadores.put(id, payloadMano);
        String[] partes = payloadMano.split("\\$");

        this.jugadorARegistrarTemporal = new String[10];
        this.jugadorARegistrarTemporal[0] = id;

        if (partes.length > 0) {
            jugadorARegistrarTemporal[1] = partes[0]; // IP
        }
        if (partes.length > 1) {
            jugadorARegistrarTemporal[2] = partes[1]; // Puerto
        }

        // Solo intentamos leer el resto si el payload es "completo" (viene del registro de usuario)
        if (partes.length > 2) {
            jugadorARegistrarTemporal[3] = partes[2]; // Avatar
        }
        if (partes.length > 3) {
            jugadorARegistrarTemporal[4] = partes[3]; // Colores
        }
        if (partes.length > 4) {
            jugadorARegistrarTemporal[5] = partes[4];
        }
        if (partes.length > 5) {
            jugadorARegistrarTemporal[6] = partes[5];
        }
        if (partes.length > 6) {
            jugadorARegistrarTemporal[7] = partes[6];
        }

        ordenDeTurnos.add(id);

        // verificar elementos (ids)
        for (String jugador : ordenDeTurnos) {
            if (jugadorARegistrarTemporal[1].equals(jugador)) {
                notificarObservadores("NOMBRE_REPETIDO");
            }
        }

        //validacion
        notificarObservadores("REGISTRAR_CANDIDATO");
        notificarObservadores("JUGADOR_UNIDO");

        jugadorARegistrarTemporal = null;
        candidatos.remove(id);

    }

    // Permite al Controlador obtener el payload completo (Avatar, Colores) de un jugador por su ID
    public String getDatosJugador(String idJugador) {
        return this.datosJugadores.get(idJugador);

    }

    private void limpiarSalaTotalmente() {
        this.indiceTurnoActual = -1;
        this.mazoSerializado = "";
        this.ultimoTableroSerializado = "";
        this.ultimoJugadorQueMovio = "";

        // --- AQUÍ ESTÁ EL CAMBIO CLAVE ---
        this.ordenDeTurnos.clear(); // ¡Expulsamos a todos de la sala lógica!
        this.perfilesJugadores.clear();
        this.datosJugadores.clear();
        this.numeroDeJugadoresRegistrados = 0;
        // ---------------------------------

        this.fichasPorJugador.clear();
        this.votosTerminar.clear();
        this.puntajesFinales.clear();

        this.recolectandoPuntajes = false;
        this.votacionTerminarEnCurso = false;
        this.partidaConfigurada = false;

        System.out.println("[Pizarra] ---------------------------------------------");
        System.out.println("[Pizarra] ¡SALA DISUELTA! Todos los jugadores han sido removidos.");
        System.out.println("[Pizarra] ---------------------------------------------");
    }

    /**
     * Verifica si actualmente es el turno del jugador indicado.
     *
     * @param id El ID del jugador a verificar.
     * @return true si el ID coincide con el jugador en el turno actual, false
     * en caso contrario.
     */
    @Override
    public synchronized boolean esTurnoDe(String id) {
        if (indiceTurnoActual == -1) {
            return false;
        }
        return ordenDeTurnos.get(indiceTurnoActual).equals(id);
    }

    /**
     * Metodo para configurar la partida, guarda los comodines y numero de
     * fichas para que el Agente pueda usarlos.
     *
     * @param idCliente
     * @param payload
     */
    public void configurarPartida(String idCliente, String payload) {
        ordenDeTurnos.add(idCliente);
        configuracionPartida = new String[2];

        String[] partes = payload.split("\\$");

        if (partes.length >= 2) {
            configuracionPartida[0] = partes[0];
            configuracionPartida[1] = partes[1];
            partidaConfigurada = true;
            System.out.println("[Pizarra] Configuración guardada: " + partes[0] + " comodines, " + partes[1] + " fichas.");
            notificarObservadores("CONFIGURAR_PARTIDA:" + idCliente);
        } else {
            System.err.println("[Pizarra] Error: Payload de configuración incompleto: " + payload);
        }
    }

    /**
     * Avanza el índice de turno al siguiente jugador en el orden preestablecido
     * y notifica a los observadores que el turno avanzó. Si no hay partida
     * iniciada, no hace nada.
     */
    @Override
    public synchronized void avanzarTurno() {
        if (indiceTurnoActual != -1) {
            indiceTurnoActual = (indiceTurnoActual + 1) % ordenDeTurnos.size();
            String idSiguiente = ordenDeTurnos.get(indiceTurnoActual);
            System.out.println("[Pizarra] Turno de: " + idSiguiente);
            notificarObservadores("AVANZAR_TURNO");
        }
    }

    /**
     * Valida las condiciones (2 a 4 jugadores) e inicia la partida,
     * estableciendo el índice de turno a cero.
     *
     * @return true si la partida se inicia correctamente, false si ya estaba
     * iniciada o no cumple con el número de jugadores.
     */
    @Override
    public synchronized boolean iniciarPartidaSiCorresponde() {
        int numJugadores = ordenDeTurnos.size();

        // MOCK: Se cambio para permitir iniciar con 1 para pruebas si es necesario, pero la regla es 2-4
        if (indiceTurnoActual == -1 && numJugadores >= 1 && numJugadores <= 4) {
            indiceTurnoActual = 0; // Inicia el turno del primer jugador
            String idPrimerJugador = ordenDeTurnos.get(0);
            System.out.println("[Pizarra] ¡Partida iniciada! " + numJugadores + " jugadores.");
            System.out.println("[Pizarra] Turno de: " + idPrimerJugador);
            return true;
        }

        if (indiceTurnoActual != -1) {
            System.err.println("[Pizarra] Intento de iniciar partida, pero ya estaba iniciada.");
        } else {
            System.err.println("[Pizarra] Intento de iniciar partida con " + numJugadores + " jugadores. Se requieren 2-4.");
        }
        return false;
    }

    /**
     * Obtiene la información temporal (ID, IP y Puerto) del último cliente que
     * se intentó registrar.
     *
     * @return Un array de String con la información del cliente.
     */
    @Override
    public String[] getIpCliente() {
        return jugadorARegistrarTemporal;
    }

    /**
     * Obtiene el ID del jugador que tiene el turno actual.
     *
     * @return El ID del jugador, o null si la partida no ha iniciado.
     */
    @Override
    public String getJugador() {
        if (indiceTurnoActual == -1 || ordenDeTurnos.isEmpty()) {
            return null;
        }
        return ordenDeTurnos.get(indiceTurnoActual);
    }

    /**
     * Obtiene la última cadena serializada del tablero que fue enviada por un
     * cliente. Se usa para comunicar movimientos (temporales o finales) a otros
     * jugadores.
     *
     * @return La cadena serializada del tablero.
     */
    @Override
    public String getUltimoTableroSerializado() {
        return this.ultimoTableroSerializado;
    }

    /**
     * Obtiene el ID del último jugador que ejecutó un comando de movimiento.
     *
     * @return El ID del jugador.
     */
    @Override
    public String getUltimoJugadorQueMovio() {
        return this.ultimoJugadorQueMovio;
    }

    /**
     * Procesa comandos enviados por los clientes, y notifica al controlador
     * cuando ocurrió un evento relevante.
     *
     * @param idCliente
     * @param comando
     * @param payload
     */
    @Override
    public void procesarComando(String idCliente, String comando, String payload) {
        switch (comando) {

            case "CHAT":
                // Payload llega como: "NombreEmisor:Mensaje"
                this.ultimoMensajeChat = payload;
                System.out.println("[Pizarra] Chat recibido de " + idCliente + ": " + payload);
                notificarObservadores("NUEVO_MENSAJE_CHAT");
                break;
            case "GANADOR":
                // Un jugador dice que ganó (0 fichas).
                // Iniciamos la recolección de puntos de TODOS para armar la tabla real.
                System.out.println("[Pizarra] " + idCliente + " reclama victoria. Solicitando puntajes a todos...");
                iniciarRecoleccionPuntajes(idCliente);
                break;

            case "ENVIO_PUNTAJE":
                // Payload esperado: Puntos (int)
                if (recolectandoPuntajes) {
                    try {
                        int puntos = Integer.parseInt(payload);
                        registrarPuntajeFinal(idCliente, puntos);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing puntaje de " + idCliente);
                    }
                }
                break;

            case "SOLICITAR_TERMINAR":
                // Votación para acabar antes
                // Payload: Puntos del solicitante (voto implícito SI)
                try {
                    int puntos = Integer.parseInt(payload);
                    iniciarVotacionTerminar(idCliente, puntos);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing puntos solicitud: " + payload);
                }
                break;

            case "VOTO_TERMINAR":
                if (votacionTerminarEnCurso) {
                    String[] partes = payload.split(":");
                    boolean acepta = "SI".equals(partes[0]);
                    int puntos = 0;
                    if (acepta && partes.length > 1) {
                        puntos = Integer.parseInt(partes[1]);
                    }
                    recibirVotoTerminar(idCliente, acepta, puntos);
                }
                break;
            case "SOLICITAR_UNIRSE":
                registrarCandidato(idCliente, payload);
                break;

            case "RESPUESTA_VOTO":
                if (!votacionEnCurso) {
                    return;
                }

                boolean voto = Boolean.parseBoolean(payload);
                votosRecibidos++;
                if (voto) {
                    votosAfirmativos++;
                }

                System.out.println("[Pizarra] Voto recibido de " + idCliente + ": " + voto
                        + " (" + votosRecibidos + "/" + totalVotantesEsperados + ")");

                if (votosRecibidos >= totalVotantesEsperados) {
                    finalizarConteo();
                }
                break;
        }
        if (indiceTurnoActual == -1) {
            switch (comando) {

                case "SOLICITAR_CREACION":
                    if (this.partidaConfigurada || !ordenDeTurnos.isEmpty()) {
                        notificarObservadores("PARTIDA_EXISTENTE:" + idCliente);
                    } else {
                        almacenarUsuarioTemporal(idCliente, payload);
                        notificarObservadores("PERMISO_CREAR:" + idCliente);
                    }
                    break;
                case "UNIRSE_PARTIDA":
                    if (this.partidaConfigurada) {
                        String payloadJugador = candidatos.get(idCliente);
                        if (payload != null && !payload.isEmpty()) {
                            payloadJugador = payload;
                        }

                        if (payloadJugador != null) {
                            registrarJugador(idCliente, payloadJugador);

                            if (ordenDeTurnos.size() == 2) {
                                System.out.println("[Pizarra] Sala lista para iniciar.");
                            }
                        }
                    } else {
                        notificarObservadores("ACCESO_DENEGADO:" + idCliente);
                        System.out.println("[Pizarra] " + idCliente + " intentó unirse pero no hay partida configurada.");
                    }
                    break;
                case "REGISTRAR":
                    almacenarUsuarioTemporal(idCliente, payload);
                    break;

                case "INICIAR_PARTIDA":
                    System.out.println("[Pizarra] Recibido comando INICIAR_PARTIDA de " + idCliente);
                    if (iniciarPartidaSiCorresponde()) {
                        notificarObservadores("EVENTO_PARTIDA_INICIADA");
                    }
                    break;

                case "CONFIGURAR_PARTIDA":
                    System.out.println("Configurando partida... Promoviendo al Host: " + idCliente);
                    String payloadHost = candidatos.get(idCliente);

                    if (payloadHost != null) {
//                        registrarJugador(idCliente, payloadHost);
                        configurarPartida(idCliente, payload);
                    } else {
                        System.err.println("[Error] El usuario " + idCliente + " no estaba en la lista de candidatos.");
                    }
                    break;

                case "ACTUALIZAR_PERFIL":
                    System.out.println("[Pizarra] Verificando perfil de " + idCliente);

                    String[] partesNuevas = payload.split("\\$");
                    String nuevoNombre = (partesNuevas.length > 0) ? partesNuevas[0] : "";
                    boolean nombreOcupado = false;

                    for (String datosExistentes : perfilesJugadores.values()) {
                        String[] partesExistentes = datosExistentes.split("\\$");
                        String nombreExistente = (partesExistentes.length > 0) ? partesExistentes[0] : "";

                        if (nombreExistente.equalsIgnoreCase(nuevoNombre)) {
                            nombreOcupado = true;
                            break;
                        }
                    }

                    if (nombreOcupado) {
                        System.out.println("[Pizarra] Error: El nombre '" + nuevoNombre + "' ya está en uso.");
                        notificarObservadores("NOMBRE_REPETIDO:" + idCliente);
                    } else {
                        // Si está libre, procedemos normal
                        perfilesJugadores.put(idCliente, payload);

                        // --- AGREGA ESTO AQUÍ ---
                        // Añadimos al jugador a la lista oficial de la sala si no estaba ya
                        if (!ordenDeTurnos.contains(idCliente)) {
                            ordenDeTurnos.add(idCliente);
                        }
                        // ------------------------

                        System.out.println("[Pizarra] Registro exitoso para " + nuevoNombre);
                        notificarObservadores("REGISTRO_EXITOSO:" + idCliente);
                    }
                    numeroDeJugadoresRegistrados++;
                    if (ordenDeTurnos.size() == 4) {
                        if (iniciarPartidaSiCorresponde()) {
                            notificarObservadores("EVENTO_PARTIDA_INICIADA");
                        }
                    }
                    break;
                case "SOLICITAR_INICIO_PARTIDA":
                    iniciarVotacionInicioPartida(idCliente);
                    break;
                case "RESPUESTA_VOTO_INICIO":
                    boolean respuesta = Boolean.parseBoolean(payload);
                    recibirVotoInicio(respuesta);
                    break;

            }
        }

        if (indiceTurnoActual != -1) {
            switch (comando) {
                case "MOVER":
                    this.ultimoJugadorQueMovio = idCliente;
                    this.ultimoTableroSerializado = payload;
                    System.out.println("[Pizarra] " + idCliente + " movió (temporal).");
                    notificarObservadores("MOVIMIENTO");
                    break;

                case "FINALIZAR_TURNO":
                    this.ultimoJugadorQueMovio = idCliente;
                    String[] partesFinalizar = payload.split("#");
                    this.ultimoTableroSerializado = partesFinalizar[0];

                    // La parte 1 (si existe) es el número de fichas que le quedaron al jugador
                    if (partesFinalizar.length > 1) {
                        try {
                            int fichasRestantes = Integer.parseInt(partesFinalizar[1]);
                            setFichasJugador(idCliente, fichasRestantes);

                            System.out.println("[Pizarra] " + idCliente + " finalizó con " + fichasRestantes + " fichas.");
                        } catch (NumberFormatException e) {
                            System.err.println("[Pizarra] Error al leer número de fichas: " + e.getMessage());
                        }
                    }

                    System.out.println("[Pizarra] " + idCliente + " finalizó turno.");
                    avanzarTurno();
                    break;

                case "TOMAR_FICHA":
                    this.ultimoJugadorQueMovio = idCliente;
                    this.ultimoTableroSerializado = payload;
                    System.out.println("[Pizarra] " + idCliente + " pidió tomar ficha.");
                    notificarObservadores("TOMAR_FICHA");
                    break;

                default:
                    if (!comando.equals("REGISTRAR") && !comando.equals("INICIAR_PARTIDA")) {
                        System.err.println("[Pizarra] Comando desconocido o fuera de lugar: " + comando);
                    }
                    break;

            }
        }
    }

    public void almacenarUsuarioTemporal(String idCliente, String payload) {
        candidatos.put(idCliente, payload);

        jugadorARegistrarTemporal = new String[3];
        String[] partes = payload.split("\\$", 2);
        jugadorARegistrarTemporal[0] = idCliente;
        jugadorARegistrarTemporal[1] = partes[0]; // IP
        jugadorARegistrarTemporal[2] = partes[1]; // Puerto

        System.out.println("[Pizarra] Candidato registrado en sala de espera: " + idCliente);

        notificarObservadores("REGISTRAR_CANDIDATO");
    }

    /**
     * NUEVO: Genera la cadena que contiene la info de TODOS los jugadores para
     * que MVCJuego sepa a quién pintar. Formato de salida:
     * "id1,payload1;id2,payload2;..."
     *
     * @return
     */
    @Override
    public String getMetadatosJugadores() {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        // Recorremos el orden de turnos para mantener la consistencia
        for (String id : ordenDeTurnos) {
            String datos = perfilesJugadores.get(id);
            if (datos != null) {
                // Separamos cada jugador por ";"
                // Formato interno: ID,DATOS (donde DATOS ya trae $ separadores)
                sb.append(id).append(",").append(datos);

                if (count < ordenDeTurnos.size() - 1) {
                    sb.append(";");
                }
                count++;
            }
        }
        return sb.toString();
    }

    public void recibirVotoInicio(boolean voto) {
        if (!votacionInicioEnCurso) {
            return;
        }

        votosInicioRecibidos++;
        if (voto) {
            votosInicioAfirmativos++;
        }

        System.out.println("[Pizarra] Voto inicio: " + voto + " (" + votosInicioRecibidos + "/" + totalVotantesInicio + ")");

        if (votosInicioRecibidos >= totalVotantesInicio) {
            votacionInicioEnCurso = false;

            if (votosInicioAfirmativos == totalVotantesInicio) {
                System.out.println("[Pizarra] Votación aprobada. Iniciando juego...");
                if (iniciarPartidaSiCorresponde()) {
                    notificarObservadores("EVENTO_PARTIDA_INICIADA");
                }
            } else {
                System.out.println("[Pizarra] Votación rechazada.");
                notificarObservadores("INICIO_PARTIDA_RECHAZADO");
            }

            votosInicioAfirmativos = 0;
            votosInicioRecibidos = 0;
        }
    }

    public void iniciarVotacionInicioPartida(String idSolicitante) {
        if (this.votacionInicioEnCurso) {
            return;
        }

        if (ordenDeTurnos.size() < 2) {
            System.out.println("[Pizarra] No se puede iniciar votación: Insuficientes jugadores.");
            return;
        }

        this.votacionInicioEnCurso = true;
        this.idSolicitanteInicio = idSolicitante;
        this.totalVotantesInicio = ordenDeTurnos.size();

        this.votosInicioAfirmativos = 1;
        this.votosInicioRecibidos = 1;

        System.out.println("[Pizarra] Iniciando votación de partida solicitada por: " + idSolicitante);
        notificarObservadores("SOLICITUD_INICIO_PARTIDA_ACTIVA:" + idSolicitante);
    }

    /**
     * Devuelve la lista de IDs de los jugadores en el orden en que se
     * repartirán los turnos.
     *
     * @return La lista de {@code String} con los IDs de los jugadores.
     */
    @Override
    public List<String> getOrdenDeTurnos() {
        return this.ordenDeTurnos;
    }

    private synchronized void iniciarRecoleccionPuntajes(String idGanador) {
        this.recolectandoPuntajes = true;
        this.puntajesFinales.clear();

        // El que ganó tiene 0 puntos automáticamente (ya que vació la mano)
        this.puntajesFinales.put(idGanador, 0);

        // Avisamos al controlador para que pida los puntos a los DEMÁS
        notificarObservadores("SOLICITAR_PUNTOS_A_TODOS:" + idGanador);
    }

    private synchronized void registrarPuntajeFinal(String idJugador, int puntos) {
        if (!recolectandoPuntajes) {
            return;
        }

        this.puntajesFinales.put(idJugador, puntos);
        System.out.println("[Pizarra] Puntaje recibido de " + idJugador + ": " + puntos);

        // Verificamos si ya tenemos los puntos de todos los jugadores en la partida
        if (puntajesFinales.size() >= ordenDeTurnos.size()) {
            finalizarPartidaConResultados();
        }
    }

    // --- MÉTODOS DE VOTACIÓN (Vote to End) ---
    public synchronized void iniciarVotacionTerminar(String idSolicitante, int puntosSolicitante) {
        if (votacionTerminarEnCurso || recolectandoPuntajes) {
            return;
        }

        this.votacionTerminarEnCurso = true;
        this.votosTerminar.clear();
        this.votosTerminar.put(idSolicitante, puntosSolicitante); // Voto SI implícito

        notificarObservadores("SOLICITUD_TERMINAR_ACTIVA:" + idSolicitante);
    }

    public synchronized void recibirVotoTerminar(String idVotante, boolean acepta, int puntos) {
        if (!votacionTerminarEnCurso) {
            return;
        }

        if (!acepta) {
            this.votacionTerminarEnCurso = false;
            this.votosTerminar.clear();
            notificarObservadores("VOTACION_TERMINAR_FALLIDA");
        } else {
            this.votosTerminar.put(idVotante, puntos);

            if (this.votosTerminar.size() >= this.ordenDeTurnos.size()) {
                // Si todos votan SI, usamos esos puntajes como finales
                this.puntajesFinales.putAll(this.votosTerminar);
                this.votacionTerminarEnCurso = false;
                finalizarPartidaConResultados();
            }
        }
    }

    // --- GENERACIÓN DE TABLA FINAL ---
    private void finalizarPartidaConResultados() {
        System.out.println("[Pizarra] Generando tabla de posiciones final...");

        // Detenemos la recolección para que nadie más envíe puntos tarde
        this.recolectandoPuntajes = false;

        StringBuilder sb = new StringBuilder();
        sb.append("=== RESULTADOS FINALES ===\n");
        sb.append("(Gana quien tenga MENOS puntos)\n\n");

        if (puntajesFinales.isEmpty()) {
            sb.append("No se registraron puntajes.");
        } else {
            // Ordenar: Menor puntaje = Mejor
            puntajesFinales.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(entry -> {
                        String id = entry.getKey();
                        String nombre = obtenerNombreJugador(id); // Convertimos ID a Nombre
                        sb.append(nombre).append(": ").append(entry.getValue()).append(" pts\n");
                    });

            // Encontrar al ganador
            String idGanador = puntajesFinales.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .get().getKey();

            sb.append("\n¡GANADOR: ").append(obtenerNombreJugador(idGanador)).append("!");
        }

        // 1. PRIMERO Enviamos resultados (mientras los datos de la sala siguen vivos)
        String tablaCompleta = sb.toString();
        notificarObservadores("PARTIDA_FINALIZADA:" + tablaCompleta);

        // 2. DESPUÉS limpiamos todo
        limpiarSalaTotalmente();
    }

    private String obtenerNombreJugador(String id) {
        if (perfilesJugadores.containsKey(id)) {
            String[] datos = perfilesJugadores.get(id).split("\\$");
            if (datos.length > 0) {
                return datos[0];
            }
        }
        return id;
    }

    /**
     * Almacena el estado "tonto" del mazo, serializado como una larga cadena de
     * texto.
     *
     * @param mazo La cadena serializada del mazo (fichas separadas por "|").
     */
    public void setMazoSerializado(String mazo) {
        this.mazoSerializado = mazo;
        System.out.println("[Pizarra] Mazo serializado almacenado.");
    }

    /**
     * Toma una ficha del mazo serializado "tonto" (extrae el primer elemento
     * separado por "|") y actualiza la cadena de mazo restante.
     *
     * @return La cadena serializada de la ficha tomada, o null si el mazo está
     * vacío.
     */
    @Override
    public String tomarFichaDelMazoSerializado() {
        if (this.mazoSerializado == null || this.mazoSerializado.isEmpty()) {
            return null;
        }

        String[] fichas = this.mazoSerializado.split("\\|", 2);
        String fichaTomada = fichas[0];
        this.mazoSerializado = (fichas.length > 1) ? fichas[1] : "";

        return fichaTomada;
    }

    /**
     * Devuelve el número de fichas restantes en la cadena serializada del mazo.
     *
     * @return El conteo de fichas restantes.
     */
    public int getMazoSerializadoCount() {
        if (this.mazoSerializado == null || this.mazoSerializado.isEmpty()) {
            return 0;
        }
        return this.mazoSerializado.split("\\|").length;
    }

    @Override
    public String[] getConfiguracionPartida() {
        return configuracionPartida;
    }

    /**
     * Método para inicializar o actualizar fichas
     *
     * @param id
     * @param cantidad
     */
    public void setFichasJugador(String id, int cantidad) {
        fichasPorJugador.put(id, cantidad);
    }

    /**
     * Método para sumar 1 ficha
     *
     * @param id
     */
    public void incrementarFichasJugador(String id) {
        if (fichasPorJugador.containsKey(id)) {
            fichasPorJugador.put(id, fichasPorJugador.get(id) + 1);
        }
    }

    @Override
    public int getNumeroDeJugadoresRegistrados() {
        return numeroDeJugadoresRegistrados;
    }

    @Override
    public String[] getUltimoResultadoVotacion() {
        return ultimoResultadoVotacion;
    }

    @Override
    public boolean isVotacionAprobada() {
        return votacionAprobada;
    }

    @Override
    public int getIndiceTurnoActual() {
        return indiceTurnoActual;
    }

    @Override
    public String[] getCandidatoTemporal() {
        return candidatoTemporal;
    }

    /**
     * Generar cadena para enviar por red (Ej: "Jugador1=14;Jugador2=13")
     *
     * @return
     */
    public String getFichasJugadoresSerializado() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : fichasPorJugador.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    @Override
    public void registrarCandidato(String id, String payloadRed) {
        String[] datos = new String[3];
        String[] partes = payloadRed.split("\\$");
        datos[0] = id;
        datos[1] = partes[0];
        datos[2] = partes[1];

        if (this.votacionEnCurso) {
            this.candidatoRechazado = datos;
            System.out.println("[Pizarra] OCUPADO. Rechazando solicitud de: " + id);
            notificarObservadores("SOLICITUD_RECHAZADA_OCUPADO");
            return;
        }

        this.candidatoTemporal = datos;

        if (this.indiceTurnoActual != -1) {
            System.out.println("[Pizarra] Rechazo automático: Partida iniciada.");
            notificarObservadores("SOLICITUD_RECHAZADA_INICIADA");
            this.candidatoTemporal = null;
            return;
        }

        if (getNumeroDeJugadoresRegistrados() >= 4) {
            System.out.println("[Pizarra] Rechazo automático: Sala llena.");
            notificarObservadores("SOLICITUD_RECHAZADA_LLENA");
            this.candidatoTemporal = null;
            return;
        }

        if (getNumeroDeJugadoresRegistrados() == 0) {
            System.out.println("[Pizarra] Sala vacía. Aceptando automáticamente al anfitrión: " + id);

            this.candidatoTemporal = datos;
            notificarObservadores("SOLICITUD_RECHAZADA_VACIA");

            this.candidatoTemporal = null;
            return;
        }

        System.out.println("[Pizarra] Solicitud válida. Iniciando estado de votación.");
        this.votacionEnCurso = true;
        this.votosAfirmativos = 0;
        this.votosRecibidos = 0;
        this.totalVotantesEsperados = getNumeroDeJugadoresRegistrados();

        notificarObservadores("SOLICITUD_ENTRANTE");
    }

    @Override
    public String[] getCandidatoRechazado() {
        return this.candidatoRechazado;
    }

    @Override
    public void limpiarCandidatoActual() {
        this.candidatoTemporal = null;
        this.votacionEnCurso = false;
        System.out.println("[Pizarra] Sala liberada para nuevas solicitudes.");
    }

    @Override
    public void finalizarConteo() {
        this.votacionAprobada = (votosAfirmativos == totalVotantesEsperados);

        this.ultimoResultadoVotacion = this.candidatoTemporal;

        this.candidatoTemporal = null;
        this.votacionEnCurso = false;
        this.votosAfirmativos = 0;
        this.votosRecibidos = 0;

        System.out.println("[Pizarra] Votación finalizada. Sala liberada automáticamente.");

        notificarObservadores("VOTACION_FINALIZADA");
    }

    public String getUltimoMensajeChat() {
        return ultimoMensajeChat;
    }

    private void reiniciarPizarra() {
        this.indiceTurnoActual = -1;
        this.mazoSerializado = "";
        this.ultimoTableroSerializado = "";
        this.ultimoJugadorQueMovio = "";

        // BORRAMOS JUGADORES PARA QUE EL LOBBY QUEDE VACÍO
        this.ordenDeTurnos.clear();
        this.perfilesJugadores.clear();
        this.datosJugadores.clear();
        this.numeroDeJugadoresRegistrados = 0;

        this.fichasPorJugador.clear();
        this.votosTerminar.clear();
        this.puntajesFinales.clear();

        this.recolectandoPuntajes = false;
        this.votacionTerminarEnCurso = false;
        this.partidaConfigurada = false;

        System.out.println("[Pizarra] ¡SALA REINICIADA! Lista para nuevos registros.");
    }
}
