package pizarra;

import DTO.GrupoDTO;
import contratos.iObservador;
import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * El Pizarrón (Blackboard) Implementación concreta que almacena el estado de la
 * partida. Es "tonto" y solo guarda datos. No sabe de red.
 *
 * @author benja
 */
public class EstadoJuegoPizarra implements iPizarraJuego {

    /**
     * El Estado Interno del Pizarrón Almacena el estado de juego de cada
     * jugador (ID -> Datos).
     */
    private final Map<String, DatosJugador> estadoJugadores;
    private final List<iObservador> observadores;
    private String ultimoTableroSerializado = "";
    /**
     * Mantiene el orden de los turnos.
     */
    private final List<String> ordenDeTurnos;
    private String ultimoPayloadMovimiento;
    private List<GrupoDTO> gruposEnTablero;
    private String ultimoJugadorQueMovio;

    /**
     * Índice del jugador que tiene el turno actual.
     */
    private int indiceTurnoActual;
    private final int JUGADORES_PARA_INICIAR = 2; // Jugadores activos por ahora.

    /**
     * Clase interna para guardar el estado de CADA jugador. No sabe de IPs ni
     * puertos, solo del juego.
     */
    private static class DatosJugador {

        private String id;
        private String manoSerializada; // La mano se guarda como un string

        public DatosJugador(String id, String manoInicialSerializada) {
            this.id = id;
            this.manoSerializada = manoInicialSerializada;
        }

        public String getManoSerializada() {
            return manoSerializada;
        }

        public void setManoSerializada(String mano) {
            this.manoSerializada = mano;
        }
    }

    public EstadoJuegoPizarra() {
        this.estadoJugadores = new ConcurrentHashMap<>();
        this.ordenDeTurnos = java.util.Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1;
        this.observadores = new ArrayList<>();
        this.gruposEnTablero = new ArrayList<>();
        this.ultimoPayloadMovimiento = "";
    }

    /**
     * Añade los métodos del Observable
     *
     * @param obs
     */
    public void addObservador(iObservador obs) {
        if (obs != null && !this.observadores.contains(obs)) {
            this.observadores.add(obs);
        }
    }

    private void notificarObservadores(String evento) {
        for (iObservador obs : this.observadores) {
            obs.actualiza(this, evento);
        }
    }

    /**
     * Implementación de la Interfaz iPizarraJuego
     *
     * @param id
     * @param payloadMano
     */
    @Override
    public void registrarJugador(String id, String payloadMano) {
        // payloadMano es la mano serializada (o "" si es nueva)
        DatosJugador datos = new DatosJugador(id, payloadMano);
        estadoJugadores.put(id, datos);
        ordenDeTurnos.add(id);
        System.out.println("[Pizarra] Jugador '" + id + "' registrado en la partida. Total: " + ordenDeTurnos.size());

        notificarObservadores("REGISTRAR");

    }

    @Override
    public String getMano(String id) {
        DatosJugador datos = estadoJugadores.get(id);
        return (datos != null) ? datos.getManoSerializada() : null;
    }

    @Override
    public synchronized boolean esTurnoDe(String id) {
        if (indiceTurnoActual == -1) {
            return false; // False = El juego no ha comenzado
        }
        return ordenDeTurnos.get(indiceTurnoActual).equals(id);
    }

    @Override
    public synchronized void avanzarTurno() {
        if (indiceTurnoActual != -1) {
            indiceTurnoActual = (indiceTurnoActual + 1) % ordenDeTurnos.size();
            String idSiguiente = ordenDeTurnos.get(indiceTurnoActual);
            System.out.println("[Pizarra] Turno de: " + idSiguiente);

            // ¡Notifica al Controlador!
            notificarObservadores("AVANZAR_TURNO");
        }
    }

    @Override
    public synchronized boolean iniciarPartidaSiCorresponde() {
        // Solo inicia si el juego no ha iniciado Y ya tenemos los jugadores necesarios
        if (indiceTurnoActual == -1 && ordenDeTurnos.size() == JUGADORES_PARA_INICIAR) {
            indiceTurnoActual = 0; // Inicia el turno del primer jugador
            String idPrimerJugador = ordenDeTurnos.get(0);
            System.out.println("[Pizarra] ¡Partida iniciada! Hay " + JUGADORES_PARA_INICIAR + " jugadores.");
            System.out.println("[Pizarra] Turno de: " + idPrimerJugador);
            return true;
        }
        return false;
    }

    @Override
    public String getJugador() {
        if (indiceTurnoActual == -1 || ordenDeTurnos.isEmpty()) {
            return null; // O un ID por defecto
        }
        return ordenDeTurnos.get(indiceTurnoActual);
    }

    /**
     * (NUEVO) Método para que el Controlador obtenga el último tablero válido
     */
    public String getUltimoTableroSerializado() {
        return this.ultimoTableroSerializado;
    }
    
    public String getUltimoJugadorQueMovio() {
        return this.ultimoJugadorQueMovio;
    }

    /**
     * REFACTORIZADO: Ahora maneja los nuevos comandos.
     */
    @Override
    public boolean procesarComando(String idCliente, String comando, String payload) {

        switch (comando) {
            case "REGISTRAR":
                registrarJugador(idCliente, payload); // payload es ""
                return true;

            case "MOVER":
                // Solo guarda el payload del movimiento, no hace nada más.
                // El Controlador lo usará para el broadcast.
                this.ultimoJugadorQueMovio = idCliente;
                this.ultimoTableroSerializado = payload;
                System.out.println("[Pizarra] " + idCliente + " movió (temporal).");
                notificarObservadores("MOVIMIENTO"); // Notifica al Controlador
                return true;

            // --- ¡NUEVOS COMANDOS! ---
            case "FINALIZAR_TURNO":
                // El cliente validó, así que guardamos este como el último estado bueno
                this.ultimoTableroSerializado = payload;
                System.out.println("[Pizarra] " + idCliente + " finalizó turno.");
                avanzarTurno(); // ¡Avanza el marcador y notifica!
                return true;

            case "TOMAR_FICHA":
                System.out.println("[Pizarra] " + idCliente + " tomó ficha y finalizó turno.");
                // Aquí podrías guardar algo si fuera necesario (ej. "TOMO_FICHA")
                // Pero por ahora, solo avanzamos el turno.
                avanzarTurno(); // ¡Avanza el marcador y notifica!
                return true;

            default:
                System.err.println("[Pizarra] Comando desconocido: " + comando);
                return false;
        }
    }

    public String getUltimoPayloadMovimiento() {
        return this.ultimoPayloadMovimiento;
    }
}
