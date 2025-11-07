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

    public JuegoRummyFachada() {
        this.tablero = new Tablero();
        this.jugador = new Jugador();
    }

    @Override
    public void iniciarPartida() {
        guardarEstadoTurno();
    }

    @Override
    public Jugador getJugadorActual() {
        return this.jugador;
    }

    @Override
    public void jugadorTomaFichaDelMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            getJugadorActual().agregarFichaAJugador(fichaTomada);
        }
    }

    @Override
    public void colocarFichasEnTablero(List<Grupo> nuevosGrupos) {
        nuevosGrupos.forEach(Grupo::validarYEstablecerTipo);
        this.tablero.setFichasEnTablero(nuevosGrupos);
    }

    @Override
    public boolean validarYFinalizarTurno() {

        Jugador jugador = getJugadorActual();
        boolean esPrimerMovimiento
                = !jugador.isHaHechoPrimerMovimiento();

        // Regla 1: Todos los grupos en el tablero DEBEN ser válidos
        if (!tablero.esEstructuraDeGruposValida()) {
            revertirCambiosTurno();
            return false;
        }

        // Regla 2: Lógica específica para el primer movimiento
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

            // El jugador debe haber añadido al menos una ficha nueva al tablero.
            boolean haJugadoFichaNueva = idsFichasNuevas.stream()
                    .anyMatch(idNuevo -> !idsFichasAntiguas.contains(idNuevo));

            if (!haJugadoFichaNueva) {
                // El jugador solo movió fichas, no añadió ninguna.
                revertirCambiosTurno();
                return false;
            }
        }

        // Si todo es válido, confirmamos los cambios.
        confirmarCambiosTurno();
        return true;
    }

    @Override
    public void revertirCambiosDelTurno() {
        revertirCambiosTurno();
    }

    private void confirmarCambiosTurno() {
        for (Grupo g : this.tablero.getFichasEnTablero()) {
            g.setValidado(); // Pone esTemporal = false
        }

        List<Integer> idsEnTablero
                = this.tablero.getTodosLosIdsDeFichas();
        this.getJugadorActual().getManoJugador().removerFichasJugadas(idsEnTablero);

        guardarEstadoTurno();
    }

    private void revertirCambiosTurno() {
        this.tablero = this.tableroAlInicioDelTurno.copiaProfunda();
        this.getJugadorActual().setManoJugador(this.manoAlInicioDelTurno.copiaProfunda());
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

    @Override
    public List<Ficha> getManoDeJugador(int indiceJugador) {
        // Devuelve la mano del jugador local, ignorando el índice.
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
    public void setManoInicial(List<Ficha> mano) {
        // Asigna la mano al jugador local
        this.getJugadorActual().getManoJugador().setFichasEnMano(mano);
        guardarEstadoTurno();
    }

    @Override
    public List<Ficha> getMazo() {
        return this.tablero.getMazo();
    }
}
