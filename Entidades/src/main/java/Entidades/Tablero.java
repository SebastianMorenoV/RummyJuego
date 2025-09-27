package Entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tablero {

    private List<Grupo> fichasEnTablero;
    private List<Ficha> mazo;

    public Tablero() {
        this.fichasEnTablero = new ArrayList<>();
        this.mazo = new ArrayList<>();
    }

    // --- LÓGICA DE NEGOCIO ---
    /**
     * El método de validación principal. Determina si el estado actual del
     * tablero es válido para terminar el turno, considerando si es el primer
     * movimiento.
     *
     * @param esPrimerMovimiento Indica si es la primera jugada del jugador.
     * @return true si la jugada es completamente válida, false en caso
     * contrario.
     */
    public boolean esJugadaValida(boolean esPrimerMovimiento) {
        // Regla 1: No debe haber grupos temporales o inválidos.
        boolean estructuraValida = this.fichasEnTablero.stream()
                .noneMatch(g -> "Invalido".equals(g.getTipo()) || "Temporal".equals(g.getTipo()));

        if (!estructuraValida) {
            System.out.println("[ENTIDAD-TABLERO] >> FALLO: Hay grupos inválidos o incompletos.");
            return false;
        }

        // Regla 2: Si es el primer movimiento, la suma de puntos debe ser >= 30.
        if (esPrimerMovimiento) {
            int puntos = this.calcularPuntosDeLaJugada();
            if (puntos < 30) {
                System.out.println("[ENTIDAD-TABLERO] >> FALLO: Primera jugada requiere 30 puntos, solo hay " + puntos);
                return false;
            }
        }

        // Si pasó todas las validaciones, la jugada es correcta.
        return true;
    }

    /**
     * Calcula la suma de puntos de todos los grupos válidos en el tablero.
     */
    public int calcularPuntosDeLaJugada() {
        return this.fichasEnTablero.stream()
                .mapToInt(Grupo::calcularPuntos)
                .sum();
    }

    // ... (El resto de la clase Tablero se mantiene igual: copiaProfunda, repartirMano, etc.)
    public List<Integer> getTodosLosIdsDeFichas() {
        return this.fichasEnTablero.stream()
                .flatMap(grupo -> grupo.getFichas().stream())
                .map(Ficha::getId)
                .collect(Collectors.toList());
    }
    // Archivo: Entidades/Tablero.java

    /**
     *      * Busca y remueve una ficha específica del tablero por su ID.      *
     * Si un grupo queda vacío después de remover la ficha, el grupo es
     * eliminado.      *      * @param idFicha El ID de la ficha a remover.    
     *  * @return La instancia de la Ficha removida, o null si no se encontró.  
     *    
     */
    public Ficha removerFicha(int idFicha) {
        Ficha fichaRemovida = null;
        // Usamos un iterador para poder remover grupos de forma segura mientras iteramos
        for (java.util.Iterator<Grupo> grupoIterator = this.fichasEnTablero.iterator(); grupoIterator.hasNext();) {
            Grupo grupo = grupoIterator.next();
            // Buscamos la ficha dentro del grupo actual
            java.util.Optional<Ficha> fichaEncontrada = grupo.getFichas().stream()
                    .filter(f -> f.getId() == idFicha)
                    .findFirst();

            if (fichaEncontrada.isPresent()) {
                fichaRemovida = fichaEncontrada.get();
                grupo.getFichas().remove(fichaRemovida); // La quitamos del grupo

// Si el grupo se quedó sin fichas, lo eliminamos del tablero
                if (grupo.getFichas().isEmpty()) {
                    grupoIterator.remove();
                }

                return fichaRemovida; // Devolvemos la ficha y terminamos la búsqueda
            }
        }
        return null; // No se encontró la ficha en ningún grupo
    }

// ... (resto del código de la clase Tablero)
    public Tablero copiaProfunda() {
        Tablero copia = new Tablero();
        List<Grupo> gruposCopia = this.fichasEnTablero.stream()
                .map(g -> {
                    List<Ficha> fichasCopia = g.getFichas().stream()
                            .map(f -> new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                            .collect(Collectors.toList());
                    return new Grupo(g.getTipo(), fichasCopia.size(), fichasCopia);
                })
                .collect(Collectors.toList());
        copia.setFichasEnTablero(gruposCopia);
        copia.setMazo(new ArrayList<>(this.mazo));
        return copia;
    }

    public void repartirMano(Jugador jugador, int cantidadFichas) {
        List<Ficha> mano = new ArrayList<>();
        for (int i = 0; i < cantidadFichas && !this.mazo.isEmpty(); i++) {
            mano.add(this.mazo.remove(0));
        }
        jugador.getManoJugador().setFichasEnMano(mano);
    }

    public Ficha tomarFichaMazo() {
        return mazo.isEmpty() ? null : mazo.remove(0);
    }

    public void crearMazoCompleto() {
        this.mazo.clear();
        Color[] colores = {Color.RED, Color.BLUE, Color.BLACK, Color.ORANGE};
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= 108; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);

        for (int i = 0; i < 2; i++) {
            for (Color color : colores) {
                for (int num = 1; num <= 13; num++) {
                    this.mazo.add(new Ficha(ids.remove(0), num, color, false));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            this.mazo.add(new Ficha(ids.remove(0), 0, Color.GRAY, true));
        }
        Collections.shuffle(this.mazo);
    }

    public List<Grupo> getFichasEnTablero() {
        return fichasEnTablero;
    }

    public void setFichasEnTablero(List<Grupo> fichasEnTablero) {
        this.fichasEnTablero = fichasEnTablero;
    }

    public List<Ficha> getMazo() {
        return mazo;
    }

    public void setMazo(List<Ficha> mazo) {
        this.mazo = mazo;
    }
}
