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
 * Esta clase representa el modelo en MVC. Su responsabilidad es conectar la
 * lógica del juego (a través de la fachada) con la Vista (a través del patrón
 * Observer) y manejar la conversión de datos entre Entidades y DTOs.
 *
 * @author Benja
 */
public class Modelo implements IModelo, PropertyChangeListener {

    private List<Observador> observadores;
    private final IJuegoRummy juego;
    private List<GrupoDTO> gruposDeTurnoDTO;

    // --- ¡NUEVA VARIABLE DE MEMORIA! ---
    // Esta guardará los DTOs CON posiciones al inicio del turno.
    private List<GrupoDTO> gruposDTOAlInicioDelTurno;

    private boolean esMiTurno;
    private iDespachador despachador;
    private String miId;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();

        // --- INICIALIZAR LA NUEVA LISTA ---
        this.gruposDTOAlInicioDelTurno = new ArrayList<>();

        this.esMiTurno = false;
    }

    /**
     * Inicia la partida.
     */
    public void iniciarJuego() {
        juego.iniciarPartida(); // Esto llama a guardarEstadoTurno() en la fachada

        // --- ¡AÑADIDO! ---
        // Guardamos el estado DTO inicial (tablero vacío)
        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);

        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    /**
     * Reacciona a eventos que le llegan.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payloadd = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        if (evento.equals("MOVIMIENTO_RECIBIDO")) {
            // Esto es SÓLO para movimientos temporales de otros jugadores.
            System.out.println("[Modelo] Evento 'MOVIMIENTO_RECIBIDO' (Temporal) detectado!");
            try {
                String payload = (String) evt.getNewValue();
                List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payload);

                if (gruposMovidos != null) {
                    System.out.println("Se intento colocar ficha (remoto-temporal)");
                    // Solo actualiza la vista, NO guarda el estado.
                    this.actualizarVistaTemporal(gruposMovidos);
                } else {
                    System.err.println("[Modelo] Error: No se pudo deserializar el payload (temporal): " + payload);
                }
            } catch (Exception e) {
                System.err.println("[Modelo] Error al procesar evento (temporal): " + e.getMessage());
            }
        }

        if (evento.equals("ESTADO_FINAL_TABLERO")) {
            System.out.println("[Modelo] Evento 'ESTADO_FINAL_TABLERO' detectado!");
            try {
                String payload = (String) evt.getNewValue();
                List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payload);

                if (gruposMovidos != null) {
                    System.out.println("Se recibió el estado final del tablero.");

                    // 1. Actualiza la vista/fachada con el estado final.
                    //    Esto también actualiza 'this.gruposDeTurnoDTO'
                    this.actualizarVistaTemporal(gruposMovidos);

                    // 2. ¡GUARDA ESTE ESTADO!
                    System.out.println("[Modelo] Guardando estado de turno (Final).");
                    juego.guardarEstadoTurno(); // Guarda la lógica

                    // --- ¡CAMBIO IMPORTANTE! ---
                    // Guarda una copia de los DTOs (con posiciones)
                    this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
                    // --- FIN DEL CAMBIO ---

                    // 3. Notifica a la Vista que guarde este estado visual
                    notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);

                } else {
                    System.err.println("[Modelo] Error: No se pudo deserializar el payload (final): " + payload);
                }
            } catch (Exception e) {
                System.err.println("[Modelo] Error al procesar evento (final): " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (evento.equals("TURNO_CAMBIADO")) {
            System.out.println("[Modelo] Evento 'TURNO_CAMBIADO' detectado! Nuevo turno: " + payloadd);
            String nuevoJugadorId = payloadd;
            this.esMiTurno = nuevoJugadorId.equals(this.miId);
            notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);
        }
    }

    private void actualizarVistaTemporal(List<GrupoDTO> gruposPropuestos) {
        this.gruposDeTurnoDTO = gruposPropuestos; // <-- ¡Esto es importante!

        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(this::convertirGrupoDtoAEntidad)
                .collect(Collectors.toList());
        juego.colocarFichasEnTablero(nuevosGrupos);

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

    // (colocarFicha no cambia)
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
                payloadBuilder.append("$"); // Delimitador ENTRE grupos
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

    // --- ***** MÉTODO MODIFICADO ***** ---
    public void tomarFichaMazo() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'tomarFichaMazo' ignorada. No es mi turno.");
            return;
        }

        // 1. Revertir la lógica y la UI local PRIMERO
        juego.revertirCambiosDelTurno();
        juego.jugadorTomaFichaDelMazo();

        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
        notificarObservadores(TipoEvento.TOMO_FICHA);

        try {
            // 2. Obtener el estado REVERTIDO
            String payloadJuegoRevertido = serializarEstadoRevertido();

            // 3. ENVIAR UN :MOVER: con el estado revertido.
            //    Esto actualiza el 'ultimoTableroSerializado' en el servidor
            //    y notifica a los otros clientes del estado revertido.
            String mensajeRevertir = this.miId + ":MOVER:" + payloadJuegoRevertido;
            this.despachador.enviar(mensajeRevertir);

            // 4. ENVIAR :TOMAR_FICHA: para avanzar el turno.
            //    El servidor ahora usará el payload que acabamos de enviar.
            String mensajeTomar = this.miId + ":TOMAR_FICHA:" + payloadJuegoRevertido;
            this.despachador.enviar(mensajeTomar);

        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // --- ***** FIN DE LA MODIFICACIÓN ***** ---

    public void terminarTurno() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'terminarTurno' ignorada. No es mi turno.");
            return;
        }

        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
            try {
                // ¡MODIFICADO! Llama al serializador correcto
                String payloadJuego = serializarJuegoFinal();
                String mensaje = this.miId + ":FINALIZAR_TURNO:" + payloadJuego;
                this.despachador.enviar(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);

            try {
                // ¡MODIFICADO! Llama al serializador correcto
                String payloadJuegoRevertido = serializarEstadoRevertido();

                String mensaje = this.miId + ":MOVER:" + payloadJuegoRevertido;
                this.despachador.enviar(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    /**
     * Serializa el estado ACTUAL del tablero (los DTOs de la UI).
     */
    private String serializarJuegoFinal() {
        // Usa la lista de DTOs actual (la del movimiento en curso)
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
     * Serializa el estado del tablero guardado al INICIO del turno. (Usado para
     * reversiones).
     */
    private String serializarEstadoRevertido() {
        // --- ¡CAMBIO CLAVE! ---
        // Usa la lista de DTOs guardada, que CONTIENE las posiciones.
        List<GrupoDTO> grupos = this.gruposDTOAlInicioDelTurno;
        // --- FIN DEL CAMBIO ---

        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        return payloadBuilder.toString();
    }

    // (agregarObservador, notificarObservadores, regresarFichaAMano no cambian)
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : this.observadores) {
            int indiceJugador = observadores.indexOf(observer);
            List<Ficha> manoEntidad = juego.getManoDeJugador(indiceJugador);
            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            boolean esSuTurno = this.esMiTurno;
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);

            switch (tipoEvento) {
                case INCIALIZAR_FICHAS:
                case ACTUALIZAR_TABLERO_TEMPORAL:
                case JUGADA_VALIDA_FINALIZADA:
                case JUGADA_INVALIDA_REVERTIR:
                case CAMBIO_DE_TURNO:
                case TOMO_FICHA:
                    observer.actualiza(this, dto);
                    break;
                case REPINTAR_MANO:
                    if (esSuTurno) {
                        observer.actualiza(this, dto);
                    }
                    break;
                default:
                    break;
            }
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

        // --- ¡LÓGICA ACTUALIZADA! ---
        // Sincroniza la lista de DTOs (con posiciones) con la 
        // lista de entidades (con estado lógico 'tipo' y 'esTemporal')
        if (this.gruposDeTurnoDTO != null && this.gruposDeTurnoDTO.size() == gruposDelJuego.size()) {
            for (int i = 0; i < gruposDelJuego.size(); i++) {
                // Actualiza el 'tipo' en nuestro DTO con el resultado de la validación
                String tipoValidado = gruposDelJuego.get(i).getTipo();
                this.gruposDeTurnoDTO.get(i).setTipo(tipoValidado);

                // Sincroniza el estado 'temporal' del DTO con el de la entidad
                this.gruposDeTurnoDTO.get(i).setEsTemporal(gruposDelJuego.get(i).esTemporal());
            }
            dto.setGruposEnTablero(this.gruposDeTurnoDTO);
        } else {
            // Si no coinciden, crea DTOs desde la lógica (perderá posiciones,
            // pero esto solo debería pasar en una desincronización)
            List<GrupoDTO> gruposDTO = gruposDelJuego.stream()
                    .map(this::convertirGrupoEntidadADto)
                    .collect(Collectors.toList());
            dto.setGruposEnTablero(gruposDTO);
        }
        // --- FIN DE LÓGICA ACTUALIZADA ---

        dto.setFichasMazo(juego.getCantidadFichasMazo());
        dto.setJugadorActual(juego.getJugadorActual().getNickname());
        return dto;
    }

    // --- MÉTODOS DE AYUDA (ACTUALIZADOS) ---
    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(fDto -> new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(),
                fDto.getColor(), fDto.isComodin()))
                .collect(Collectors.toList());

        Grupo grupo = new Grupo(dto.getTipo(), fichas.size(), fichas);

        // ¡Importante! Si el DTO dice que no es temporal, lo marcamos
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

        // Se usa 0,0 como placeholder Y se pasa el estado 'esTemporal'
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0, g.esTemporal());
    }

    // (setDespachador y setMiId)
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }
}
