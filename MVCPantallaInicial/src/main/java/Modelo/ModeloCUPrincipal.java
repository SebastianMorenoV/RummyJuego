package Modelo;

import eventos.Evento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    private String datosSalaCache;

    public void setIdCliente(String id) {
        this.idCliente = id;
    }

    public ModeloCUPrincipal() {
        observadores = new ArrayList<>();

    }

    public String getPartida() {
        throw new UnsupportedOperationException("Aun no se usa");

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

//            // DEBUG
//            System.out.println("[MOCK] Saltando espera de red para pruebas locales.");
//            notificarObservadores(Evento.CREAR_PARTIDA);

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

    public String getDatosSala() {
        return datosSalaCache;
    }

    /**
     * Logica para la votacion en Sala de Espera
     */
    public void enviarEstoyListo() {
        try {
            String mensaje = idCliente + ":ESTOY_LISTO:" + miIp + "$" + miPuerto;
            if (despachador != null) {
                despachador.enviar(ipServidor, puertoServidor, mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        System.out.println("[CLIENTE DEBUG] Modelo recibió evento: " + evento);

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

            case "CREAR_PARTIDA":
                System.out.println("[Modelo Cliente] ¡Recibí CREAR_PARTIDA! Notificando vista...");
                notificarObservadores(Evento.CREAR_PARTIDA);
                break;

            case "UNIRSE_PARTIDA":
                System.out.println("[Modelo Cliente] ¡Permiso recibido (" + evento + ")! Yendo a registro...");

                // Ir a la pantalla de poner nombre y avatar.
                notificarObservadores(Evento.CREAR_PARTIDA);
                break;

            case "ACTUALIZAR_SALA":
                this.datosSalaCache = payload;

                // Avisamos a la VistaLobby que llegaron datos nuevos
                notificarObservadores(Evento.ACTUALIZAR_SALA);
                break;

            case "MANO_INICIAL": //
                System.out.println("[Modelo] ¡Recibí mi mano! El juego ha comenzado.");

                // Guardamos la mano o se la pasamos a la vista de juego
                // payload contiene: Cartas $ MazoCount $ Metadatos
                notificarObservadores(Evento.INICIO_JUEGO);
                break;

        }
    }

}
