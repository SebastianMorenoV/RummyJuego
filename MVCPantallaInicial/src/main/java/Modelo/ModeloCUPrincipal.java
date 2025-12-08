/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import eventos.Evento;
import java.util.ArrayList;
import java.util.List;
import Vista.ObservadorLobby;

/**
 *
 * @author benja
 */
public class ModeloCUPrincipal implements IModeloLobby {
    
    List<ObservadorLobby> observadores;
    
    public ModeloCUPrincipal() {
        observadores = new ArrayList<>();
        
    }
    
    @Override
    public String getPartida() {
        return "Partida_De_Prueba";
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
    
    public void añadirObservador(ObservadorLobby obs) {
        observadores.add(obs);
    }
    
    public void notificarObservadores(Evento evento) {
        for (ObservadorLobby observador : observadores) {
            observador.actualiza(this, evento);
        }
    }
}
