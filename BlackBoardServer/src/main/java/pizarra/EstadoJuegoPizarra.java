package pizarra;
// O donde vivirá tu blackboard

import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * El Pizarrón 📌 Implementación concreta que almacena el estado de la partida.
 * Es "tonto" y solo guarda datos. No sabe de red.
 */
public class EstadoJuegoPizarra implements iPizarraJuego {

    // --- El Estado Interno del Pizarrón ---
    /**
     * Almacena el estado de juego de cada jugador (ID -> Datos).
     */
    private final Map<String, DatosJugador> estadoJugadores;

    /**
     * Mantiene el orden de los turnos.
     */
    private final List<String> ordenDeTurnos;

    /**
     * Índice del jugador que tiene el turno actual.
     */
    private int indiceTurnoActual;

    private final int JUGADORES_PARA_INICIAR = 2; // O los que necesites

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

    // --- Constructor ---
    public EstadoJuegoPizarra() {
        this.estadoJugadores = new ConcurrentHashMap<>();
        this.ordenDeTurnos = java.util.Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1; // -1 significa "juego no iniciado"
    }

    // --- Implementación de la Interfaz iPizarraJuego ---
    @Override
    public void registrarJugador(String id, String payloadMano) {
        // payloadMano es la mano serializada (o "" si es nueva)
        DatosJugador datos = new DatosJugador(id, payloadMano);
        estadoJugadores.put(id, datos);
        ordenDeTurnos.add(id);
        System.out.println("[Pizarra] Jugador '" + id + "' registrado en la partida. Total: " + ordenDeTurnos.size());
    }

    @Override
    public void actualizarMano(String id, String payloadMano) {
        DatosJugador datos = estadoJugadores.get(id);
        if (datos != null) {
            datos.setManoSerializada(payloadMano);
            System.out.println("[Pizarra] Mano de '" + id + "' actualizada.");
        }
    }

    @Override
    public String getMano(String id) {
        DatosJugador datos = estadoJugadores.get(id);
        return (datos != null) ? datos.getManoSerializada() : null;
    }

    @Override
    public synchronized boolean esTurnoDe(String id) {
        if (indiceTurnoActual == -1) {
            return false; // El juego no ha comenzado
        }
        return ordenDeTurnos.get(indiceTurnoActual).equals(id);
    }

    @Override
    public synchronized void avanzarTurno() {
        if (indiceTurnoActual != -1) {
            // Avanza al siguiente jugador, o vuelve al primero (circular)
            indiceTurnoActual = (indiceTurnoActual + 1) % ordenDeTurnos.size();
            String idSiguiente = ordenDeTurnos.get(indiceTurnoActual);
            System.out.println("[Pizarra] Turno de: " + idSiguiente);
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
}
