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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta clase representa el modelo en MVC. Su responsabilidad es conectar la
 * lógica del juego (a través de la fachada) con la Vista (a través del patrón
 * Observer) y manejar la conversión de datos entre Entidades y DTOs.
 */
public class Modelo implements IModelo {

    private List<Observador> observadores;
    private final IJuegoRummy juego;
    private List<GrupoDTO> gruposDeTurnoDTO;
    private Observador enTurno;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();
    }

    public void iniciarJuego() {
        enTurno = observadores.get(0);

        juego.iniciarPartida();

        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    public void tomarFichaMazo() {
        // revertir jugada temporal del tablero
        juego.revertirCambiosDelTurno();
        juego.jugadorTomaFichaDelMazo();

        // revierte el tablero
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
        notificarObservadores(TipoEvento.TOMO_FICHA);
        // 2. Llama a la Fachada para que cambie el contador de jugador interno
        juego.siguienteTurno();

        // 3. Actualiza la variable 'enTurno' del Modelo para que apunte al nuevo jugador
        int indiceActual = observadores.indexOf(enTurno);
        enTurno = observadores.get((indiceActual + 1) % observadores.size());

        // 4. Envía UNA SOLA notificación que lo actualiza todo.
        // Usamos CAMBIO_DE_TURNO como el evento principal que refresca toda la vista.
        notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);
    }

    public void colocarFicha(List<GrupoDTO> gruposPropuestos) {
        this.gruposDeTurnoDTO = gruposPropuestos;

        // 1. El Modelo se encarga de convertir DTOs a Entidades
        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(this::convertirGrupoDtoAEntidad)
                .collect(Collectors.toList());

        // 2. Llama a la fachada con las entidades
        juego.colocarFichasEnTablero(nuevosGrupos);

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

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
        // La posición real se determinará en la Vista. Pasamos 0,0 como placeholders.
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0);
    }

    // --- Métodos del Patrón Observador (sin cambios) ---
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        // Itera sobre todos los observadores para enviarles una actualización personalizada.
        for (Observador observer : this.observadores) {
            // 1. Obtenemos el índice del observador para saber qué jugador es.
            int indiceJugador = observadores.indexOf(observer);

            // 2. Pedimos a la fachada la mano de ESE jugador específico.
            List<Ficha> manoEntidad = juego.getManoDeJugador(indiceJugador);

            // 3. La convertimos a una lista de DTOs de fichas.
            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            // 4. Creamos el DTO con la mano específica de este jugador.
            boolean esSuTurno = observer.equals(enTurno);
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);
            /*
             * Decide a quién enviar la notificación basado en el tipo de evento.
             */
            switch (tipoEvento) {
                // --- CASOS GLOBALES: Notificar a TODOS ---
                case INCIALIZAR_FICHAS:
                case ACTUALIZAR_TABLERO_TEMPORAL:
                case JUGADA_VALIDA_FINALIZADA:
                case JUGADA_INVALIDA_REVERTIR:
                case CAMBIO_DE_TURNO:
                case TOMO_FICHA:
                    observer.actualiza(this, dto);
                    break;

                // --- CASOS ESPECÍFICOS: Notificar solo al jugador EN TURNO ---
                case REPINTAR_MANO:
                    if (esSuTurno) {
                        observer.actualiza(this, dto);
                    }
                    break;

                // Si hay un evento no contemplado, no se notifica para evitar errores.
                default:
                    break;
            }
        }
    }

    public void regresarFichaAMano(int idFicha) {
        // Se delega la logica y la validacion a la fachada del juego
        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) {// exito: La ficha era temporal y volvió a la mano.

            // notificamos a la vista para que actualice el tablero y la mano.
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
        } else { // fallo: La ficha pertenece a un grupo ya validado
            // notificamos a la vista que la jugada es invalida y debe revertir
            // el movimiento visual de la ficha
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

}
