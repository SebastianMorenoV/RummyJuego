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
     * Valida la jugada final en el tablero. Si es válida, confirma los cambios
     * y actualiza la mano. Si es inválida, revierte el tablero y la mano al
     * estado inicial del turno.
     */
    public void terminarTurno() { // Recibe vacío, ya que el tablero ya está actualizado
        System.out.println("\n[MODELO] Intentando terminar turno con el estado actual del tablero...");

        // Un turno es válido si:
        // 1. No hay ningún grupo marcado como "Invalido" (grupos >= 3 que no cumplen regla).
        // 2. Todos los grupos de 1 o 2 fichas son eliminados o retornados a la mano (esto es responsabilidad de la vista/jugador en este modelo, pero aquí validamos que no queden).
        // NOTA: Para este modelo, un grupo 'Temporal' (1 o 2 fichas) NO debe estar en el tablero al finalizar el turno.
        boolean tableroValido = this.tablero.getFichasEnTablero().stream()
                .noneMatch(g -> "Invalido".equals(g.getTipo()) || "Temporal".equals(g.getTipo()));

        // --- LÓGICA DE PRIMERA JUGADA (Solo si el tablero es válido hasta ahora) ---
        if (tableroValido && !primerMovimientoRealizado) {
            // En este punto, solo contamos los puntos de los grupos válidos ('escalera' o 'tercia')
            int puntosJugada = calcularPuntos(this.tablero.getFichasEnTablero());
            System.out.println("[MODELO] Verificando primera jugada. Puntos: " + puntosJugada);
            if (puntosJugada < 30) {
                System.out.println("[MODELO] >> ERROR: La primera jugada debe sumar 30 puntos o más.");
                tableroValido = false;
                notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);

            }
        }

        if (tableroValido) {
            System.out.println("[MODELO] >> ÉXITO: La jugada es válida. Confirmando cambios.");

            // Si es el primer movimiento válido
            if (!primerMovimientoRealizado) {
                primerMovimientoRealizado = true;
                System.out.println("[MODELO] Primera jugada completada exitosamente.");
            }

            // --- Actualizar la mano del jugador (mover fichas de la mano al tablero) ---
            // Se identifican las fichas que están en el tablero...
            List<Integer> idsEnTablero = new ArrayList<>();
            this.tablero.getFichasEnTablero().forEach(g -> g.getFichas().forEach(f -> idsEnTablero.add(f.getId())));

            // ...y se eliminan de la mano si su ID está en el tablero.
            this.jugador.getManoJugador().getFichasEnMano().removeIf(f -> idsEnTablero.contains(f.getId()));

            iniciarTurno(); // Guardar el nuevo estado válido (para el próximo turno).
            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            // --- LÓGICA DE VICTORIA ---
            if (this.jugador.getManoJugador().getFichasEnMano().isEmpty()) {
                System.out.println("[MODELO] ¡JUEGO TERMINADO! El jugador ha ganado.");
                // notificarObservadores(TipoEvento.JUEGO_TERMINADO);
            }

            notificarObservadores(TipoEvento.REPINTAR_MANO); // Repintar mano para mostrar las fichas restantes

        } else {
            System.out.println("[MODELO] >> FALLO: Reviertiendo tablero y mano al estado anterior.");
            // Restaurar estado
            this.tablero = copiaProfundaTablero(this.tableroAlInicioDelTurno);
            this.jugador.setManoJugador(copiaProfundaMano(this.manoAlInicioDelTurno));

            // El tablero se repinta para mostrar el estado revertido
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
            notificarObservadores(TipoEvento.REPINTAR_MANO);
        }
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
     * Recibe el estado actual del tablero desde la UI, lo valida
     * individualmente (marcando los grupos con menos de 3 fichas o inválidos) y
     * actualiza el estado temporal del tablero de la entidad. Este método
     * permite la retroalimentación visual inmediata.
     *
     * @param gruposPropuestos El estado actual del tablero que el jugador armó
     * en la UI.
     */
    public void colocarFicha(List<GrupoDTO> gruposPropuestos) { // Antes era terminarTurno
        System.out.println("\n[MODELO] Actualizando y validando grupos en tiempo real...");
        List<Grupo> nuevosGruposDelTablero = new ArrayList<>();

        for (GrupoDTO grupoDTO : gruposPropuestos) {
            // 1. Crear las entidades Ficha a partir de los DTOs
            List<Ficha> fichasDelGrupo = grupoDTO.getFichasGrupo().stream()
                    .map(dto -> new Ficha(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin()))
                    .collect(Collectors.toList());

            String tipoGrupoValidado = "Temporal"; // Nuevo tipo: 'Temporal' para grupos < 3 fichas

            // 2. Validar Grupos: Solo si tienen 3 o más fichas
            if (fichasDelGrupo.size() >= 3) {
                if (esEscaleraValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "escalera";
                } else if (esTerciaValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "tercia";
                } else {
                    tipoGrupoValidado = "Invalido";
                }
            }

            // 3. Crear el nuevo Grupo y agregarlo a la lista temporal
            nuevosGruposDelTablero.add(new Grupo(tipoGrupoValidado, fichasDelGrupo.size(), fichasDelGrupo));
        }

        // 4. Actualizar el tablero de la entidad con el nuevo estado validado
        this.tablero.setFichasEnTablero(nuevosGruposDelTablero);

        // 5. Notificar a la vista para que se repinte con el estado actual (incluyendo grupos 'Invalido' o 'Temporal')
        // Esto resuelve los bugs visuales al tener una ÚNICA fuente de verdad.
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
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
