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
    private Observador enTurno;
    private iDespachador despachador;
    private String miId;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();
    }

    /**
     * Inicia la partida. Se establece al primer jugador como el actual, se
     * solicita a la fachada inicializar el juego (agregar jugadores, preparar
     * el mazo y repartir fichas) y se notifica a los observadores para que
     * dibujen las fichas iniciales en la mano.
     */
    public void iniciarJuego() {
        enTurno = observadores.get(0);
        juego.iniciarPartida();
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    /**
     * Reacciona a eventos que le llegan.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();

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
     * Método para que el jugador tome una ficha del mazo cuando no puede
     * realizar una jugada válida. Primero revierte cualquier movimiento
     * temporal del turno para regresar el tablero a su estado inicial y
     * notificar a los observadores. Luego toma la ficha del mazo, avanza al
     * siguiente jugador actualizando la variable 'enTurno' y finalmente
     * notifica el cambio de turno para actualizar la vista.
     */
    public void tomarFichaMazo() {
        // Revertir jugada temporal del tablero
        juego.revertirCambiosDelTurno();
        juego.jugadorTomaFichaDelMazo();

        // Revierte el tablero
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
        notificarObservadores(TipoEvento.TOMO_FICHA);
        // Llama a la Fachada para que cambie el contador de jugador interno
        juego.siguienteTurno();

        // Actualiza la variable 'enTurno' del Modelo para que apunte al nuevo jugador
        int indiceActual = observadores.indexOf(enTurno);
        enTurno = observadores.get((indiceActual + 1) % observadores.size());

        notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);
    }

    /**
     * Finaliza el turno del jugador. Se valida la jugada desde la fachada. Si
     * es válida, se notifica, se cambia al siguiente jugador y se indica el
     * cambio de turno. Si es inválida, se revierten los movimientos realizados.
     * Finalmente, se repinta la mano del jugador.
     */
    public void terminarTurno() {
        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {

            // Lógica para cambiar de turno
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);

            int indiceActual = observadores.indexOf(enTurno);

            enTurno = observadores.get((indiceActual + 1) % observadores.size());

            notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);

            if (juego.haGanadoElJugador()) {
                // notificarObservadores(TipoEvento.JUEGO_TERMINADO);
            }
        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
        notificarObservadores(TipoEvento.REPINTAR_MANO);
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

        // Itera sobre todos los observadores para enviarles una actualización personalizada.
        for (Observador observer : this.observadores) {

            // Obtenemos el indice del observador para saber que jugador es.
            int indiceJugador = observadores.indexOf(observer);

            // Pedimos a la fachada la mano de ESE jugador especifico
            List<Ficha> manoEntidad = juego.getManoDeJugador(indiceJugador);

            // La convertimos a una lista de DTOs de fichas
            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            // Creamos el DTO con la mano especifica de este jugador.
            boolean esSuTurno = observer.equals(enTurno);
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
