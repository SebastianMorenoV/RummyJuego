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
import Vista.ObservadorPantallaInicial;

/**
 *
 * @author benja
 */
public class ModeloCUPrincipal implements IModeloPantallaInicial, PropertyChangeListener {
    
    private List<ObservadorPantallaInicial> observadores = new ArrayList<>();
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
        String eventoRed = evt.getPropertyName(); 
        
        System.out.println("[ModeloPrincipal] Recibido del servidor: " + eventoRed);

        switch (eventoRed) {
            case "UNION_ACEPTADA":
                notificarObservadores(Evento.SOLICITAR_UNIRSE_A_PARTIDA);
                break;
                
            case "UNION_RECHAZADA":
                notificarObservadores(Evento.UNION_RECHAZADA);
                break;
                
            case "ERROR_SALA_LLENA":
                notificarObservadores(Evento.ERROR_SALA_LLENA);
                break;
                
            case "ERROR_VOTACION_EN_CURSO":
                notificarObservadores(Evento.ERROR_VOTACION_EN_CURSO);
                break;
                
            case "ERROR_PARTIDA_INICIADA":
                notificarObservadores(Evento.ERROR_PARTIDA_YA_INICIADA);
                break;
                
            default:
                break;
        }
    }

    public void añadirObservador(ObservadorPantallaInicial observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(Evento evento) {
        for (ObservadorPantallaInicial obs : observadores) {
            obs.actualiza(this, evento);
        }
    }
}