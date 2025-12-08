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
    private iDespachador despachador;
    private String ipServidor;
    private int puertoServidor;
    private String miId;

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
            case "MANO_INICIAL":
                System.out.println("[ModeloSala] ¡Juego Iniciado! Notificando vista.");
                notificarObservadores("JUEGO_INICIADO", payload);
                notificarObservadores("CERRAR_SALA", null);
                break;

            case "ERROR_PARTIDA_NO_INICIADA":
                notificarObservadores("ERROR_INICIO", payload);
                break;
        }
    }

    public void solicitarInicioPartida() {
        try {
            if (despachador != null) {
                String mensaje = miId + ":INICIAR_PARTIDA:null";
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
