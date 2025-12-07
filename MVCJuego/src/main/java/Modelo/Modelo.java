package Modelo;

import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Dtos.ActualizacionDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Fachada.IJuegoRummy;
import Fachada.JuegoRummyFachada;
import Vista.Observador;
import Vista.TipoEvento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jdk.jshell.execution.Util;

/**
 * Clase Modelo en el patrón MVC. Gestiona el estado local del juego, interactúa
 * con la Fachada de lógica de juego (juego) y maneja la comunicación de red
 * mediante el despachador.
 *
 * @author benja
 */
public class Modelo implements IModelo, PropertyChangeListener {

    private List<Observador> observadores;
    private final IJuegoRummy juego;
    private List<GrupoDTO> gruposDeTurnoDTO;
    private List<GrupoDTO> gruposDTOAlInicioDelTurno;
    private Map<String, Integer> conteoFichasRivales = new HashMap<>();
    private String idJugadorEnTurnoGlobal = "";
    private boolean esMiTurno;
    private iDespachador despachador;
    private String miId;

    private int mazoFichasRestantes = 0;
    private String ipServidor;
    private int puertoServidor;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>();
        this.esMiTurno = false;
    }

    /**
     * Inicia el juego localmente y notifica a la vista para generar fichas
     * iniciales.
     */
    public void iniciarJuego() {
        juego.iniciarPartida();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    /**
     * Maneja eventos recibidos desde red (servidor) usando observer pattern.
     * Actualiza el modelo según el tipo de evento.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payloadd = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        switch (evento) {

            case "COMANDO_INICIAR_PARTIDA":
                System.out.println("[Modelo] Recibida orden del servidor para iniciar la partida.");
                enviarComandoIniciarPartida();
                break;

            case "MANO_INICIAL":
                System.out.println("[Modelo] Evento 'MANO_INICIAL' detectado!");
                try {
                    String[] payloadPartes = payloadd.split("\\$");
                    String manoPayload = payloadPartes[0];

                    if (payloadPartes.length > 1) {
                        this.mazoFichasRestantes = Integer.parseInt(payloadPartes[1]);
                    }

                    List<FichaJuegoDTO> fichasDTO = deserializarMano(manoPayload);
                    List<Ficha> manoEntidad = fichasDTO.stream()
                            .map(this::convertirFichaDtoAEntidad)
                            .collect(Collectors.toList());

                    juego.setManoInicial(manoEntidad);

                    notificarObservadores(TipoEvento.TOMO_FICHA);
                    notificarObservadores(TipoEvento.REPINTAR_MANO);
                    notificarObservadores(TipoEvento.INICIAR_PANTALLA);
                    notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
                } catch (Exception e) {
                    System.err.println("[Modelo] Error al procesar MANO_INICIAL: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "FICHA_RECIBIDA":
                System.out.println("[Modelo] Evento 'FICHA_RECIBIDA' detectado!");
                try {
                    FichaJuegoDTO fichaDTO = FichaJuegoDTO.deserializar(payloadd);
                    if (fichaDTO != null) {
                        Ficha fichaEntidad = convertirFichaDtoAEntidad(fichaDTO);
                        juego.getJugadorActual().agregarFichaAJugador(fichaEntidad);

                        this.mazoFichasRestantes--;

                        notificarObservadores(TipoEvento.REPINTAR_MANO);
                        notificarObservadores(TipoEvento.TOMO_FICHA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "TURNO_CAMBIADO":
                System.out.println("[Modelo] Evento 'TURNO_CAMBIADO' detectado! Payload: " + payloadd);
                String[] partesTurno = payloadd.split(":");
                this.idJugadorEnTurnoGlobal = partesTurno[0];
                String nuevoJugadorId = partesTurno[0];
                this.esMiTurno = nuevoJugadorId.equals(this.miId);

                if (partesTurno.length > 1) {
                    try {
                        this.mazoFichasRestantes = Integer.parseInt(partesTurno[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("[Modelo] Error al parsear conteo del mazo en TURNO_CAMBIADO");
                    }
                }

                if (partesTurno.length > 2) {
                    String dataContadores = partesTurno[2]; // "J1=14;J2=13;"
                    String[] pares = dataContadores.split(";");
                    for (String par : pares) {
                        String[] kv = par.split("=");
                        if (kv.length == 2) {
                            conteoFichasRivales.put(kv[0], Integer.parseInt(kv[1]));
                        }
                    }
                }

                notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);
                notificarObservadores(TipoEvento.TOMO_FICHA);
                break;

            case "MOVIMIENTO_RECIBIDO":
                System.out.println("[Modelo] Evento 'MOVIMIENTO_RECIBIDO' (Temporal) detectado!");
                try {
                    List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payloadd);
                    if (gruposMovidos != null) {
                        this.actualizarVistaTemporal(gruposMovidos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "ESTADO_FINAL_TABLERO":
                System.out.println("[Modelo] Evento 'ESTADO_FINAL_TABLERO' detectado!");
                try {
                    List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payloadd);
                    if (gruposMovidos != null) {
                        this.actualizarVistaTemporal(gruposMovidos);
                        juego.guardarEstadoTurno();
                        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
                        notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                System.out.println("[Modelo] Evento PropertyChange desconocido: " + evento);
                break;
        }
    }
    public void setDatosRed(iDespachador despachador, String miId, String ipServer, int portServer) {
        this.despachador = despachador;
        this.miId = miId;
        this.ipServidor = ipServer;
        this.puertoServidor = portServer;
    }
    /**
     * Refresca estado del tablero temporalmente antes de validar jugada.
     */
    private void actualizarVistaTemporal(List<GrupoDTO> gruposPropuestos) {
        this.gruposDeTurnoDTO = gruposPropuestos;

        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(this::convertirGrupoDtoAEntidad)
                .collect(Collectors.toList());
        juego.colocarFichasEnTablero(nuevosGrupos);

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

    /**
     * Envía movimiento al servidor y actualiza tablero local.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'colocarFicha' ignorada. No es mi turno.");
            return;
        }
        this.actualizarVistaTemporal(grupos);

        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        String payloadCompleto = payloadBuilder.toString();
        String mensaje = this.miId + ":MOVER:" + payloadCompleto;

        try {
            this.despachador.enviar(ipServidor, puertoServidor, mensaje);
        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reversa jugada si no fue válida y solicita ficha al servidor.
     */
    public void tomarFichaMazo() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'tomarFichaMazo' ignorada. No es mi turno.");
            return;
        }

        juego.revertirCambiosDelTurno();
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR_TOMO_FICHA);

        try {
            String payloadJuegoRevertido = serializarEstadoRevertido();
            String mensajeTomar = this.miId + ":TOMAR_FICHA:" + payloadJuegoRevertido;
            this.despachador.enviar(ipServidor, puertoServidor, mensajeTomar);

        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Finaliza turno: valida jugada, envía estado al servidor y notifica a la
     * vista.
     */
    public void terminarTurno() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'terminarTurno' ignorada. No es mi turno.");
            notificarObservadores(TipoEvento.NO_ES_MI_TURNO);
            return;
        }

        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {

            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
            try {
                String payloadJuego = serializarJuegoFinal();
                int misFichas = juego.getJugadorActual().getManoJugador().getFichasEnMano().size();
                String mensaje = this.miId + ":FINALIZAR_TURNO:" + payloadJuego + "#" + misFichas;
                this.despachador.enviar(ipServidor, puertoServidor, mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            if (!gruposDTOAlInicioDelTurno.equals(gruposDeTurnoDTO)) {

                System.out.println("Jugada invalida");
                System.out.println(gruposDTOAlInicioDelTurno.toString());
                System.out.println(gruposDeTurnoDTO.toString());

                notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
                try {
                    String payloadJuegoRevertido = serializarEstadoRevertido();
                    String mensaje = this.miId + ":MOVER:" + payloadJuegoRevertido;
                    this.despachador.enviar(ipServidor,puertoServidor, mensaje);
                } catch (IOException ex) {
                    Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                notificarObservadores(TipoEvento.TOMAR_FICHA_POR_FINALIZARTURNO);
            }

        }
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    /**
     * Serializa el estado final del turno para enviarlo por red.
     */
    private String serializarJuegoFinal() {
        List<GrupoDTO> grupos = this.gruposDeTurnoDTO;
        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        return payloadBuilder.toString();
    }

    /**
     * Serializa el estado inicial del turno para revertir jugada.
     */
    private String serializarEstadoRevertido() {
        List<GrupoDTO> grupos = this.gruposDTOAlInicioDelTurno;
        StringBuilder payloadBuilder = new StringBuilder();

        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        return payloadBuilder.toString();
    }

    /**
     * Registra observadores para actualizaciones del modelo (patrón Observer).
     */
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    /**
     * Notifica cambios a las vistas con un DTO de actualización.
     */
    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : this.observadores) {

            List<Ficha> manoEntidad = juego.getJugadorActual().getManoJugador().getFichasEnMano();

            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            boolean esSuTurno = this.esMiTurno;
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);

            observer.actualiza(this, dto);
        }
    }

    /**
     * Regresa ficha a la mano (si el movimiento es válido) y actualiza la UI.
     */
    public void regresarFichaAMano(int idFicha) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'regresarFichaAMano' ignorada. No es mi turno.");
            return;
        }

        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) {

            boolean fichaEncontrada = false;
            for (GrupoDTO grupoDTO : this.gruposDeTurnoDTO) {
                Iterator<FichaJuegoDTO> iter = grupoDTO.getFichasGrupo().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getIdFicha() == idFicha) {
                        iter.remove();
                        grupoDTO.setCantidad(grupoDTO.getFichasGrupo().size());
                        fichaEncontrada = true;
                        break;
                    }
                }
                if (fichaEncontrada) {
                    break;
                }
            }

            this.gruposDeTurnoDTO.removeIf(g -> g.getFichasGrupo().isEmpty());

            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
            notificarObservadores(TipoEvento.REPINTAR_MANO);

            try {
                String payloadJuegoActualizado = serializarJuegoFinal();
                String mensaje = this.miId + ":MOVER:" + payloadJuegoActualizado;
                this.despachador.enviar(ipServidor, puertoServidor, mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

    /**
     * Obtiene el estado actual del juego para enviarlo a la vista.
     */
    @Override
    public JuegoDTO getTablero() {
        JuegoDTO dto = new JuegoDTO();
        List<Grupo> gruposDelJuego = juego.getGruposEnTablero();

        if (this.gruposDeTurnoDTO != null && this.gruposDeTurnoDTO.size() == gruposDelJuego.size()) {
            for (int i = 0; i < gruposDelJuego.size(); i++) {
                String tipoValidado = gruposDelJuego.get(i).getTipo();
                this.gruposDeTurnoDTO.get(i).setTipo(tipoValidado);
                this.gruposDeTurnoDTO.get(i).setEsTemporal(gruposDelJuego.get(i).esTemporal());
            }
            dto.setGruposEnTablero(this.gruposDeTurnoDTO);
        } else {
            List<GrupoDTO> gruposDTO = gruposDelJuego.stream()
                    .map(this::convertirGrupoEntidadADto)
                    .collect(Collectors.toList());
            dto.setGruposEnTablero(gruposDTO);
        }

        dto.setFichasMazo(this.mazoFichasRestantes);

        // Obtener nombre del jugador actual (quien tiene el turno)
        String nombreJugadorActual = (juego.getJugadorActual() != null)
                ? juego.getJugadorActual().getNickname()
                : "...";
       dto.setJugadorActual(this.idJugadorEnTurnoGlobal);

        // --- CORRECCIÓN PRINCIPAL: Llenar la lista de jugadores ---
        List<DTO.JugadorDTO> listaJugadoresDTO = new ArrayList<>();

        // Lista de IDs que existen en tu juego (Debería venir del servidor, 
        // pero para que funcione visualmente ahora, usaremos los fijos).
        String[] idsTodosLosJugadores = {"Jugador1", "Jugador2", "Jugador3", "Jugador4"};

        for (String id : idsTodosLosJugadores) {
            DTO.JugadorDTO jugadorDto = new DTO.JugadorDTO();
            jugadorDto.setNombre(id);

            // Determinar si es turno de este ID
            boolean esSuTurno = id.equals(nombreJugadorActual);
            jugadorDto.setEsTurno(esSuTurno);

            // Actualizar fichas:
            // Si soy YO (miId), obtengo el dato real de mi mano.
            if (id.equals(this.miId)) {
                // Lógica existente para MI mano
                int cantidad = juego.getJugadorActual().getManoJugador().getFichasEnMano().size();
                jugadorDto.setFichasRestantes(cantidad);
            } else {
                // Lógica NUEVA para rivales
                // Si el mapa tiene el dato, lo usa. Si no, pone 14 por defecto.
                int cantidadRival = conteoFichasRivales.getOrDefault(id, 14);
                jugadorDto.setFichasRestantes(cantidadRival);
            }

            listaJugadoresDTO.add(jugadorDto);
        }

        // ¡IMPORTANTE! Asignar la lista al DTO
        dto.setJugadores(listaJugadoresDTO);

        return dto;
    }

    /**
     * Convierte DTO de grupo a entidad Grupo.
     */
    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(this::convertirFichaDtoAEntidad)
                .collect(Collectors.toList());
        Grupo grupo = new Grupo(dto.getTipo(), fichas.size(), fichas);
        if (!dto.isEsTemporal()) {
            grupo.setValidado();
        }
        return grupo;
    }

    /**
     * Convierte entidad Grupo a DTO.
     */
    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(),
                f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0, g.esTemporal());
    }

    /**
     * Convierte DTO de ficha a entidad Ficha.
     */
    private Ficha convertirFichaDtoAEntidad(FichaJuegoDTO fDto) {
        if (fDto == null) {
            return null;
        }
        return new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(),
                fDto.getColor(), fDto.isComodin());
    }

    /**
     * Convierte payload en una lista de fichas DTO.
     */
    private List<FichaJuegoDTO> deserializarMano(String payload) {
        List<FichaJuegoDTO> mano = new ArrayList<>();
        if (payload == null || payload.isEmpty()) {
            return mano;
        }

        String[] fichasData = payload.split("\\|");

        for (String fichaData : fichasData) {
            if (fichaData != null && !fichaData.isEmpty()) {
                FichaJuegoDTO ficha = FichaJuegoDTO.deserializar(fichaData);
                if (ficha != null) {
                    mano.add(ficha);
                }
            }
        }
        return mano;
    }

    public void enviarComandoIniciarPartida() {
        try {
            String mensaje = this.miId + ":INICIAR_PARTIDA:";
            System.out.println("[Modelo] Enviando comando INICIAR_PARTIDA al servidor.");
            this.despachador.enviar(ipServidor, puertoServidor, mensaje);
        } catch (IOException ex) {
            System.err.println("[Modelo] Error al enviar comando INICIAR_PARTIDA: " + ex.getMessage());
        }
    }

    /**
     * Envía comando al servidor para iniciar partida.
     */
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }
    public void iniciarPantalla(){
        notificarObservadores(TipoEvento.INICIAR_PANTALLA);
    }
    /**
     * Establece la identificación única (ID) del cliente/jugador actual, usada
     * para construir los mensajes enviados al servidor.
     */
    public void setMiId(String miId) {
        this.miId = miId;
    }
}
