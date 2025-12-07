package Modelo;

import eventos.Evento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Vista.ObservadorLobby;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase representa el modelo de datos para CU PantallaPrincipal
 *
 * @author benja
 */
public class ModeloCUPrincipal implements IModeloLobby, PropertyChangeListener {

    List<ObservadorLobby> observadores;
    iDespachador despachador;
    private String miIp;
    private int miPuerto;
    String ipServidor;
    int puertoServidor;
    private String idCliente;

    public ModeloCUPrincipal() {
        observadores = new ArrayList<>();

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();

        switch (evento) {
            case "ACCESO_DENEGADO":
                notificarObservadores(Evento.ACCESO_DENEGADO);
                break;
            case "PARTIDA-EXISTENTE":
                notificarObservadores(Evento.PARTIDA_EXISTENTE); // agregar aqui
                break;

            case "PUEDES_CONFIGURAR":
                notificarObservadores(Evento.CREAR_PARTIDA);
                break;
        }
    }

    public void SolicitarUnirseApartida() {
        String mensaje = idCliente + ":SOLICITAR_UNIRSE:" + miIp + "$" + miPuerto;
        if (despachador != null) {
            try {
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            } catch (IOException ex) {
                Logger.getLogger(ModeloCUPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void iniciarCreacionPartida() {
        try {
            String mensaje = idCliente + ":SOLICITAR_CREACION:" + miIp + "$" + miPuerto;

            if (despachador != null) {
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cerrarCU() {
        notificarObservadores(Evento.CERRAR_CU);
    }

    public void iniciarCU() {
        try {
            String mensaje = idCliente + ":REGISTRAR:" + miIp + "$" + miPuerto;

            if (despachador != null) {
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }

            notificarObservadores(Evento.INICIO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciarLobby() {
        notificarObservadores(Evento.INICIO);
    }

    @Override
    public String getPartida() {
        System.out.println("No utilizado.");
        return null;
    }

    public void añadirObservador(ObservadorLobby obs) {
        observadores.add(obs);
    }

    public void notificarObservadores(Evento evento) {
        for (ObservadorLobby observador : observadores) {
            observador.actualiza(this, evento);
        }
    }

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public String getMiIp() {
        return miIp;
    }

    public void setMiIp(String miIp) {
        this.miIp = miIp;
    }

    public int getMiPuerto() {
        return miPuerto;
    }

    public void setMiPuerto(int miPuerto) {
        this.miPuerto = miPuerto;
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public int getPuertoServidor() {
        return puertoServidor;
    }

    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    public void setIdCliente(String id) {
        this.idCliente = id;
    }

}
