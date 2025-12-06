/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import eventos.Evento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Vista.ObservadorSalaEspera;

/**
 *
 * @author benja
 */
public class ModeloCUPrincipal implements IModeloPantallaInicial, PropertyChangeListener {
    
    private List<ObservadorSalaEspera> observadores = new ArrayList<>();
    private iDespachador despachador;
    private String miId;
    private String ipServidor;
    private int puertoServidor;
    private String miIp;
    private int miPuertoEscucha;

    public void setDatosRed(iDespachador despachador, String miId, String ipServer, int portServer, String miIp, int miPuerto) {
        this.despachador = despachador;
        this.miId = miId;
        this.ipServidor = ipServer;
        this.puertoServidor = portServer;
        this.miIp = miIp;
        this.miPuertoEscucha = miPuerto;
    }

    public void SolicitarUnirseApartida(){
        try {
            if(despachador == null) {
                System.err.println("[ModeloPrincipal] Error: Despachador no configurado");
                return;
            }
            String mensaje = miId + ":SOLICITAR_UNIRSE:" + miIp + "$" + miPuertoEscucha;
            System.out.println("[ModeloPrincipal] Enviando: " + mensaje);
            despachador.enviar(ipServidor, puertoServidor, mensaje);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        
        if (evento.equals("UNION_ACEPTADA")) {
            System.out.println("[ModeloPrincipal] ¡Me aceptaron! Entrando a sala.");
            notificarObservadores(Evento.SOLICITAR_UNIRSE_A_PARTIDA); 
        } else if (evento.equals("UNION_RECHAZADA")) {
            notificarObservadores(Evento.RECHAZADO);
        }
    }

    public void añadirObservador(ObservadorSalaEspera observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(Evento evento) {
        for (ObservadorSalaEspera obs : observadores) {
            obs.actualiza(this, evento);
        }
    }
}