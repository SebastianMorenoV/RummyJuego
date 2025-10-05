package Fachada;

import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;

import java.util.List;

/**
* Interfaz que define el contrato para la lógica del juego de Rummy. Actua como
* una abstracción entre el Modelo y la implementación concreta del juego.
*/
public interface IJuegoRummy {

    void iniciarPartida();

    void jugadorTomaFichaDelMazo();

    void colocarFichasEnTablero(List<Grupo> nuevosGrupos);

    boolean validarYFinalizarTurno();

    void siguienteTurno();

    Jugador getJugadorActual();

    void revertirCambiosDelTurno();

    List<Grupo> getGruposEnTablero();

    List<Ficha> getManoDeJugador(int indiceJugador);

    int getCantidadFichasMazo();

    boolean haGanadoElJugador();

    boolean intentarRegresarFichaAMano(int idFicha);

}
