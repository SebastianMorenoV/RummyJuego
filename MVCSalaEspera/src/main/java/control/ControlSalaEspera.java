/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import contratos.controladoresMVC.iControlEjercerTurno;
import contratos.controladoresMVC.iControlSalaEspera;
import modelo.ModeloSalaEspera;

/**
 *
 * @author benja
 */
public class ControlSalaEspera implements iControlSalaEspera {
    
    private ModeloSalaEspera modelo;
    private iControlEjercerTurno controlEjercerTurno;
    public ControlSalaEspera(ModeloSalaEspera modelo) {
        this.modelo = modelo;
    }
    
    @Override
    public void iniciarSalaDeEspera() {
        System.out.println("[ControlSalaEspera] Recibida orden de iniciar. Notificando al modelo.");
        modelo.indicarMostrarPantalla();
    }
    
    @Override
    public void enviarVoto(boolean aceptado) {
        System.out.println("[ControlSalaEspera] Procesando voto: " + aceptado);
        modelo.enviarVoto(aceptado);
    }

    @Override
    public void setEjercerTurno(iControlEjercerTurno cet) {
        this.controlEjercerTurno = cet;
    }
    
    @Override
    public void solicitarInicioPartida() {
        System.out.println("[ControlSala] Solicitando inicio de partida al servidor...");
        modelo.solicitarInicioPartida();
    }
    @Override
    public void navegacionEjercerTurno() {
        if (controlEjercerTurno != null) {
            System.out.println("[ControlSala] Navegando hacia MVC Juego...");
            controlEjercerTurno.IniciarEjercerTurno(); 
        } else {
            System.err.println("[ControlSala] ERROR CRÍTICO: Controlador de Juego es NULL.");
        }
    }

    
}
