package pizarra;

import DTO.GrupoDTO;
import contratos.iObservador;
import contratos.iPizarraJuego;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * El Pizarrón (Blackboard) "tonto". Solo guarda datos (muchos como Strings) y
 * notifica al Controlador.
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

    public EstadoJuegoPizarra() {
        this.ordenDeTurnos = Collections.synchronizedList(new ArrayList<>());
        this.indiceTurnoActual = -1; 
        this.observadores = new ArrayList<>();
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
        jugadorARegistrarTemporal = new String[3];
        String[] partes = payloadMano.split("\\$", 2);
        jugadorARegistrarTemporal[0] = id;
        jugadorARegistrarTemporal[1] = partes[0]; 
        jugadorARegistrarTemporal[2] = partes[1];


        ordenDeTurnos.add(id);

        notificarObservadores("JUGADOR_UNIDO");
        jugadorARegistrarTemporal = null; 
    }

    /**
     * Verifica si actualmente es turno del jugador indicado.
     *
     * @param id
     * @return
     */
    @Override
    public synchronized boolean esTurnoDe(String id) {
        if (indiceTurnoActual == -1) {
            return false; 
        }
        return ordenDeTurnos.get(indiceTurnoActual).equals(id);
    }

    /**
     * Avanza el turno al siguiente jugador en orden, y notifica al controlador
     * que el turno avanzó.
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
     * Valida e inicia la partida. Soporta de 2 a 4 jugadores.
     *
     * @return
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

    @Override
    public String[] getIpCliente() {
        return jugadorARegistrarTemporal;
    }

    @Override
    public String getJugador() {
        if (indiceTurnoActual == -1 || ordenDeTurnos.isEmpty()) {
            return null;
        }
        return ordenDeTurnos.get(indiceTurnoActual);
    }

    @Override
    public String getUltimoTableroSerializado() {
        return this.ultimoTableroSerializado;
    }

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
                    this.ultimoTableroSerializado = payload; 
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
     * Le da al Controlador la lista de jugadores para que el Agente reparta.
     */
    @Override
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
     * Toma una ficha del mazo serializado "tonto". La pizarra no sabe qué es
     * una ficha, solo sabe separar por "|".
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
     * Devuelve el número de fichas restantes en el string del mazo.
     */
    public int getMazoSerializadoCount() {
        if (this.mazoSerializado == null || this.mazoSerializado.isEmpty()) {
            return 0;
        }
        return this.mazoSerializado.split("\\|").length;
    }


}
