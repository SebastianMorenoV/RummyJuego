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

    private Map<String, String> datosJugadores = new HashMap<>();

    private Map<String, String> perfilesJugadores = new HashMap<>();

    private String ultimoTableroSerializado = "";
    private final List<String> ordenDeTurnos;
    private String ultimoJugadorQueMovio;
    private int indiceTurnoActual;
    private String[] jugadorARegistrarTemporal;
    private String[] configuracionPartida;
    private String mazoSerializado;
    private List<String> jugadoresListos = new ArrayList<>();

    private Map<String, Integer> fichasPorJugador = new HashMap<>(); // NUEVO

    public EstadoJuegoPizarra() {
        this.ordenDeTurnos = Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1;
        this.observadores = new ArrayList<>();
        this.mazoSerializado = "";
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
    }

    // Permite al Controlador obtener el payload completo (Avatar, Colores) de un jugador por su ID
    public String getDatosJugador(String idJugador) {
        return this.datosJugadores.get(idJugador);
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
        configuracionPartida = new String[2];

        String[] partes = payload.split("\\$");

        if (partes.length >= 2) {
            configuracionPartida[0] = partes[0];
            configuracionPartida[1] = partes[1];
            System.out.println("[Pizarra] Configuración guardada: " + partes[0] + " comodines, " + partes[1] + " fichas.");
            notificarObservadores("CONFIGURAR_PARTIDA");
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

                case "CONFIGURAR_PARTIDA":
                    System.out.println("Configurando partida en blackboard [CU Configurar Partida]:   ");
                    configurarPartida(idCliente, payload);
                    break;

                case "ACTUALIZAR_PERFIL": //
                    System.out.println("[Pizarra] Verificando perfil de " + idCliente);

                    // El payload llega como: "Nombre$Avatar$Color..."
                    String[] partesNuevas = payload.split("\\$");
                    String nuevoNombre = (partesNuevas.length > 0) ? partesNuevas[0] : "";

                    boolean nombreOcupado = false;

                    // Recorremos los perfiles que ya tenemos guardados en el mapa 'perfilesJugadores'
                    for (String datosExistentes : perfilesJugadores.values()) {
                        // Extraemos el nombre de los datos existentes
                        String[] partesExistentes = datosExistentes.split("\\$");
                        String nombreExistente = (partesExistentes.length > 0) ? partesExistentes[0] : "";

                        // Comparamos (ignorando mayúsculas/minúsculas)
                        if (nombreExistente.equalsIgnoreCase(nuevoNombre)) {
                            nombreOcupado = true;
                            break;
                        }
                    }

                    if (nombreOcupado) {
                        System.out.println("[Pizarra] Error: El nombre '" + nuevoNombre + "' ya está en uso.");
                        // El formato del evento será: "NOMBRE_REPETIDO:ID_DEL_CLIENTE"
                        notificarObservadores("NOMBRE_REPETIDO:" + idCliente);
                    } else {
                        // Si está libre, procedemos normal
                        perfilesJugadores.put(idCliente, payload);
                        System.out.println("[Pizarra] Registro exitoso para " + nuevoNombre);
                        notificarObservadores("REGISTRO_EXITOSO:" + idCliente);
                    }
                    break;

                // mock?
                case "SOLICITAR_CREACION":
                    System.out.println("[Pizarra] Procesando solicitud de creación de: " + idCliente);

                    // Si la lista está vacía O si YO soy el primero en la lista
                    boolean soyElHost = ordenDeTurnos.isEmpty() || ordenDeTurnos.get(0).equals(idCliente);

                    if (soyElHost) {
                        System.out.println("[Pizarra] Permiso CONCEDIDO a " + idCliente);
                        notificarObservadores("PERMISO_CREAR:" + idCliente);
                    } else {
                        System.out.println("[Pizarra] Permiso DENEGADO a " + idCliente);
                        notificarObservadores("PARTIDA_EXISTENTE:" + idCliente);
                    }
                    break;

                case "SOLICITAR_UNIRSE":
                    System.out.println("[Pizarra] Solicitud de unirse recibida de: " + idCliente);

                    // VALIDACIONES:
                    // 1. Debe haber alguien ya registrado (el Host)
                    // 2. No debe haber iniciado el juego (indiceTurnoActual == -1, ya validado por el if padre)
                    // 3. Máximo 4 jugadores
                    boolean hayHost = !ordenDeTurnos.isEmpty();
                    boolean hayEspacio = ordenDeTurnos.size() <= 4;

                    if (hayHost && hayEspacio) {
                        System.out.println("[Pizarra] Permiso de unirse CONCEDIDO a " + idCliente);
                        notificarObservadores("PERMISO_UNIRSE:" + idCliente);
                    } else {
                        System.out.println("[Pizarra] Permiso de unirse DENEGADO (Sala vacía o llena).");
                        notificarObservadores("ACCESO_DENEGADO:" + idCliente);
                    }
                    break;

                case "ESTOY_LISTO":
                    System.out.println("[Pizarra] Jugador listo: " + idCliente);

                    if (!jugadoresListos.contains(idCliente)) {
                        jugadoresListos.add(idCliente);
                        notificarObservadores("ACTUALIZAR_ESTADO_SALA");
                    }

                    // Verificar si todos (o mínimo 2) están listos
                    int totalEnSala = ordenDeTurnos.size();
                    int totalListos = jugadoresListos.size();

                    System.out.println("[Pizarra] Listos: " + totalListos + "/" + totalEnSala);

                    // REGLA DE INICIO: Mínimo 2 jugadores y Todos deben estar listos
                    if (totalEnSala >= 2 && totalListos == totalEnSala) {
                        System.out.println("[Pizarra] ¡Todos listos! Iniciando partida...");

                        if (iniciarPartidaSiCorresponde()) {
                            notificarObservadores("EVENTO_PARTIDA_INICIADA");
                        }
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
}
