package Modelo;

import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Fachada.IJuegoRummy;
import Fachada.JuegoRummyFachada;
import Vista.Observador;
import Vista.TipoEvento;
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

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
    }

    // --- Métodos que delegan la lógica del juego a la Fachada ---
    public void iniciarJuego() {
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
    }

    public void colocarFicha(List<GrupoDTO> gruposPropuestos) {
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
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
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
        List<GrupoDTO> gruposDTO = juego.getGruposEnTablero().stream()
                .map(this::convertirGrupoEntidadADto)
                .collect(Collectors.toList());
        dto.setGruposEnTablero(gruposDTO);
        dto.setFichasMazo(juego.getCantidadFichasMazo());
        return dto;
    }

    @Override
    public List<FichaJuegoDTO> getMano() {
        return juego.getManoJugador().stream()
                .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
    }

    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(fDto -> new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(), fDto.getColor(), fDto.isComodin(), fDto.getFila(), fDto.getColumna()))
                .collect(Collectors.toList());
        // Asegúrate de tener un constructor en la Entidad Grupo que acepte fila y columna
        return new Grupo("Temporal", fichas.size(), fichas, dto.getFila(), dto.getColumna());
    }

    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin(), f.getFila(), f.getColumna()))
                .collect(Collectors.toList());
        // Usa el constructor del DTO que acepta fila y columna
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, g.getFila(), g.getColumna());
    }

    // --- Métodos del Patrón Observador (sin cambios) ---
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : new ArrayList<>(this.observadores)) {
            observer.actualiza(this, tipoEvento);
        }
    }
    
    public void regresarFichaAMano(int idFicha) {
        // Se delega la logica y la validacion a la fachada del juego
        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);
        
        if (fueRegresadaExitosamente) {// exito: La ficha era temporal y volvió a la mano.
            // notificamos a la vista para que actualice el tablero y la mano.
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
        } else { // fallo: La ficha pertenece a un grupo ya validado
            // notificamos a la vista que la jugada es invalida y debe revertir
            // el movimiento visual de la ficha
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }
}
