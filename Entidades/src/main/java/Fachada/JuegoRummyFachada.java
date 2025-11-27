package Fachada;

import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;
import Entidades.Mano;
import Entidades.Tablero;
import java.util.List;

/**
 * Fachada que encapsula toda la lógica y el estado del juego de Rummy.
 *
 * @author benja
 */
public class JuegoRummyFachada implements IJuegoRummy {

    private Tablero tablero;
    private Jugador jugador;
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;

    /**
     * Constructor por defecto. Inicializa el tablero y el jugador.
     */
    public JuegoRummyFachada() {
        this.tablero = new Tablero();
        this.jugador = new Jugador();
    }

    /**
     * Inicia una nueva partida o turno de juego, guardando el estado inicial
     * del tablero y la mano del jugador para posibles reversiones.
     * Es el primer paso en cada nuevo turno.
     */
    @Override
    public void iniciarPartida() {
        guardarEstadoTurno();
    }

    /**
     * Obtiene la instancia del Jugador que está participando actualmente.
     * * @return El objeto Jugador actual.
     */
    @Override
    public Jugador getJugadorActual() {
        return this.jugador;
    }

    /**
     * Simula la acción del jugador de tomar una ficha del mazo.
     * La ficha es removida del mazo del Tablero y añadida a la mano del Jugador.
     */
    @Override
    public void jugadorTomaFichaDelMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            getJugadorActual().agregarFichaAJugador(fichaTomada);
        }
    }

    /**
     * Coloca uno o más grupos de fichas (tercias o corridas) propuestos por el 
     * jugador en el tablero.
     * Este método valida la estructura interna de los grupos antes de establecerlos.
     * * @param nuevosGrupos La lista de grupos de fichas que el jugador intenta colocar.
     */
    @Override
    public void colocarFichasEnTablero(List<Grupo> nuevosGrupos) {
        nuevosGrupos.forEach(Grupo::validarYEstablecerTipo);
        this.tablero.setFichasEnTablero(nuevosGrupos);
    }

    /**
     * Valida si los movimientos realizados por el jugador en el turno actual 
     * cumplen con las reglas del Rummy (estructura de grupos válida, primer
     * movimiento de 30+ puntos, o haber jugado al menos una ficha).
     * Si las reglas se cumplen, los cambios se confirman; si no, se revierten.
     * * @return true si el turno es válido y se finaliza, false si se revierte 
     * el estado del juego.
     */
    @Override
    public boolean validarYFinalizarTurno() {

        Jugador jugador = getJugadorActual();
        boolean esPrimerMovimiento
                = !jugador.isHaHechoPrimerMovimiento();

        if (!tablero.esEstructuraDeGruposValida()) {
            revertirCambiosTurno();
            return false;
        }

        if (esPrimerMovimiento) {

            List<Grupo> gruposNuevos = this.tablero.getGruposTemporales();

            if (gruposNuevos.isEmpty()) {
                revertirCambiosTurno();
                return false;
            }

            List<Integer> idsFichasAntiguas
                    = this.tableroAlInicioDelTurno.getTodosLosIdsDeFichas();
            int puntosNuevos = 0;

            for (Grupo grupoNuevo : gruposNuevos) {

                boolean usaFichaAntigua = grupoNuevo.getFichas().stream()
                        .anyMatch(ficha
                                -> idsFichasAntiguas.contains(ficha.getId()));

                if (usaFichaAntigua) {
                    revertirCambiosTurno();
                    return false;
                }

                puntosNuevos += grupoNuevo.calcularPuntos();
            }

            if (puntosNuevos < 30) {
                revertirCambiosTurno();
                return false;
            }

            jugador.setHaHechoPrimerMovimiento(true);

        } else {

            List<Integer> idsFichasAntiguas
                    = this.tableroAlInicioDelTurno.getTodosLosIdsDeFichas();
            List<Integer> idsFichasNuevas
                    = this.tablero.getTodosLosIdsDeFichas();

            boolean haJugadoFichaNueva = idsFichasNuevas.stream()
                    .anyMatch(idNuevo -> !idsFichasAntiguas.contains(idNuevo));

            if (!haJugadoFichaNueva) {
                revertirCambiosTurno();
                return false;
            }
        }

        confirmarCambiosTurno();
        return true;
    }

    /**
     * Revierte el estado del tablero y la mano del jugador a cómo estaban
     * al inicio del turno actual.
     */
    @Override
    public void revertirCambiosDelTurno() {
        revertirCambiosTurno();
    }

    /**
     * Método privado que finaliza los cambios del turno, validando los grupos 
     * colocados y removiendo las fichas jugadas de la mano del jugador.
     * También llama a guardarEstadoTurno() para preparar el estado 
     * para el siguiente jugador.
     */
    private void confirmarCambiosTurno() {
        for (Grupo g : this.tablero.getFichasEnTablero()) {
            g.setValidado(); 
        }

        List<Integer> idsEnTablero
                = this.tablero.getTodosLosIdsDeFichas();
        this.getJugadorActual().getManoJugador().removerFichasJugadas(idsEnTablero);

        guardarEstadoTurno();
    }

    /**
     * Método privado para restaurar el estado del tablero y la mano 
     * a partir de las copias guardadas al inicio del turno.
     */
    private void revertirCambiosTurno() {
        this.tablero = this.tableroAlInicioDelTurno.copiaProfunda();
        this.getJugadorActual().setManoJugador(this.manoAlInicioDelTurno.copiaProfunda());
    }

    /**
     * Guarda el estado actual del Tablero} y la Mano del jugador 
     * en variables de respaldo para permitir una posible reversión.
     */
    @Override
    public void guardarEstadoTurno() {
        this.tableroAlInicioDelTurno = this.tablero.copiaProfunda();
        this.manoAlInicioDelTurno = this.getJugadorActual().getManoJugador().copiaProfunda();
    }

    /**
     * Verifica si el jugador actual ha ganado la partida (normalmente, si 
     * su mano está vacía).
     * * @return true si el jugador ganó, false en caso contrario.
     */
    @Override
    public boolean haGanadoElJugador() {
        return this.getJugadorActual().haGanado();
    }

    /**
     * Intenta regresar una ficha específica del tablero a la mano del jugador.
     * Solo se pueden regresar fichas que no formen parte de grupos ya validados.
     * * @param idFicha El ID de la ficha a intentar regresar.
     * @return true si la ficha fue regresada a la mano, false si pertenece 
     * a un grupo validado.
     */
    @Override
    public boolean intentarRegresarFichaAMano(int idFicha) {
        for (Grupo grupoValidado : this.tablero.getFichasEnTablero().stream().filter(g
                -> !g.esTemporal()).toList()) {

            for (Ficha ficha : grupoValidado.getFichas()) {
                if (ficha.getId() == idFicha) {
                    return false;
                }
            }
        }

        Ficha fichaParaRegresar = this.tablero.removerFicha(idFicha);
        return fichaParaRegresar != null;
    }

    /**
     * Obtiene la lista de fichas que el jugador actual tiene en su mano.
     * * @param indiceJugador Parámetro de compatibilidad, se usa el jugador actual.
     * @return Lista de Fichas en la mano del jugador.
     */
    @Override
    public List<Ficha> getManoDeJugador(int indiceJugador) {
        return this.jugador.getManoJugador().getFichasEnMano();
    }

    /**
     * Obtiene la lista completa de grupos de fichas que están actualmente 
     * en el tablero.
     * * @return Lista de Grupos en el tablero.
     */
    @Override
    public List<Grupo> getGruposEnTablero() {
        return this.tablero.getFichasEnTablero();
    }

    /**
     * Obtiene la cantidad de fichas restantes en el mazo principal.
     * * @return El número de fichas en el mazo.
     */
    @Override
    public int getCantidadFichasMazo() {
        return this.tablero.getMazo().size();
    }

    /**
     * Establece la mano inicial del jugador. Esto se usa típicamente al inicio 
     * de la partida después de repartir las fichas.
     * * @param mano La lista de fichas que compondrán la mano inicial del jugador.
     */
    @Override
    public void setManoInicial(List<Ficha> mano) {
        this.getJugadorActual().getManoJugador().setFichasEnMano(mano);
        guardarEstadoTurno();
    }

    /**
     * Obtiene la lista completa de fichas que están en el mazo.
     * * @return La lista de Fichas en el mazo.
     */
    @Override
    public List<Ficha> getMazo() {
        return this.tablero.getMazo();
    }
}
