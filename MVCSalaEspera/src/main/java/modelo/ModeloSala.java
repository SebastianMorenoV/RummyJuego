/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import Dtos.ActualizacionSalaDTO;
import Util.Configuracion;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.TipoEventoSala;
import vista.ObservadorSala;

/**
 *
 * @author benja
 */
public class ModeloSala implements IModeloSala, PropertyChangeListener{
    private List<ObservadorSala> observadores;
    private iDespachador despachador;
    private String miId;

    
    private final static Logger logger = Logger.getLogger(ModeloSala.class.getName());

    private iEnsambladorCliente ensambladorCliente;
    private int miPuertoDeEscucha;

    public ModeloSala() {
        observadores = new ArrayList<>();

    }
    

    @Override
    public void iniciarConexionRed() {
        if (ensambladorCliente == null) {
            logger.severe("ERROR: EnsambladorCliente no inyectado. La red no iniciará.");
            return;
        }

        try {
            String ipCliente = InetAddress.getLocalHost().getHostAddress();
            String mensajeRegistro = miId + ":REGISTRAR:" + ipCliente + "$" + miPuertoDeEscucha;
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensajeRegistro);
        } catch (IOException ex) {
            Logger.getLogger(ModeloSala.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    

    
    @Override
    public void setEnsambladorCliente(iEnsambladorCliente ensambladorCliente) {
        this.ensambladorCliente = ensambladorCliente;
    }

    @Override
    public void setMiPuertoDeEscucha(int miPuertoDeEscucha) {
        this.miPuertoDeEscucha = miPuertoDeEscucha;
    }


    @Override
    public String getMiId() {
        return miId;
    }
    
    @Override
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    @Override
    public void setMiId(String miId) {
        this.miId = miId;
    }
    

    @Override
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

    public void añadirObservador(ObservadorSala obs) {
        observadores.add(obs);
    }
    
    public void notificarObservadores(TipoEventoSala evento) {
        for (ObservadorSala observador : observadores) {
            ActualizacionSalaDTO dto = new ActualizacionSalaDTO(evento);
            observador.actualiza(this, dto);
        }
    }
    
    public void notificarObservadores2(ActualizacionSalaDTO dto) {
    for (ObservadorSala observador : observadores) {
        observador.actualiza(this, dto);
    }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (String) evt.getNewValue();
        
        if (evento.equals("ACTUALIZAR_SALA")) {
            // Payload viene como: "Juan:true;Pedro:false;Maria:true"
            List<ActualizacionSalaDTO.JugadorInfo> listaJugadores = new ArrayList<>();
            
            if (payload != null && !payload.isEmpty()) {
                String[] jugadoresData = payload.split(";");
                
                for (String jugadorStr : jugadoresData) {
                    // Separamos nombre y estado (Juan : true)
                    String[] datos = jugadorStr.split(":");
                    if (datos.length == 2) {
                        String nombre = datos[0];
                        boolean listo = Boolean.parseBoolean(datos[1]);
                        listaJugadores.add(new ActualizacionSalaDTO.JugadorInfo(nombre, listo));
                    }
                }
            }

            // Enviamos la lista completa a la vista
            ActualizacionSalaDTO dto = new ActualizacionSalaDTO(
                TipoEventoSala.ACTUALIZAR_CONTADORES, listaJugadores
            );
            notificarObservadores2(dto);
        }
        else if (evento.equals("MANO_INICIAL")) {
            System.out.println("[Modelo SalaEspera] ¡Juego iniciado! Cerrando sala...");

            // Avisar a la vista para que se cierre y arranque el juego
            ActualizacionSalaDTO dto = new ActualizacionSalaDTO(TipoEventoSala.COMENZAR_JUEGO);
            notificarObservadores2(dto);
        }
    }

}
