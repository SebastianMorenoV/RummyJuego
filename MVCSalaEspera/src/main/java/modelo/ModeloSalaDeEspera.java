/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tipoEventos.EventoSalaEspera;
import vista.ObservadorSalaDeEspera;

/**
 *
 * @author chris
 */
public class ModeloSalaDeEspera implements IModeloSalaDeEspera, PropertyChangeListener {

    private iDespachador despachador;
    private String ipServidor;
    private int puertoServidor;
    private String idCliente;

    private List<ObservadorSalaDeEspera> observadores = new ArrayList<>();

    @Override
    public void enviarVotoInicio(boolean aceptado) {
        try {
            if (despachador != null) {
                // Enviamos el voto de vuelta al servidor
                String mensaje = idCliente + ":RESPUESTA_VOTO_INICIO:" + aceptado;
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Este es el método que llama el Controlador para iniciar todo.
     */
    @Override
    public void iniciarCU() {
        notificarObservadores(EventoSalaEspera.MOSTRAR_SALA, null);
    }

    /**
     * Este es el método que llama el Controlador cuando el juego arranca.
     */
    @Override
    public void cerrarCU() {
        notificarObservadores(EventoSalaEspera.CERRAR_CU, null);
    }

    public void agregarObservador(ObservadorSalaDeEspera obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    private void notificarObservadores(EventoSalaEspera evento, Object datos) {
        for (ObservadorSalaDeEspera obs : observadores) {
            obs.actualiza(this, evento, datos);
        }
    }

    @Override
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    @Override
    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    @Override
    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    @Override
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public void enviarVoto(boolean aceptado) {
        try {
            if (despachador != null) {
                String mensaje = idCliente + ":RESPUESTA_VOTO:" + aceptado;
                System.out.println("[ModeloSalaEspera] Enviando voto: " + mensaje);
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void solicitarInicioPartida() {
        try {
            if (despachador != null) {
                String mensaje = idCliente + ":SOLICITAR_INICIO_PARTIDA:"; 
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        if (evento.equals("ACTUALIZAR_SALA")) {
            System.out.println("[ModeloSala] Recibí lista de jugadores: " + payload);
            // Avisamos a la vista y le pasamos los datos sucios (String)
            notificarObservadores(EventoSalaEspera.ACTUALIZAR_DATOS_JUGADORES, payload);
        }
        if (evento.equals("PETICION_VOTO")) {
            if (!payload.equals(this.idCliente)) {
                notificarObservadores(EventoSalaEspera.PETICION_VOTO, payload);
            } else {
                System.out.println("[ModeloSala] Ignorando petición de voto propia.");
            }
        }
        if (evento.equals("MANO_INICIAL")) {
            System.out.println("[ModeloSala] Partida iniciada. Cerrando sala de espera...");
            cerrarCU();
        }
        if (evento.equals("PETICION_VOTO_INICIO")) {
            notificarObservadores(EventoSalaEspera.PETICION_VOTO_INICIO, payload);
        }
        
        if (evento.equals("INICIO_PARTIDA_RECHAZADA")) {
            notificarObservadores(EventoSalaEspera.INICIO_PARTIDA_RECHAZADA, payload);
        }
    }


}
