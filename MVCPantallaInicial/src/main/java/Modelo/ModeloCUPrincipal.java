/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import eventos.Evento;
import Vista.Observador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benja
 */
public class ModeloCUPrincipal implements IModeloLobby, PropertyChangeListener {
    
    List<Observador> observadores;
    
    public ModeloCUPrincipal() {
        observadores = new ArrayList<>();
        
    }
    
    @Override
    public String getPartida() {
        return "Partida_Activa_Mock";
    }
    
    public void SolicitarUnirseApartida(){
        notificarObservadores(Evento.SOLICITAR_UNIRSE_A_PARTIDA);
    }
    
    public void iniciarCreacionPartida() {
        notificarObservadores(Evento.CREAR_PARTIDA);
        
    }
    public void iniciarLobby(){
        notificarObservadores(Evento.INICIO);
    }
    
    public void añadirObservador(Observador obs) {
        observadores.add(obs);
    }
    
    public void notificarObservadores(Evento evento) {
        for (Observador observador : observadores) {
            observador.actualiza(this, evento);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
