package Modelo;

import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;
import Entidades.Tablero;
import Entidades.Mano;
import Vista.Observador;
import Vista.TipoEvento;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Modelo implements IModelo {

    private List<Observador> observadores;
    private Tablero tablero;
    private Jugador jugador;

    // Atributos para guardar el estado del turno y del juego
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;
    private boolean primerMovimientoRealizado = false;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.tablero = new Tablero();
        this.jugador = new Jugador();
    }

    public void iniciarJuego() {
        this.jugador = new Jugador("Sebas", "B1", new Mano());
        tablero.crearMazoCompleto();
        tablero.repartirMano(jugador, 14);
        iniciarTurno();
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    public void iniciarTurno() {
        this.tableroAlInicioDelTurno = this.tablero.copiaProfunda();
        this.manoAlInicioDelTurno = this.jugador.getManoJugador().copiaProfunda();
        System.out.println("[MODELO] Estado de inicio de turno guardado.");
    }

    public void terminarTurno() {
        // El Modelo solo pregunta a la entidad si la jugada es válida,
        // pasándole el contexto necesario (si es el primer movimiento).
        boolean esPrimerMovimiento = !this.primerMovimientoRealizado;

        if (tablero.esJugadaValida(esPrimerMovimiento)) {
            confirmarTurno();
        } else {
            revertirTurno();
        }
    }

    private void confirmarTurno() {
        System.out.println("[MODELO] >> ÉXITO: Jugada válida. Confirmando cambios.");
        if (!primerMovimientoRealizado) {
            primerMovimientoRealizado = true;
        }

        // Orquesta las actualizaciones en las entidades
        List<Integer> idsEnTablero = this.tablero.getTodosLosIdsDeFichas();
        this.jugador.getManoJugador().removerFichasJugadas(idsEnTablero);

        // Pregunta a la entidad si hay un ganador
        if (this.jugador.haGanado()) {
            System.out.println("[MODELO] ¡JUEGO TERMINADO! El jugador ha ganado.");
            // notificarObservadores(TipoEvento.JUEGO_TERMINADO);
        }

        notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
        notificarObservadores(TipoEvento.REPINTAR_MANO);

        iniciarTurno();
    }

    private void revertirTurno() {
        System.out.println("[MODELO] >> FALLO: Jugada inválida. Reviertiendo.");
        this.tablero = this.tableroAlInicioDelTurno;
        this.jugador.setManoJugador(this.manoAlInicioDelTurno);

        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    public void colocarFicha(List<GrupoDTO> gruposPropuestos) {
        // 1. Mapea DTOs a Entidades
        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(dto -> {
                    List<Ficha> fichas = dto.getFichasGrupo().stream()
                            .map(fDto -> new Ficha(fDto.getIdFicha(), fDto.getNumeroFicha(), fDto.getColor(), fDto.isComodin()))
                            .collect(Collectors.toList());
                    return new Grupo("Temporal", fichas.size(), fichas);
                })
                .collect(Collectors.toList());

        // 2. Le pide a cada entidad que se valide a sí misma
        nuevosGrupos.forEach(Grupo::validarYEstablecerTipo);

        // 3. Actualiza el estado y notifica
        this.tablero.setFichasEnTablero(nuevosGrupos);
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }


    // ... (resto del código de la clase Modelo)
    public void tomarFichaMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        if (fichaTomada != null) {
            jugador.agregarFichaAJugador(fichaTomada);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.TOMO_FICHA);
        }
    }

    // --- MÉTODOS OBSERVADOR ---
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        // Se itera sobre una copia para evitar ConcurrentModificationException
        // si un observador intentara agregarse o removerse durante la notificación.
        for (Observador observer : new ArrayList<>(this.observadores)) {
            observer.actualiza(this, tipoEvento);
        }
    }

    // --- GETTERS PARA LA VISTA ---
    @Override
    public JuegoDTO getTablero() {
        JuegoDTO dto = new JuegoDTO();
        List<GrupoDTO> gruposDTO = this.tablero.getFichasEnTablero().stream()
                .map(g -> {
                    List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                            .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                            .collect(Collectors.toList());
                    return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO);
                })
                .collect(Collectors.toList());
        dto.setGruposEnTablero(gruposDTO);
        dto.setFichasMazo(this.tablero.getMazo().size());
        return dto;
    }

    @Override
    public List<FichaJuegoDTO> getMano() {
        return jugador.getManoJugador().getFichasEnMano().stream()
                .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
    }
}
