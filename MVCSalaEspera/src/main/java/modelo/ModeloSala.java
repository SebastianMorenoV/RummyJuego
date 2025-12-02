/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import Dtos.ActualizacionSalaDTO;
import Util.Configuracion;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.Observador;
import vista.TipoEventoSala;

/**
 *
 * @author benja
 */
public class ModeloSala implements IModeloSala, PropertyChangeListener{
    private List<Observador> observadores;
    private iDespachador despachador;
    private String miId; // Necesitas saber quién eres

    public ModeloSala() {
        observadores = new ArrayList<>();
    }
    
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

    public void enviarSolicitudInicio() {
        if (despachador == null || miId == null) {
            System.err.println("[SalaEspera] Error: Despachador o ID no configurados.");
            return;
        }

        try {
            // PROTOCOLO: ID:COMANDO:PAYLOAD
            String mensaje = this.miId + ":ESTOY_LISTO:";
            System.out.println("[SalaEspera] Enviando solicitud de inicio: " + mensaje);
            
            // Usamos la configuración global para saber a dónde enviar
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);
            
            // Opcional: Notificar a la vista que ya se envió (para deshabilitar el botón)
            notificarObservadores(TipoEventoSala.SOLICITAR_INICIO); 
            
        } catch (IOException ex) {
            Logger.getLogger(ModeloSala.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void añadirObservador(Observador obs) {
        observadores.add(obs);
    }
    
    public void notificarObservadores(TipoEventoSala evento) {
        for (Observador observador : observadores) {
            ActualizacionSalaDTO dto = new ActualizacionSalaDTO(evento);
            observador.actualiza(this, dto);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        
        //o cualquier cosa que señale el comienzo de la partida.
        if (evento.equals("MANO_INICIAL")) {
            System.out.println("[Modelo SalaEspera] ¡Juego iniciado por el servidor!");
            
            //posiblemente cambiar dependiendo de lo que siga
            notificarObservadores(TipoEventoSala.COMENZAR_JUEGO);
        }
    }
}
