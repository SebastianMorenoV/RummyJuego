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

    private Tablero tablero;
    private List<Jugador> jugadores;
    private int jugadorActual;
    private boolean primerMovimientoRealizado;
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;

    public JuegoRummyFachada() {
        this.tablero = new Tablero();
        this.jugadores = new ArrayList<>();
        this.jugadorActual = 0;
        this.primerMovimientoRealizado = false;
    }

    
    @Override
    public void iniciarPartida() {
        this.jugadores.add(new Jugador("Jugador 1", "B1", new Mano()));
        this.jugadores.add(new Jugador("Jugador 2", "B2", new Mano()));
        this.tablero.crearMazoCompleto();
        for (Jugador jugador : this.jugadores) {
            this.tablero.repartirMano(jugador, 14);
        }
        guardarEstadoTurno();
    }

    @Override
    public Jugador getJugadorActual() {
        return this.jugadores.get(this.jugadorActual);
    }

    @Override
    public void jugadorTomaFichaDelMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            getJugadorActual().agregarFichaAJugador(fichaTomada);
        }
    }

    @Override
    public void siguienteTurno() {
        this.jugadorActual = (this.jugadorActual + 1) % this.jugadores.size();
        guardarEstadoTurno();
    }

    @Override
    public void colocarFichasEnTablero(List<Grupo> nuevosGrupos) {
        nuevosGrupos.forEach(Grupo::validarYEstablecerTipo);
        this.tablero.setFichasEnTablero(nuevosGrupos);
    }

    @Override
    public boolean validarYFinalizarTurno() {

        Jugador jugador = getJugadorActual();
        boolean esPrimerMovimiento = !jugador.isHaHechoPrimerMovimiento();

        if (tablero.esJugadaValida(esPrimerMovimiento)) { // Pasa la bandera correcta
            confirmarCambiosTurno();
            return true;
        } else {
            revertirCambiosTurno();
            return false;
        }
    }

    @Override
    public void revertirCambiosDelTurno() {
        revertirCambiosTurno();
    }

    // Métodos Internos
    private void confirmarCambiosTurno() {
        if (!primerMovimientoRealizado) {
            this.primerMovimientoRealizado = true;
        }

        // Marcar todos los grupos en el tablero como validados y permanentes
        for (Grupo g : this.tablero.getFichasEnTablero()) {
            g.setValidado(); // Esto pone esTemporal = false
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
    }

    @Override
    public void guardarEstadoTurno() {
        this.tableroAlInicioDelTurno = this.tablero.copiaProfunda();
        this.manoAlInicioDelTurno = this.getJugadorActual().getManoJugador().copiaProfunda();
    }

    @Override
    public boolean haGanadoElJugador() {
        return this.getJugadorActual().haGanado();
    }

    /**
     * Metodo en el que se regresan fichas a la mano. AHORA FUNCIONARÁ porque
     * `!g.esTemporal()` encontrará los grupos validados.
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
        Ficha fichaParaRegresar = this.tablero.removerFicha(idFicha);

        if (fichaParaRegresar != null) {
            return true;
        }

        return false; // FALSE = No se encontró la ficha para remover
    }

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
