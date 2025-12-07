package modelo;

import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import Util.Configuracion;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Modelo para la Sala de Espera. Maneja el estado de los jugadores listos y la
 * sincronización de red.
 */
public class ModeloSalaEspera implements IModeloSalaEspera, PropertyChangeListener {

    private final static Logger logger = Logger.getLogger(ModeloSalaEspera.class.getName());

    private List<ObservadorSalaEspera> observadores;
    private Map<String, Boolean> jugadoresListos = new HashMap<>();
    private List<String> idsJugadoresEnSala;
    private iDespachador despachador;
    private String miId;
    private iEnsambladorCliente ensambladorCliente;
    private int miPuertoDeEscucha;

    public ModeloSalaEspera() {
        this.observadores = new ArrayList<>();
        this.idsJugadoresEnSala = new ArrayList<>();

        // Inicializar la lista de slots fijos para que la vista siempre sepa dónde dibujar
        this.idsJugadoresEnSala = List.of("Jugador1", "Jugador2", "Jugador3", "Jugador4");
    }

    /**
     * Recibe eventos delegados del Modelo principal (MVCJuego).
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payload = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        switch (evento) {
            case "SALA_ACTUALIZADA":
                logger.info("Evento SALA_ACTUALIZADA recibido. Payload: " + payload);
                actualizarEstadoGlobalDeSala(payload);
                notificarObservadores(TipoEvento.SALA);//notifica a la vista para que repinte los iconos
                break;
            default:
                break;
        }
    }

    /**
     * Deserializa la cadena del servidor (Ej: J1=true;J2=false;...) y actualiza
     * el estado local.
     */
    private void actualizarEstadoGlobalDeSala(String payload) {
        if (payload.isEmpty()) {
            return;
        }

        this.jugadoresListos.clear();

        String[] pares = payload.split(";");
        for (String par : pares) {
            if (par.isEmpty()) {
                continue;
            }

            String[] kv = par.split("=");
            if (kv.length == 2) {
                String id = kv[0];
                boolean listo = Boolean.parseBoolean(kv[1]);

                this.jugadoresListos.put(id, listo);
            }
        }
    }

    /**
     * ** MÉTODO DE INICIO DE CONEXIÓN ** Se llama desde EnsambladoresMVC.
     */
    public void iniciarConexionRed() {//esto va pal ensamblador!!!!!!!!!!!!!!!!!
        if (ensambladorCliente == null) {
            logger.severe("ERROR: EnsambladorCliente no inyectado. La red no iniciará.");
            return;
        }

        // 3. Registrar Cliente con el servidor (Este registro se usa para el envío del mensaje SALA_ACTUALIZADA)
        try {
            String ipCliente = InetAddress.getLocalHost().getHostAddress();
            String mensajeRegistro = miId + ":REGISTRAR:" + ipCliente + "$" + miPuertoDeEscucha;
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensajeRegistro);
        } catch (IOException ex) {
            Logger.getLogger(ModeloSalaEspera.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Lógica CU: El cliente envía el comando LISTO al servidor.
     */
    public void jugadorPulsaListo() {
        try {
            String comandoListo = miId + ":LISTO:";
            despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), comandoListo);

            // Actualización LOCAL (Para que la respuesta sea instantánea)
            jugadoresListos.put(miId, true);
            notificarObservadores(TipoEvento.SALA);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al enviar comando LISTO:", e);
        }
    }

    public void iniciarCU() {
        simularEntradaDeJugadores();
    }

    public void simularEntradaDeJugadores() {
        this.idsJugadoresEnSala = new ArrayList<>();
        List<String> allPlayerIds = List.of("Jugador1", "Jugador2", "Jugador3", "Jugador4");
        this.idsJugadoresEnSala.addAll(allPlayerIds);

        for (String id : this.idsJugadoresEnSala) {
            this.jugadoresListos.putIfAbsent(id, false);
        }

        notificarObservadores(TipoEvento.SALA);
    }

    public void notificarObservadores(TipoEvento evt) {
        for (ObservadorSalaEspera observador : observadores) {
            observador.actualiza(this, evt);
        }
    }

    public void agregarObservador(ObservadorSalaEspera obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public boolean isPartidaLista() {
        long jugadoresEnSala = idsJugadoresEnSala.size();
        long jugadoresListosCount = jugadoresListos.values().stream().filter(r -> r).count();
        return jugadoresEnSala >= 2 && jugadoresListosCount == jugadoresEnSala;
    }

    public void setEnsambladorCliente(iEnsambladorCliente ensambladorCliente) {
        this.ensambladorCliente = ensambladorCliente;
    }

    public void setMiPuertoDeEscucha(int miPuertoDeEscucha) {
        this.miPuertoDeEscucha = miPuertoDeEscucha;
    }

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

    public Map<String, Boolean> getJugadoresListos() {
        return jugadoresListos;
    }

    public List<String> getIdsJugadoresEnSala() {
        return idsJugadoresEnSala;
    }

    public String getMiId() {
        return miId;
    }

    @Override
    public void cerrarCU() {
        notificarObservadores(TipoEvento.CERRAR_CU);
    }

}
