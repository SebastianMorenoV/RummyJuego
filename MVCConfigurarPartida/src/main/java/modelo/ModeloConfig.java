package modelo;

import TipoEventos.EventoConfig;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.ObservadorConfig;

/**
 * Esta clase representa los datos de todo el mundo MVC "Configurar Partida"
 * Contiene metodos para mandar fuera al Blackboard con la configuracion dada.
 *
 * @author benja
 */
public class ModeloConfig implements iModeloConfig, PropertyChangeListener {

    List<ObservadorConfig> observadores;
    iDespachador despachador;
    String ipServidor;
    int puertoServidor;
    String ipCliente;
    int puertoCliente;
    String idCliente;

    public ModeloConfig() {
        observadores = new ArrayList<>();
    }

    public void iniciarCU() {
        notificarObservadores(EventoConfig.CREAR_PARTIDA);
    }

    public void cerrarCU() {
        notificarObservadores(EventoConfig.CERRAR_CU);
    }

    public void configurarPartida(int comodines, int fichas) {
        try {
            String payload = serializarConfiguracion(comodines, fichas);
            String comando = "CONFIGURAR_PARTIDA";
            String mensajeProtocolo = idCliente + ":" + comando + ":" + payload;

            despachador.enviar(ipServidor, puertoServidor, mensajeProtocolo);

        } catch (IOException ex) {
            Logger.getLogger(ModeloConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void notificarObservadores(EventoConfig evento) {
        for (ObservadorConfig observador : observadores) {
            observador.actualiza(this, evento);
        }
    }

    private String serializarConfiguracion(int comodines, int fichas) {
        return String.valueOf(comodines) + "$" + String.valueOf(fichas);
    }

    public void añadirObservador(ObservadorConfig obs) {
        observadores.add(obs);
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

    public void setPuertoCliente(int puertoCliente) {
        this.puertoCliente = puertoCliente;
    }

    public void setIdCliente(String id) {
        this.idCliente = id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();

        switch (evento) {
            case "PARTIDA-CREADA-EXITO":
                notificarObservadores(EventoConfig.PARTIDA_CREADA); // agregar aqui
                break;
        }
    }

}
