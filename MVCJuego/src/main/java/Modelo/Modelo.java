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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Esta clase representa el modelo en MVC.
 *
 * @author Benja (Refactorizado por IA)
 */
public class Modelo implements IModelo, PropertyChangeListener {

    private List<Observador> observadores;
    private final IJuegoRummy juego;
    private List<GrupoDTO> gruposDeTurnoDTO;
    private List<GrupoDTO> gruposDTOAlInicioDelTurno;
    private boolean esMiTurno;
    private iDespachador despachador;
    private String miId;
    
    private int mazoFichasRestantes = 0;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada(); // <-- Esta fachada ya no tiene la lista
        this.gruposDeTurnoDTO = new ArrayList<>();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>();
        this.esMiTurno = false;
    }

    public void iniciarJuego() {
        juego.iniciarPartida();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

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
                } catch (Exception e) { e.printStackTrace(); }
                break;
                
            case "TURNO_CAMBIADO":
                System.out.println("[Modelo] Evento 'TURNO_CAMBIADO' detectado! Payload: " + payloadd);
                String[] partesTurno = payloadd.split(":");
                String nuevoJugadorId = partesTurno[0];
                this.esMiTurno = nuevoJugadorId.equals(this.miId);
                
                if (partesTurno.length > 1) {
                    try {
                        this.mazoFichasRestantes = Integer.parseInt(partesTurno[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("[Modelo] Error al parsear conteo del mazo en TURNO_CAMBIADO");
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
                } catch (Exception e) { e.printStackTrace(); }
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
                } catch (Exception e) { e.printStackTrace(); }
                break;
                
            default:
                 System.out.println("[Modelo] Evento PropertyChange desconocido: " + evento);
                 break;
        }
    }
    
    private void actualizarVistaTemporal(List<GrupoDTO> gruposPropuestos) {
        this.gruposDeTurnoDTO = gruposPropuestos;

        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(this::convertirGrupoDtoAEntidad)
                .collect(Collectors.toList());
        juego.colocarFichasEnTablero(nuevosGrupos);

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

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
            this.despachador.enviar(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tomarFichaMazo() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'tomarFichaMazo' ignorada. No es mi turno.");
            return;
        }

        juego.revertirCambiosDelTurno();
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        
        try {
            String payloadJuegoRevertido = serializarEstadoRevertido();
            String mensajeTomar = this.miId + ":TOMAR_FICHA:" + payloadJuegoRevertido;
            this.despachador.enviar(mensajeTomar);

        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void terminarTurno() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'terminarTurno' ignorada. No es mi turno.");
            return;
        }

        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
            try {
                String payloadJuego = serializarJuegoFinal();
                String mensaje = this.miId + ":FINALIZAR_TURNO:" + payloadJuego;
                this.despachador.enviar(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
            try {
                String payloadJuegoRevertido = serializarEstadoRevertido();
                String mensaje = this.miId + ":MOVER:" + payloadJuegoRevertido;
                this.despachador.enviar(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

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

    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : this.observadores) {
            
            // --- INICIO DE CORRECCIÓN ---
            // Ya no usamos un índice. Obtenemos la mano del jugador local
            // directamente de la fachada.
            List<Ficha> manoEntidad = juego.getJugadorActual().getManoJugador().getFichasEnMano();
            // --- FIN DE CORRECCIÓN ---

            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            boolean esSuTurno = this.esMiTurno;
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);

            observer.actualiza(this, dto);
        }
    }

    public void regresarFichaAMano(int idFicha) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'regresarFichaAMano' ignorada. No es mi turno.");
            return;
        }
        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) {
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

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
        
        if(juego.getJugadorActual() != null) {
            dto.setJugadorActual(juego.getJugadorActual().getNickname());
        } else {
            dto.setJugadorActual("...");
        }
        return dto;
    }

    // --- MÉTODOS DE AYUDA PARA CONVERSIÓN ---

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

    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(),
                f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0, g.esTemporal());
    }
    
    private Ficha convertirFichaDtoAEntidad(FichaJuegoDTO fDto) {
        if (fDto == null) return null;
        return new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(),
                         fDto.getColor(), fDto.isComodin());
    }
    
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

    // --- MÉTODOS DE AYUDA PARA RED ---

    public void enviarComandoIniciarPartida() {
        try {
            String mensaje = this.miId + ":INICIAR_PARTIDA:";
            System.out.println("[Modelo] Enviando comando INICIAR_PARTIDA al servidor.");
            this.despachador.enviar(mensaje);
        } catch (IOException ex) {
            System.err.println("[Modelo] Error al enviar comando INICIAR_PARTIDA: " + ex.getMessage());
        }
    }

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }
}