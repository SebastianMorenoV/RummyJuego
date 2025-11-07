package pizarra;

import DTO.GrupoDTO;
import contratos.iObservador;
import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * El Pizarrón (Blackboard) "tonto".
 * Solo guarda datos (muchos como Strings) y notifica al Controlador.
 */
public class EstadoJuegoPizarra implements iPizarraJuego {

    private final Map<String, DatosJugador> estadoJugadores;
    private final List<iObservador> observadores;
    private String ultimoTableroSerializado = "";
    private final List<String> ordenDeTurnos;
    private String ultimoPayloadMovimiento;
    private List<GrupoDTO> gruposEnTablero;
    private String ultimoJugadorQueMovio;
    private int indiceTurnoActual;
    
    // Almacén "tonto" para el mazo. Es solo un string largo.
    private String mazoSerializado;

    /**
     * Clase interna para guardar el estado de CADA jugador.
     * Ya no almacena la mano.
     */
    private static class DatosJugador {
        private String id;
        public DatosJugador(String id, String manoInicialSerializada) {
            this.id = id;
            // La mano ya no se guarda aquí
        }
    }

    public EstadoJuegoPizarra() {
        this.estadoJugadores = new ConcurrentHashMap<>();
        this.ordenDeTurnos = Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1; // -1 = El juego no ha comenzado
        this.observadores = new ArrayList<>();
        this.gruposEnTablero = new ArrayList<>();
        this.ultimoPayloadMovimiento = "";
        this.mazoSerializado = "";
    }

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

    @Override
    public void registrarJugador(String id, String payloadMano) {
        // payloadMano se ignora, la mano se dará al iniciar
        DatosJugador datos = new DatosJugador(id, "");
        estadoJugadores.put(id, datos);
        ordenDeTurnos.add(id);
        System.out.println("[Pizarra] Jugador '" + id
                + "' registrado. Total: " + ordenDeTurnos.size());

        // Notifica al controlador que el "lobby" ha cambiado
        notificarObservadores("JUGADOR_UNIDO");
    }

    @Override
    public String getMano(String id) {
        // La pizarra ya no gestiona las manos; el Agente lo hizo.
        return null;
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
            indiceTurnoActual = (indiceTurnoActual + 1) % ordenDeTurnos.size();
            String idSiguiente = ordenDeTurnos.get(indiceTurnoActual);
            System.out.println("[Pizarra] Turno de: " + idSiguiente);
            // Notifica que el turno avanzó. El Controlador se encargará del resto.
            notificarObservadores("AVANZAR_TURNO");
        }
    }

    /**
     * Valida e inicia la partida. Soporta de 2 a 4 jugadores.
     */
    @Override
    public synchronized boolean iniciarPartidaSiCorresponde() {
        int numJugadores = ordenDeTurnos.size();
        
        // Solo inicia si el juego NO ha comenzado Y hay entre 2 y 4 jugadores
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

    @Override
    public String getJugador() {
        if (indiceTurnoActual == -1 || ordenDeTurnos.isEmpty()) {
            return null;
        }
        return ordenDeTurnos.get(indiceTurnoActual);
    }

    public String getUltimoTableroSerializado() {
        return this.ultimoTableroSerializado;
    }

    public String getUltimoJugadorQueMovio() {
        return this.ultimoJugadorQueMovio;
    }

    @Override
    public boolean procesarComando(String idCliente, String comando, String payload) {
        // Solo permite registrar o iniciar si el juego no ha comenzado
        if (indiceTurnoActual == -1) {
             switch (comando) {
                case "REGISTRAR":
                    registrarJugador(idCliente, payload); // payload es ""
                    return true;
                case "INICIAR_PARTIDA":
                    System.out.println("[Pizarra] Recibido comando INICIAR_PARTIDA de " + idCliente);
                    if (iniciarPartidaSiCorresponde()) {
                        notificarObservadores("EVENTO_PARTIDA_INICIADA");
                    }
                    return true;
             }
        }

        // Si el juego ya inició, solo acepta comandos de juego
        if (indiceTurnoActual != -1) {
            switch (comando) {
                case "MOVER":
                    this.ultimoJugadorQueMovio = idCliente;
                    this.ultimoTableroSerializado = payload; // Guarda el estado temporal
                    System.out.println("[Pizarra] " + idCliente + " movió (temporal).");
                    notificarObservadores("MOVIMIENTO");
                    return true;

                case "FINALIZAR_TURNO":
                    this.ultimoJugadorQueMovio = idCliente;
                    this.ultimoTableroSerializado = payload; // Guarda el estado final
                    System.out.println("[Pizarra] " + idCliente + " finalizó turno.");
                    avanzarTurno(); // Notifica "AVANZAR_TURNO"
                    return true;

                case "TOMAR_FICHA":
                    this.ultimoJugadorQueMovio = idCliente;
                    this.ultimoTableroSerializado = payload; // Guarda el estado REVERTIDO
                    System.out.println("[Pizarra] " + idCliente + " pidió tomar ficha.");
                    notificarObservadores("TOMAR_FICHA"); // Notifica al Controlador
                    return true;
            }
        }
        
        System.err.println("[Pizarra] Comando desconocido o fuera de lugar: " + comando);
        return false;
    }
    
    // --- Métodos de Ayuda para el Controlador ---
    
    /**
     * Le da al Controlador la lista de jugadores para que el Agente reparta.
     */
    public List<String> getOrdenDeTurnos() {
        return this.ordenDeTurnos;
    }
    
    /**
     * Almacena el estado "tonto" del mazo.
     */
    public void setMazoSerializado(String mazo) {
        this.mazoSerializado = mazo;
        System.out.println("[Pizarra] Mazo serializado almacenado.");
    }
    
    /**
     * Toma una ficha del mazo serializado "tonto".
     * La pizarra no sabe qué es una ficha, solo sabe separar por "|".
     */
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
     * Devuelve el número de fichas restantes en el string del mazo.
     */
    public int getMazoSerializadoCount() {
        if (this.mazoSerializado == null || this.mazoSerializado.isEmpty()) {
            return 0;
        }
        // Contar el número de separadores "|" y sumar 1
        // (Un mazo "1,2|3,4" tiene 2 fichas)
        return this.mazoSerializado.split("\\|").length;
    }

    public String getUltimoPayloadMovimiento() {
        return this.ultimoPayloadMovimiento;
    }
}