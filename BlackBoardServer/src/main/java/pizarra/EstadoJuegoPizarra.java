package pizarra;

import contratos.iObservador;
import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * El Pizarrón (Blackboard) "tonto". Solo guarda datos (muchos como Strings) y
 * notifica al Controlador cuando hay un cambio de estado relevante. Es el
 * centro de datos y coordinación de eventos del lado del servidor.
 *
 * @author chris
 */
public class EstadoJuegoPizarra implements iPizarraJuego {

    private final List<iObservador> observadores;
    private String ultimoTableroSerializado = "";
    private final List<String> ordenDeTurnos;
    private String ultimoJugadorQueMovio;
    private int indiceTurnoActual;
    private String[] jugadorARegistrarTemporal;
    private String mazoSerializado;
    
    
    private Map<String, Integer> fichasPorJugador = new HashMap<>();
    private int numeroDeJugadoresRegistrados;
    private String[] candidatoTemporal;
    private String[] candidatoRechazado;  
    private String[] ultimoResultadoVotacion;
    private boolean votacionEnCurso = false;
    private int votosAfirmativos = 0;
    private int votosRecibidos = 0;
    private int totalVotantesEsperados = 0;
    private boolean votacionAprobada = false;

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
        jugadorARegistrarTemporal = new String[3];
        String[] partes = payloadMano.split("\\$", 2);
        jugadorARegistrarTemporal[0] = id;
        jugadorARegistrarTemporal[1] = partes[0];
        jugadorARegistrarTemporal[2] = partes[1];

        ordenDeTurnos.add(id);
        notificarObservadores("JUGADOR_UNIDO");
        numeroDeJugadoresRegistrados++;
        jugadorARegistrarTemporal = null;
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

        if (indiceTurnoActual == -1 && numJugadores >= 2 && numJugadores <= 4) {
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
     * @return
     */
    @Override
    public void procesarComando(String idCliente, String comando, String payload) {
        switch (comando) {

            case "SOLICITAR_UNIRSE":
                registrarCandidato(idCliente, payload);
                break;

            case "RESPUESTA_VOTO":
                if (!votacionEnCurso) return;

                boolean voto = Boolean.parseBoolean(payload);
                votosRecibidos++;
                if (voto) votosAfirmativos++;

                System.out.println("[Pizarra] Voto recibido de " + idCliente + ": " + voto + 
                                   " (" + votosRecibidos + "/" + totalVotantesEsperados + ")");

                if (votosRecibidos >= totalVotantesEsperados) {
                    finalizarConteo();
                }
                break;
        }
        if (indiceTurnoActual == -1) {
            switch (comando) {
                case "REGISTRAR":
                    registrarJugador(idCliente, payload);
                    break; // Importante: break para no saltar al siguiente caso
                case "INICIAR_PARTIDA":
                    System.out.println("[Pizarra] Recibido comando INICIAR_PARTIDA de " + idCliente);
                    if (iniciarPartidaSiCorresponde()) {
                        notificarObservadores("EVENTO_PARTIDA_INICIADA");
                    }
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
                    // [NUEVA LÓGICA] Separar el tablero del contador de fichas usando "#"
                    // El payload viene como: "GRUPO1;...$GRUPO2...#5"
                    String[] partesFinalizar = payload.split("#");

                    // La parte 0 es el tablero serializado (lo de siempre)
                    this.ultimoTableroSerializado = partesFinalizar[0];

                    // La parte 1 (si existe) es el número de fichas que le quedaron al jugador
                    if (partesFinalizar.length > 1) {
                        try {
                            int fichasRestantes = Integer.parseInt(partesFinalizar[1]);

                            // Guardamos este dato en la Pizarra
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
            
            registrarJugador(id, payloadRed);

            this.candidatoTemporal = datos; 
            notificarObservadores("CANDIDATO_ACEPTADO_DIRECTAMENTE");
            
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
    
    /**
     * Devuelve el número de fichas restantes en la cadena serializada del mazo.
     *
     * @return El conteo de fichas restantes.
     */
    @Override
    public int getMazoSerializadoCount() {
        if (this.mazoSerializado == null || this.mazoSerializado.isEmpty()) {
            return 0;
        }
        return this.mazoSerializado.split("\\|").length;
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



    @Override
    public int getNumeroDeJugadoresRegistrados() {
        return numeroDeJugadoresRegistrados;
    }

    @Override
    public int getIndiceTurnoActual() {
        return indiceTurnoActual;
    }

    @Override
    public String[] getCandidatoTemporal() {
        return this.candidatoTemporal;
    }

    @Override
    public boolean isVotacionAprobada() {
        return this.votacionAprobada;
    }
    @Override
    public String[] getUltimoResultadoVotacion() {
        return this.ultimoResultadoVotacion;
    }
    // NUEVO: Método para inicializar o actualizar fichas
    public void setFichasJugador(String id, int cantidad) {
        fichasPorJugador.put(id, cantidad);
    }

    // NUEVO: Método para sumar 1 ficha (cuando alguien come)
    @Override
    public void incrementarFichasJugador(String id) {
        if (fichasPorJugador.containsKey(id)) {
            fichasPorJugador.put(id, fichasPorJugador.get(id) + 1);
        }
    }

    // NUEVO: Generar cadena para enviar por red (Ej: "Jugador1=14;Jugador2=13")
    @Override
    public String getFichasJugadoresSerializado() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : fichasPorJugador.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }
}
