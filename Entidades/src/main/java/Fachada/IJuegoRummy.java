/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Fachada;

import Entidades.Ficha;
import Entidades.Grupo;

import java.util.List;

/**
 * Interfaz que define el contrato para la lógica del juego de Rummy. Actúa como
 * una abstracción entre el Modelo y la implementación concreta del juego.
 */
public interface IJuegoRummy {

    void iniciarPartida();

    void jugadorTomaFichaDelMazo();

    void colocarFichasEnTablero(List<Grupo> nuevosGrupos);

    boolean validarYFinalizarTurno();
    
    void revertirCambiosDelTurno();

    List<Ficha> getManoJugador();

    List<Grupo> getGruposEnTablero();

    int getCantidadFichasMazo();

    boolean haGanadoElJugador();
    
    boolean intentarRegresarFichaAMano(int idFicha);
}
