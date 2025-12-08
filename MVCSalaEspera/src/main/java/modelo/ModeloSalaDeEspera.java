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
 * @author benja
 */
public class ModeloSalaDeEspera implements IModeloSalaDeEspera, PropertyChangeListener {

    private iDespachador despachador;
    private String ipServidor;
    private int puertoServidor;
    private String idCliente;

    private List<ObservadorSalaDeEspera> observadores = new ArrayList<>();

    @Override
    public void votarParaIniciar() {
        // protocolo: ID:COMANDO:
        String mensaje = idCliente + ":ESTOY_LISTO:";

        System.out.println("[ModeloSalaEspera] Enviando voto de inicio al servidor...");

        try {
            if (despachador != null) {
                despachador.enviar(ipServidor, puertoServidor, mensaje);

                // Avisar a la vista localmente para que bloquee el botón
                notificarObservadores(EventoSalaEspera.VOTO_REGISTRADO, null);

            } else {
                System.err.println("Error: El despachador no está inicializado.");
            }
        } catch (IOException ex) {
            Logger.getLogger(ModeloSalaDeEspera.class.getName()).log(Level.SEVERE, null, ex);
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
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        if (evento.equals("ACTUALIZAR_SALA")) {
            System.out.println("[ModeloSala] Recibí lista de jugadores: " + payload);
            // Avisamos a la vista y le pasamos los datos sucios (String)
            notificarObservadores(EventoSalaEspera.ACTUALIZAR_DATOS_JUGADORES, payload);
        }
    }
}
