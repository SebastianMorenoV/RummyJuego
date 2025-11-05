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
    private boolean esMiTurno;
    private iDespachador despachador;
    private String miId;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();
        this.esMiTurno = false;
    }

    /**
     * Inicia la partida. Se establece al primer jugador como el actual, se
     * solicita a la fachada inicializar el juego (agregar jugadores, preparar
     * el mazo y repartir fichas) y se notifica a los observadores para que
     * dibujen las fichas iniciales en la mano.
     */
    public void iniciarJuego() {
        juego.iniciarPartida();
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
            System.out.println("[Modelo] Evento 'MOVIMIENTO_RECIBIDO' detectado!");

            try {
                String payload = (String) evt.getNewValue();
                List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payload);

                if (gruposMovidos != null && !gruposMovidos.isEmpty()) {
                    System.out.println("Se intento colocar ficha (remoto)");

                    this.actualizarVistaTemporal(gruposMovidos);

                } else {
                    System.err.println("[Modelo] Error: No se pudo deserializar el payload: " + payload);
                }

            } catch (Exception e) {
                System.err.println("[Modelo] Error al procesar evento: " + e.getMessage());
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
        // 1. Llama al método que SÓLO pinta localmente
        this.actualizarVistaTemporal(grupos);

        // 2. Serializa y envía el mensaje a la red
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

    /**
     * REFACTORIZADO: 1. Ejecuta la lógica local. 2. Notifica a la UI local. 3.
     * ENVÍA el comando "TOMAR_FICHA" al servidor para que avance el turno.
     */
    public void tomarFichaMazo() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'tomarFichaMazo' ignorada. No es mi turno.");
            return;
        }
        // 1. Lógica local (exactamente como la tenías)
        juego.revertirCambiosDelTurno();
        juego.jugadorTomaFichaDelMazo();

        // 2. Notificaciones locales (exactamente como las tenías)
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
        notificarObservadores(TipoEvento.TOMO_FICHA);

        // 3. ¡Lógica de red!
        // Le dice al servidor que avance el turno.
        try {
            String mensaje = this.miId + ":TOMAR_FICHA:"; // Nuevo comando
            this.despachador.enviar(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * REFACTORIZADO: 1. Valida la lógica local. 2. Si es válida, notifica a la
     * UI local y ENVÍA el comando "FINALIZAR_TURNO". 3. Si es inválida, solo
     * notifica a la UI local.
     */
    public void terminarTurno() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'terminarTurno' ignorada. No es mi turno.");
            return;
        }
        // 1. Lógica local (como la tenías)
        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {
            // 2. Notificación local
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);

            // 3. ¡Lógica de red!
            // Le dice al servidor que la jugada fue válida y que avance el turno.
            try {
                // El payload podría ser el estado final del tablero
                String payloadJuego = serializarJuegoFinal(); // (Necesitarás este método)
                String mensaje = this.miId + ":FINALIZAR_TURNO:" + payloadJuego;
                this.despachador.enviar(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(Modelo.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            // 4. Lógica local de jugada inválida (como la tenías)
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }

        // Esto se queda, para repintar la mano si la jugada fue inválida
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    /**
     * Método de ayuda para serializar el estado final del tablero en un
     * `FINALIZAR_TURNO`. Reutiliza la lógica de `colocarFicha`.
     */
    private String serializarJuegoFinal() {
        List<GrupoDTO> grupos = this.gruposDeTurnoDTO; // Asume que esto tiene el estado final
        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$"); // Delimitador ENTRE grupos
            }
        }
        return payloadBuilder.toString();
    }

    // Métodos del Patron Observador
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    /**
     * Notifica a los observadores sobre un cambio en el estado del juego.
     * envian una actualizacion personalizada segun el tipo de evento.
     *
     * @param tipoEvento el evento que ocurrio para indicar a quien le llegara
     * la notificacion.
     */
    public void notificarObservadores(TipoEvento tipoEvento) {

        for (Observador observer : this.observadores) {
            int indiceJugador = observadores.indexOf(observer);
            List<Ficha> manoEntidad = juego.getManoDeJugador(indiceJugador);
            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            // --- LÓGICA SIMPLIFICADA ---
            // Ya no compara observadores, solo usa el booleano.
            boolean esSuTurno = this.esMiTurno;
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);

            /*
             * Decide a quien enviar la notificacion basado en el tipo de evento.
             */
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

    /**
     * Regresa una ficha a la mano según su id. Se intenta devolver la ficha y,
     * si era temporal, se actualiza el tablero y la mano. Si la ficha ya
     * formaba parte de un grupo válido, se revierte el movimiento visual
     * notificando a los observadores.
     *
     * @param idFicha para indicar que ficha se quiere regresar
     */
    public void regresarFichaAMano(int idFicha) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'regresarFichaAMano' ignorada. No es mi turno.");
            return;
        }
        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) { // EXITO: La ficha era temporal y volvio a la mano.

            // Notificamos a la vista para que actualice el tablero y la mano.
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);

            notificarObservadores(TipoEvento.REPINTAR_MANO);

        } else { // FALLO: La ficha pertenece a un grupo ya validado

            // Notificamos a la vista que la jugada es invalida y debe revertir
            // El movimiento visual de la ficha
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

    // Getters que convierten Entidades a DTOs para la Vista
    @Override
    public JuegoDTO getTablero() {
        JuegoDTO dto = new JuegoDTO();
        List<Grupo> gruposDelJuego = juego.getGruposEnTablero();

        // Si los DTOs temporales coinciden con los grupos del juego, los usamos
        // pues contienen la informacion de posicion que necesitamos.
        if (this.gruposDeTurnoDTO != null && this.gruposDeTurnoDTO.size() == gruposDelJuego.size()) {
            for (int i = 0; i < gruposDelJuego.size(); i++) {

                // Actualizamos el 'tipo' en nuestro DTO con el resultado de la validación
                String tipoValidado = gruposDelJuego.get(i).getTipo();

                this.gruposDeTurnoDTO.get(i).setTipo(tipoValidado);
            }
            dto.setGruposEnTablero(this.gruposDeTurnoDTO);
        } else {

            // Si no coinciden, creamos DTOs que no tengan posicion en si.
            List<GrupoDTO> gruposDTO = gruposDelJuego.stream()
                    .map(this::convertirGrupoEntidadADto)
                    .collect(Collectors.toList());
            dto.setGruposEnTablero(gruposDTO);
        }

        dto.setFichasMazo(juego.getCantidadFichasMazo());
        dto.setJugadorActual(juego.getJugadorActual().getNickname());
        return dto;
    }

    // Métodos de ayuda para conversión DTO-Entidad
    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {

        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(fDto -> new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(),
                fDto.getColor(), fDto.isComodin()))
                .collect(Collectors.toList());
        return new Grupo("Temporal", fichas.size(), fichas);
    }

    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(),
                f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());

        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0);
    }

    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

}
