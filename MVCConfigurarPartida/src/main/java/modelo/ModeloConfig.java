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
 *
 * @author benja
 */
public class ModeloConfig implements iModeloConfig {

    List<ObservadorConfig> observadores;
    iDespachador despachador;

    public ModeloConfig() {
        observadores = new ArrayList<>();

    }

    @Override
    public void getPartida() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void iniciarCU() {
        notificarObservadores(EventoConfig.CREAR_PARTIDA);
    }

    @Override
    public void configurarPartida(int comodines, int fichas) {
        try {
            // 1. Obtener el payload actual ("2$10")
            String payload = serializarConfiguracion(comodines, fichas);

            // 2. Definir el ID (puede ser "Host" o pasarse como variable) y el Comando
            String idCliente = "REMPLAZAR_ESTE_NOMBRE_AL_REGISTRAR_@CHRIS";
            String comando = "CONFIGURAR_PARTIDA";
            String puerto = "9001";
            String ipClienteMock = "192.168.100.3";

            // 3. Construir el mensaje con el formato correcto: ID:COMANDO:PAYLOAD
            String mensajeProtocolo = idCliente + ":" + comando + ":" + payload;

            // 4. Enviar
            despachador.enviar("192.168.100.3", 5000, mensajeProtocolo);

            String mensajeRegistro = idCliente + ":REGISTRAR:" + ipClienteMock + "$" + puerto;
            despachador.enviar("192.168.100.3", 5000, mensajeRegistro);

        } catch (IOException ex) {
            Logger.getLogger(ModeloConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String serializarConfiguracion(int comodines, int fichas) {
        return String.valueOf(comodines) + "$" + String.valueOf(fichas);
    }

    @Override
    public void añadirObservador(ObservadorConfig obs) {
        observadores.add(obs);
    }

    public void notificarObservadores(EventoConfig evento) {
        for (ObservadorConfig observador : observadores) {
            observador.actualiza(this, evento);
        }
    }

    @Override
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

}
