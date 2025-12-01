package modelo;

import TipoEventos.EventoConfig;
import contratos.iDespachador;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.ObservadorConfig;

/**
 * Esta clase representa los datos de todo el mundo MVC "Configurar Partida"
 * Contiene metodos para mandar fuera al Blackboard con la configuracion dada.
 * @author benja
 */
public class ModeloConfig implements iModeloConfig {

    List<ObservadorConfig> observadores;
    iDespachador despachador;

    public ModeloConfig() {
        observadores = new ArrayList<>();
    }

    @Override
    public void iniciarCU() {
        notificarObservadores(EventoConfig.CREAR_PARTIDA);
    }

    @Override
    public void configurarPartida(int comodines, int fichas) {
        try {
            String payload = serializarConfiguracion(comodines, fichas);
            String idCliente = "REMPLAZAR_ESTE_NOMBRE_AL_REGISTRAR_@CHRIS";
            String comando = "CONFIGURAR_PARTIDA";
            String puerto = "9001";
            String ipClienteMock = "192.168.100.3";

            String mensajeProtocolo = idCliente + ":" + comando + ":" + payload;

            despachador.enviar("192.168.100.3", 5000, mensajeProtocolo);

            String mensajeRegistro = idCliente + ":REGISTRAR:" + ipClienteMock + "$" + puerto;
            despachador.enviar("192.168.100.3", 5000, mensajeRegistro);

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

    @Override
    public void añadirObservador(ObservadorConfig obs) {
        observadores.add(obs);
    }

    @Override
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

}
