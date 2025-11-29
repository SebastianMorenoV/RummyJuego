/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import TipoEventos.EventoConfig;
import java.util.ArrayList;
import java.util.List;
import vista.ObservadorConfig;

/**
 *
 * @author benja
 */
public class ModeloConfig implements iModeloConfig {

    List<ObservadorConfig> observadores;

    public ModeloConfig() {
        observadores = new ArrayList<>();

    }

    @Override
    public void getPartida() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void iniciarCU(){
        notificarObservadores(EventoConfig.CREAR_PARTIDA);
    }

    @Override
    public void añadirObservador(ObservadorConfig obs) {
        observadores.add(obs);
    }

    public void notificarObservadores(EventoConfig evento) {
        for (ObservadorConfig observador : observadores) {
            observador.actualiza(this, evento);
        }
    }
}
