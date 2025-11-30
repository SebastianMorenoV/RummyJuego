/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import eventos.Evento;
import Vista.Observador;
import contratos.Configuracion;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benja
 */
public class Modelo implements iModelo,PropertyChangeListener {
    
    List<Observador> observadores;
    private iDespachador despachador;
    public Modelo() {
        observadores = new ArrayList<>();
        
    }
    
    @Override
    public String getPartida() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public void SolicitarUnirseApartida(){
        try {
            String miIp = InetAddress.getLocalHost().getHostAddress();
            int miPuerto = 9002; 
            String miId = "Jugador_" + System.currentTimeMillis(); 

            String mensaje = miId + ":SOLICITAR_UNION:" + miIp + "$" + miPuerto;
            
            if (despachador != null) {
                System.out.println("Enviando solicitud de unión...");
                despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Aquí recibimos la respuesta del Servidor (vía Listener -> Procesador)
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        
        if (evento.equals("RESPUESTA_UNION")) {
            String respuesta = (String) evt.getNewValue(); // ACEPTADO, NO_EXISTE, etc.
            
            switch (respuesta) {
                case "NO_EXISTE":
                    notificarObservadores(Evento.ERROR_NO_HAY_PARTIDA);
                    break;
                case "YA_INICIADA":
                    notificarObservadores(Evento.ERROR_PARTIDA_INICIADA);
                    break;
                case "RECHAZADO":
                    notificarObservadores(Evento.UNION_RECHAZADA);
                    break;
                case "ACEPTADO":
                    notificarObservadores(Evento.UNION_ACEPTADA);
                    break;
            }
        }
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
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }
}
