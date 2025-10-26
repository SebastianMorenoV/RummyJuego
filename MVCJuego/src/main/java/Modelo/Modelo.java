package Modelo;

import DTO.ActualizacionDTO;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Fachada.IJuegoRummy;
import Fachada.JuegoRummyFachada;
import Vista.Observador;
import Vista.TipoEvento;
import static Vista.TipoEvento.TOMO_FICHA;
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
     * Metodo para iniciar el juego. Indicamos que el primero jugador del turno
     * sera el 0 de la lista de observadores, despues le hablamos a la fachada
     * para iniciar la partida que basicamente se añaden los jugadores, se
     * añaden al mazo las fichas y se reparten de este mismo mazo las fichas a
     * los jugadores, despues notificamos al observer que el tipo de evento es
     * inicializar_Fichas para que se pinten en la mano
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

        // (Opcional, pero basado en tu código viejo, esto también debería ir aquí)
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
     * Metodo para tomar una ficha del mazo. Este metodo nos sirve para cuando
     * un jugador no puede hacer alguna jugada o movimiento y tiene que tomar
     * una ficha del mazo, comenzando con revertir los cambios del turno por si
     * movio algo o coloco una ficha generando un grupo invalido para poder
     * notificar a los observadores y regresar todo tipo de movimiento a como
     * estaba en el inicio de su turno. Despues de esto el metodo llama a la
     * fachada para indicar que es turno del siguiente jugador, despues
     * actualiza a la variable "enTurno" para que sea el siguiente jugador el
     * que este en turno, por ultimo se notifica al observador del TipoEvento de
     * cambio de turno para indicar en el titulo de la vista que esta en turno o
     * en espera
     */
    public void tomarFichaMazo() {
        // revertir jugada temporal del tablero
        juego.revertirCambiosDelTurno();
        juego.jugadorTomaFichaDelMazo();

        // revierte el tablero
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
     * Metodo en el que se termina un turno de un jugador. Primero obtenemos
     * desde la fachada si la jugada fue valida y la guardamos en un booleano.
     * si la jugada fue valida se repinta el tablero con el tipoEvento de
     * jugada_valida_finalizar y se le pasa el turno al siguiente jugador . Si
     * la jugada fue invalida se revierten los cambios hechos en el turno
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

    // --- Métodos del Patron Observador ---
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
     * Metodo para regresar la ficha a la mano. primero se calcula si la ficha
     * fue regresada exitosamente con "boolean fueRegresadaExitosamente", si
     * este booleano es true esto indica que la ficha era temporal y regreso a
     * la mano para despues notificar a los observadores, actualizar el tablero
     * y repintar la mano. si el booleano es false esto nos indica que la ficha
     * ya pertenece a un grupo validado dentro del tablero, por lo que solo se
     * revierten los cambios y la ficha regresa a su origen
     *
     * @param idFicha para indicar que ficha se quiere regresar
     */
    public void regresarFichaAMano(int idFicha) {
        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) {// exito: La ficha era temporal y volvio a la mano.

            // notificamos a la vista para que actualice el tablero y la mano.
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
        } else { // fallo: La ficha pertenece a un grupo ya validado
            // notificamos a la vista que la jugada es invalida y debe revertir
            // el movimiento visual de la ficha
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

    // --- Getters que convierten Entidades a DTOs para la Vista ---
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

    // --- Métodos de ayuda para conversión DTO <-> Entidad ---
    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(fDto -> new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(), fDto.getColor(), fDto.isComodin()))
                .collect(Collectors.toList());
        return new Grupo("Temporal", fichas.size(), fichas);
    }

    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());

        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0);
    }

    /*SETTERS*/
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

}
