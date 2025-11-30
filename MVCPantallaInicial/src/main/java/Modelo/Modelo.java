/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import eventos.Evento;
import Vista.Observador;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benja
 */
public class Modelo implements IModelo {
    
    List<Observador> observadores;
    
    public Modelo() {
        observadores = new ArrayList<>();
        
    }
    
    @Override
    public String getPartida() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void SolicitarUnirseApartida(){
        notificarObservadores(Evento.SOLICITAR_UNIRSE_A_PARTIDA);
    }
    
    public void iniciarCreacionPartida() {
        notificarObservadores(Evento.CREAR_PARTIDA);
        
    }
    
    public void añadirObservador(Observador obs) {
        observadores.add(obs);
    }
    
    public void notificarObservadores(Evento evento) {
        for (Observador observador : observadores) {
            observador.actualiza(this, evento);
        }
    }
}
