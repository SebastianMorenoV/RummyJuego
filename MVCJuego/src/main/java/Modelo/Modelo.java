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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene toda la lógica del juego, valida las jugadas y gestiona el estado.
 * Es la única fuente de verdad para la Vista.
 */
public class Modelo implements IModelo {

    private List<Observador> observadores;
    private Tablero tablero;
    private Jugador jugador;

    // --- Atributos para guardar el estado del turno ---
    private Tablero tableroAlInicioDelTurno;
    private Mano manoAlInicioDelTurno;
    private boolean primerMovimientoRealizado = false;

    public Modelo() {
        observadores = new ArrayList<>();
        tablero = new Tablero();
        jugador = new Jugador();
    }

    // --- Métodos de Inicio y Flujo de Turno ---
    /**
     * Prepara el juego, reparte las fichas y guarda el estado inicial.
     */
    public void iniciarJuego() {
        this.jugador = new Jugador("Sebas", "B1", new Mano());
        tablero.crearMazoCompleto();
        repartirMano(jugador);

        // Guarda el estado inicial del tablero y la mano como el punto de restauración.
        iniciarTurno();

        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    /**
     * Guarda una copia profunda del estado actual del tablero y la mano del
     * jugador. Se llama al inicio de un turno válido.
     */
    public void iniciarTurno() {
        System.out.println("[MODELO] Guardando estado al inicio del turno...");
        this.tableroAlInicioDelTurno = copiaProfundaTablero(this.tablero);
        this.manoAlInicioDelTurno = copiaProfundaMano(this.jugador.getManoJugador());
        System.out.println("[MODELO] Estado guardado.");
    }

    /**
     * Valida la jugada propuesta. Si es válida, la confirma. Si es inválida,
     * revierte el tablero y la mano al estado guardado al inicio del turno.
     *
     * @param gruposPropuestos El estado del tablero que el jugador armó en la
     * UI.
     */
    // En la clase Modelo.java
    public void terminarTurno(List<GrupoDTO> gruposPropuestos) {
        System.out.println("\n[MODELO] Intentando terminar turno...");
        actualizarGruposEnTablero(gruposPropuestos);

        boolean tableroValido = this.tablero.getFichasEnTablero().stream()
                .noneMatch(g -> "Invalido".equals(g.getTipo()));

        // --- LÓGICA DE PRIMERA JUGADA ---
        if (tableroValido && !primerMovimientoRealizado) {
            int puntosJugada = calcularPuntos(this.tablero.getFichasEnTablero());
            System.out.println("[MODELO] Verificando primera jugada. Puntos: " + puntosJugada);
            if (puntosJugada < 30) {
                System.out.println("[MODELO] >> ERROR: La primera jugada debe sumar 30 puntos o más.");
                tableroValido = false; // La jugada no es válida si no cumple el requisito de puntos.
            }
        }

        if (tableroValido) {
            System.out.println("[MODELO] >> ÉXITO: La jugada es válida.");
            if (!primerMovimientoRealizado) {
                primerMovimientoRealizado = true; // Marca que la primera jugada ya se hizo.
                System.out.println("[MODELO] Primera jugada completada exitosamente.");
            }

            // Actualizar la mano del jugador
            List<Integer> idsEnTablero = new ArrayList<>();
            this.tablero.getFichasEnTablero().forEach(g -> g.getFichas().forEach(f -> idsEnTablero.add(f.getId())));
            this.jugador.getManoJugador().getFichasEnMano().removeIf(f -> idsEnTablero.contains(f.getId()));

            iniciarTurno(); // Guardar el nuevo estado válido.

            // --- LÓGICA DE VICTORIA ---
            if (this.jugador.getManoJugador().getFichasEnMano().isEmpty()) {
                System.out.println("[MODELO] ¡JUEGO TERMINADO! El jugador ha ganado.");
                // Aquí podrías añadir una notificación de victoria.
                // notificarObservadores(TipoEvento.JUEGO_TERMINADO);
            }

        } else {
            System.out.println("[MODELO] >> FALLO: Reviertiendo tablero y mano al estado anterior.");
            this.tablero = this.tableroAlInicioDelTurno;
            this.jugador.setManoJugador(this.manoAlInicioDelTurno);
        }

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    /**
     * Calcula el valor en puntos de una lista de grupos. Los comodines no suman
     * puntos.
     */
    private int calcularPuntos(List<Grupo> grupos) {
        int totalPuntos = 0;
        for (Grupo grupo : grupos) {
            for (Ficha ficha : grupo.getFichas()) {
                if (!ficha.isComodin()) {
                    totalPuntos += ficha.getNumero();
                }
            }
        }
        return totalPuntos;
    }

    // --- Métodos de Lógica de Juego y Validación ---
    /**
     * Recibe los grupos desde la UI, los valida individualmente y actualiza el
     * estado temporal del tablero.
     */
    private void actualizarGruposEnTablero(List<GrupoDTO> gruposPropuestos) {
        List<Grupo> nuevosGruposDelTablero = new ArrayList<>();

        for (GrupoDTO grupoDTO : gruposPropuestos) {
            List<Ficha> fichasDelGrupo = grupoDTO.getFichasGrupo().stream()
                    .map(dto -> new Ficha(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin()))
                    .collect(Collectors.toList());

            String tipoGrupoValidado = "Invalido";
            if (fichasDelGrupo.size() >= 3) {
                if (esEscaleraValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "escalera";
                } else if (esTerciaValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "tercia";
                }
            }

            nuevosGruposDelTablero.add(new Grupo(tipoGrupoValidado, fichasDelGrupo.size(), fichasDelGrupo));
        }
        tablero.setFichasEnTablero(nuevosGruposDelTablero);
    }

    /**
     * Verifica si una lista de fichas forma una tercia (o cuarta) válida,
     * considerando los comodines. Regla: Mismo número, colores diferentes.
     */
    private boolean esTerciaValida(List<Ficha> fichas) {
        if (fichas == null || fichas.size() < 3 || fichas.size() > 4) {
            return false;
        }

        // Separamos las fichas reales de los comodines
        List<Ficha> fichasReales = fichas.stream().filter(f -> !f.isComodin()).collect(Collectors.toList());

        // Si no hay fichas reales (puros comodines), la jugada es válida.
        if (fichasReales.isEmpty()) {
            return true;
        }

        // Verificamos que todas las fichas reales tengan el mismo número
        int numeroDelGrupo = fichasReales.get(0).getNumero();
        for (Ficha f : fichasReales) {
            if (f.getNumero() != numeroDelGrupo) {
                return false;
            }
        }

        // Verificamos que las fichas reales no tengan colores repetidos
        long coloresUnicos = fichasReales.stream().map(Ficha::getColor).distinct().count();
        if (coloresUnicos < fichasReales.size()) {
            return false;
        }

        return true; // Si pasó las pruebas, es válida.
    }

    /**
     * Verifica si una lista de fichas forma una escalera válida, considerando
     * los comodines. Regla: Números consecutivos, mismo color.
     */
    private boolean esEscaleraValida(List<Ficha> fichas) {
        if (fichas == null || fichas.size() < 3) {
            return false;
        }

        // Separamos las fichas reales de los comodines
        List<Ficha> fichasReales = fichas.stream().filter(f -> !f.isComodin()).collect(Collectors.toList());
        int numComodines = fichas.size() - fichasReales.size();

        // Si hay menos de 2 fichas reales, es imposible determinar el color o la secuencia.
        // Una escalera de puros comodines es válida.
        if (fichasReales.size() < 2) {
            return true;
        }

        // Verificamos que todas las fichas reales sean del mismo color
        Color colorDelGrupo = fichasReales.get(0).getColor();
        for (Ficha f : fichasReales) {
            if (!f.getColor().equals(colorDelGrupo)) {
                return false;
            }
        }

        // Ordenamos las fichas reales por número
        fichasReales.sort(Comparator.comparingInt(Ficha::getNumero));

        // Contamos cuántos "huecos" hay en la secuencia
        int huecos = 0;
        for (int i = 0; i < fichasReales.size() - 1; i++) {
            // La diferencia de números menos 1 nos da el número de fichas faltantes
            huecos += (fichasReales.get(i + 1).getNumero() - fichasReales.get(i).getNumero() - 1);
        }

        // La jugada es válida si tenemos suficientes comodines para llenar los huecos
        return numComodines >= huecos;
    }

    // --- Métodos de Ayuda (Copias Profundas) ---
    private Tablero copiaProfundaTablero(Tablero original) {
        Tablero copia = new Tablero();
        List<Grupo> gruposCopia = original.getFichasEnTablero().stream()
                .map(g -> {
                    List<Ficha> fichasCopia = g.getFichas().stream()
                            .map(f -> new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                            .collect(Collectors.toList());
                    return new Grupo(g.getTipo(), g.getNumFichas(), fichasCopia);
                })
                .collect(Collectors.toList());
        copia.setFichasEnTablero(gruposCopia);
        copia.setMazo(new ArrayList<>(original.getMazo()));
        return copia;
    }

    private Mano copiaProfundaMano(Mano original) {
        Mano copia = new Mano();
        List<Ficha> fichasCopia = original.getFichasEnMano().stream()
                .map(f -> new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
        copia.setFichasEnMano(fichasCopia);
        copia.setCantidadFichasEnMano(fichasCopia.size());
        return copia;
    }

    // --- Getters, Observadores y otros métodos ---
    @Override
    public JuegoDTO getTablero() {
        JuegoDTO juegoDTO = new JuegoDTO();
        List<GrupoDTO> gruposDTO = this.tablero.getFichasEnTablero().stream()
                .map(grupo -> {
                    List<FichaJuegoDTO> fichasDTO = grupo.getFichas().stream()
                            .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                            .collect(Collectors.toList());
                    return new GrupoDTO(grupo.getTipo(), grupo.getFichas().size(), fichasDTO);
                })
                .collect(Collectors.toList());
        juegoDTO.setGruposEnTablero(gruposDTO);
        juegoDTO.setFichasMazo(this.tablero.getMazo().size());
        return juegoDTO;
    }

    @Override
    public List<FichaJuegoDTO> getMano() {
        return jugador.getManoJugador().getFichasEnMano().stream()
                .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : observadores) {
            observer.actualiza(this, tipoEvento);
        }
    }

    public void agregarObservador(Observador obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void repartirMano(Jugador jugador) {
        List<Ficha> mazo = tablero.getMazo();
        List<Ficha> fichasMano = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            if (!mazo.isEmpty()) {
                fichasMano.add(mazo.remove(0));
            }
        }
        jugador.getManoJugador().setFichasEnMano(fichasMano);
    }

    public void tomarFichaMazo() {
        if (!tablero.getMazo().isEmpty()) {
            Ficha fichaTomada = tablero.tomarFichaMazo();
            jugador.agregarFichaAJugador(fichaTomada);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.TOMO_FICHA);
        }
    }
}
