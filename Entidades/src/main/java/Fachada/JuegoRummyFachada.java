/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Fachada;

import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;
import Entidades.Mano;
import Entidades.Tablero;

import java.util.List;

/**
 * Fachada que encapsula toda la lógica y el estado del juego de Rummy. No
 * conoce la Vista ni los Observadores. Su única responsabilidad es gestionar el
 * juego.
 */
public class JuegoRummyFachada implements IJuegoRummy {

    // --- Atributos de Estado del Juego  ---
    private Tablero tablero;
    private Jugador jugador;
    private boolean primerMovimientoRealizado;

    // --- Atributos para Revertir Turno ---
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;

    public JuegoRummyFachada() {
        this.tablero = new Tablero();
        this.jugador = new Jugador();
        this.primerMovimientoRealizado = false;
    }

    /**
     * Prepara el juego, crea el mazo, reparte las fichas e inicia el primer
     * turno.
     */
    @Override
    public void iniciarPartida() {
        this.jugador = new Jugador("Sebas", "B1", new Mano());
        this.tablero.crearMazoCompleto();
        this.tablero.repartirMano(jugador, 14);
        guardarEstadoTurno();
    }

    /**
     * El jugador toma una ficha del mazo y la agrega a su mano.
     */
    @Override
    public void jugadorTomaFichaDelMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            jugador.agregarFichaAJugador(fichaTomada);
        }
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
        for (Grupo nuevosGrupo : nuevosGrupos) {
            System.out.println("Grupo: " + nuevosGrupo.getTipo() + " fichas: " + nuevosGrupo.getFichas());
        }
        this.tablero.setFichasEnTablero(nuevosGrupos);
    }

    /**
     * Valida la jugada actual en el tablero. Si es válida, confirma los
     * cambios. Si no, revierte el tablero y la mano del jugador al estado de
     * inicio de turno.
     *
     * @return true si la jugada fue válida y se confirmó, false si fue inválida
     * y se revirtió.
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

        // Orquesta la actualización de la mano del jugador
        List<Integer> idsEnTablero = this.tablero.getTodosLosIdsDeFichas();
        this.jugador.getManoJugador().removerFichasJugadas(idsEnTablero);

        // Prepara el siguiente turno
        guardarEstadoTurno();
    }

    private void revertirCambiosTurno() {
        this.tablero = this.tableroAlInicioDelTurno;
        this.jugador.setManoJugador(this.manoAlInicioDelTurno);
        // El estado guardado se mantiene, no se vuelve a llamar a guardarEstadoTurno()
    }

    private void guardarEstadoTurno() {
        this.tableroAlInicioDelTurno = this.tablero.copiaProfunda();
        this.manoAlInicioDelTurno = this.jugador.getManoJugador().copiaProfunda();
    }

    // --- Getters para que el Modelo consulte el estado y cree los DTOs ---
    @Override
    public List<Ficha> getManoJugador() {
        return this.jugador.getManoJugador().getFichasEnMano();
    }

    @Override
    public List<Grupo> getGruposEnTablero() {
        return this.tablero.getFichasEnTablero();
    }

    @Override
    public int getCantidadFichasMazo() {
        return this.tablero.getMazo().size();
    }

    @Override
    public boolean haGanadoElJugador() {
        return this.jugador.haGanado();
    }
    
    @Override
    public boolean intentarRegresarFichaAMano(int idFicha) {
        // Filtramos para quedarnos solo con los grupos que NO son temporales.
        for (Grupo grupoValidado : this.tablero.getFichasEnTablero().stream().filter(g -> !g.esTemporal()).toList()) {

            for (Ficha ficha : grupoValidado.getFichas()) {
                if (ficha.getId() == idFicha) {
                    // Encontrada en un grupo antiguo. No se puede mover.
                    return false;
                }
            }
        }

        // Si el bucle termina, la ficha no está en un grupo permanente y se puede mover.
        // ... el resto de tu lógica para remover la ficha y agregarla a la mano ...
        Ficha fichaParaRegresar = this.tablero.removerFicha(idFicha);

        if (fichaParaRegresar != null) {
            //this.jugador.agregarFichaAJugador(fichaParaRegresar);
            return true;
        }

        return false; // No se encontró la ficha para remover
    }
}
