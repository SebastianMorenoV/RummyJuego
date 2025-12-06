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
import vista.ObservadorSalaEspera;

/**
 *
 * @author benja
 */
public class ModeloSalaEspera implements IModeloSalaEspera, PropertyChangeListener {

    private List<ObservadorSalaEspera> observadores = new ArrayList<>();
    iDespachador despachador;
    String ipServidor;
    int puertoServidor;
    String ipCliente;
    String miId;

    public ModeloSalaEspera() {
    }

    public void indicarMostrarPantalla() {
        notificarObservadores("MOSTRAR_PANTALLA", null);
    }

    public void agregarObservador(ObservadorSalaEspera obs) {
        observadores.add(obs);
    }

    private void notificarObservadores(String evento, Object payload) {
        for (ObservadorSalaEspera obs : observadores) {
            obs.actualiza(this, evento, payload);
        }
    }

    public void enviarVoto(boolean aceptado) {
        try {
            if (despachador != null) {
                String mensaje = miId + ":RESPUESTA_VOTO:" + aceptado;
                System.out.println("[ModeloSalaEspera] Enviando voto: " + mensaje);
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setDatosRed(iDespachador despachador, String miId, String ipServer, int portServer) {
        this.despachador = despachador;
        this.miId = miId;
        this.ipServidor = ipServer;
        this.puertoServidor = portServer;
    }

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    public void setIpCliente(String ipCliente) {
        this.ipCliente = ipCliente;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        System.out.println("[ModeloSalaEspera] Evento de red recibido: " + evento);

        switch (evento) {
            case "PETICION_VOTO":
                notificarObservadores("PETICION_VOTO", payload);
                break;

            case "ACTUALIZAR_LISTA":
                String[] nombresJugadores = payload.split(",");
                notificarObservadores("ACTUALIZAR_LISTA", nombresJugadores);
                break;
        }
    }

}
