package Fachada;

import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;
import Entidades.Mano;
import Entidades.Tablero;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;

/**
 * Fachada que encapsula toda la lógica y el estado del juego de Rummy. No
 * conoce la Vista ni los Observadores. Su única responsabilidad es gestionar el
 * juego.
 *
 * @author Benja
 */
public class JuegoRummyFachada implements IJuegoRummy {

    // Atributos de Estado del Juego 
    private Tablero tablero;
    private List<Jugador> jugadores;
    private int jugadorActual;
    private boolean primerMovimientoRealizado;

    // Atributos para Revertir Turno
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;

    public JuegoRummyFachada() {
        this.tablero = new Tablero();
        this.jugadores = new ArrayList<>();
        this.jugadorActual = 0;
        this.primerMovimientoRealizado = false;
    }

    /**
     * Prepara el juego, crea el mazo, reparte las fichas e inicia el primer
     * turno.
     */
    @Override
    public void iniciarPartida() {
        //Se crean dos jugadores, uno en cada vista del juego
        this.jugadores.add(new Jugador("Jugador 1", "B1", new Mano()));
        this.jugadores.add(new Jugador("Jugador 2", "B2", new Mano()));

        this.tablero.crearMazoCompleto();

        for (Jugador jugador : this.jugadores) {
            this.tablero.repartirMano(jugador, 14);
        }

        guardarEstadoTurno();
    }

    /**
     * Se obtiene al jugador que actualmente tenga su turno.
     *
     * @return
     */
    @Override
    public Jugador getJugadorActual() {
        return this.jugadores.get(this.jugadorActual);
    }

    /**
     * El jugador actual toma una ficha del mazo y la agrega a su mano.
     */
    @Override
    public void jugadorTomaFichaDelMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            getJugadorActual().agregarFichaAJugador(fichaTomada);
        }
    }

    /**
     * Se pasa al siguiente turno, dandole el turno al siguiente jugador.
     */
    @Override
    public void siguienteTurno() {
        this.jugadorActual = (this.jugadorActual + 1) % this.jugadores.size();
        guardarEstadoTurno();
    }

    /**
     * Actualiza el tablero con los grupos de fichas que el jugador intenta
     * colocar. Esta es una jugada temporal hasta que se termina el turno.
     *
     * @param nuevosGrupos son los grupos que desea agregar el jugador , por
     * defecto grupos de 1 ficha.
     */
    @Override
    public void colocarFichasEnTablero(List<Grupo> nuevosGrupos) {
        // La lógica de validación de los grupos se mantiene en la entidad Grupo
        nuevosGrupos.forEach(Grupo::validarYEstablecerTipo);
        this.tablero.setFichasEnTablero(nuevosGrupos);
    }

    /**
     * Valida la jugada actual en el tablero. Si es válida, confirma los
     * cambios. Si no, revierte el tablero y la mano del jugador al estado de
     * inicio de turno. Si el jugador decide por si mismo, finalizar el turno,
     * se da por terminado y pasa el turno al siguiente jugador.
     *
     * @return true si la jugada fue valida y/o se confirmo, false si fue
     * invalida y se revirtio.
     */
    @Override
    public boolean validarYFinalizarTurno() {
        boolean esPrimerMovimiento = !this.primerMovimientoRealizado;
        if (tablero.esJugadaValida(esPrimerMovimiento)) {
            confirmarCambiosTurno();
            return true;
        } else {
            revertirCambiosTurno();
            return false;
        }
    }

    /**
     * Funcionalidad de revertir los cambios del turno actual.
     */
    @Override
    public void revertirCambiosDelTurno() {
        revertirCambiosTurno();
    }

    // --- Métodos Internos ---
    private void confirmarCambiosTurno() {
        if (!primerMovimientoRealizado) {
            this.primerMovimientoRealizado = true;
        }

        // Orquesta la actualizacion de la mano del jugador
        List<Integer> idsEnTablero = this.tablero.getTodosLosIdsDeFichas();
        this.getJugadorActual().getManoJugador().removerFichasJugadas(idsEnTablero);

        // Prepara el siguiente turno
        guardarEstadoTurno();
    }

    private void revertirCambiosTurno() {
        this.tablero = this.tableroAlInicioDelTurno;
        this.getJugadorActual().setManoJugador(this.manoAlInicioDelTurno);
        // El estado guardado se mantiene, no se vuelve a llamar a guardarEstadoTurno()
    }

    private void guardarEstadoTurno() {
        this.tableroAlInicioDelTurno = this.tablero.copiaProfunda();
        this.manoAlInicioDelTurno = this.getJugadorActual().getManoJugador().copiaProfunda();
    }

    @Override
    public boolean haGanadoElJugador() {
        return this.getJugadorActual().haGanado();
    }

    /**
     * Metodo en el que se regresan fichas a la mano. filtra por grupos no
     * temporales para que no sean regresables cuando su grupo es valido y si
     * esa validacion ya pasa y la ficha esta en un grupo invalido se intenta
     * regresar a la mano y y se elimina la ficha del tablero, si la ficha se
     * regreso a la mano regresa true, si no un false indicando que no fue
     * regresada exitosamente
     *
     * @param idFicha
     * @return
     */
    @Override
    public boolean intentarRegresarFichaAMano(int idFicha) {
        // Filtramos para quedarnos solo con los grupos que NO son temporales.
        for (Grupo grupoValidado : this.tablero.getFichasEnTablero().stream().filter(g
                -> !g.esTemporal()).toList()) {

            for (Ficha ficha : grupoValidado.getFichas()) {
                if (ficha.getId() == idFicha) {
                    
                    // Encontrada en un grupo antiguo. No se puede mover.
                    return false;
                }
            }
        }

        // Si el bucle termina, la ficha no está en un grupo permanente y se puede mover.
        // El resto de tu lógica para remover la ficha y agregarla a la mano
        Ficha fichaParaRegresar = this.tablero.removerFicha(idFicha);

        if (fichaParaRegresar != null) {
            return true;
        }

        return false; // FALSE No se encontró la ficha para remover
    }

    // Getters para que el Modelo consulte el estado y cree los DTOs
    @Override
    public List<Ficha> getManoDeJugador(int indiceJugador) {
        if (indiceJugador >= 0 && indiceJugador < jugadores.size()) {
            return jugadores.get(indiceJugador).getManoJugador().getFichasEnMano();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Grupo> getGruposEnTablero() {
        return this.tablero.getFichasEnTablero();
    }

    @Override
    public int getCantidadFichasMazo() {
        return this.tablero.getMazo().size();
    }

}
