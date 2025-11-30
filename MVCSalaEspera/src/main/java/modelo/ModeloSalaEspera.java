/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import contratos.Configuracion;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import vista.Observador;

/**
 *
 * @author benja
 */
public class ModeloSalaEspera implements IModelo, PropertyChangeListener {

    private List<Observador> observadores = new ArrayList<>();
    private iDespachador despachador;
    private String miId = "Host"; 

    public ModeloSalaEspera() {
        this.observadores = new ArrayList<>();
    }
    

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }
    
    public void setMiId(String id) {
        this.miId = id;
    }

    public void agregarObservador(Observador obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(String evento, Object datos) {
        for (Observador obs : observadores) {
            obs.actualiza(evento, datos);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (String) evt.getNewValue();

        if (evento.equals("PETICION_VOTO")) {
            System.out.println("[SalaEspera] Petición de voto recibida para: " + payload);
            notificarObservadores("MOSTRAR_VOTACION", payload);
        }
        
        if (evento.equals("JUGADOR_NUEVO_EN_SALA")) {
             System.out.println("[SalaEspera] Nuevo jugador aceptado: " + payload);
        }
    }

    public void enviarVoto(boolean aceptado) {
        try {
            if (despachador == null) {
                System.err.println("[SalaEspera] Error: Despachador no configurado.");
                return;
            }
            String mensaje = miId + ":RESPUESTA_VOTO:" + aceptado;
            despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);
            System.out.println("[SalaEspera] Voto enviado: " + aceptado);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
