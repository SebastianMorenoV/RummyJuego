package Entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta clase representa el tablero de el juego Rummy. Se compone de un arreglo
 * de grupos , que representan los grupos puestos en el tablero. Un mazo de
 * fichas , para ser utilizadas por los jugadores.
 *
 * @author Sebastian Moreno
 */
public class Tablero {

    private List<Grupo> fichasEnTablero;
    private List<Ficha> mazo;

    public Tablero() {
        this.fichasEnTablero = new ArrayList<>();
        this.mazo = new ArrayList<>();
    }

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
            return false;
        }

        // Regla 2: Si es el primer movimiento, la suma de puntos debe ser >= 30.
        if (esPrimerMovimiento) {
            int puntos = this.calcularPuntosDeLaJugada();
            if (puntos < 30) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula la suma de puntos de todos los grupos válidos en el tablero.
     *
     * @return el numero de puntos validos en la jugada nueva de el jugador.
     */
    public int calcularPuntosDeLaJugada() {
        return this.fichasEnTablero.stream()
                .mapToInt(Grupo::calcularPuntos)
                .sum();
    }

    /**
     * Metodo auxiliar para tener los ids de las fichas que hay en el tablero.
     *
     * @return lista de todos los ids de las fichas que hay en el tablero.
     */
    public List<Integer> getTodosLosIdsDeFichas() {
        return this.fichasEnTablero.stream()
                .flatMap(grupo -> grupo.getFichas().stream())
                .map(Ficha::getId)
                .collect(Collectors.toList());
    }

    /**
     * Busca y remueve una ficha específica del tablero por su ID.       Si un
     * grupo queda vacío después de remover la ficha, el grupo es eliminado.
     *
     * @param idFicha El ID de la ficha a remover.    
     * @return La instancia de la Ficha removida, o null si no se encontró.    
     *  
     */
    public Ficha removerFicha(int idFicha) {
        Ficha fichaRemovida = null;
        // Usamos un iterador para poder remover grupos de forma segura mientras iteramos
        for (java.util.Iterator<Grupo> grupoIterator = this.fichasEnTablero.iterator(); grupoIterator.hasNext();) {
            Grupo grupo = grupoIterator.next();
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
        return null;
    }

    /**
     * Este metodo auxiliar crea una copia de el tablero antes de empezar el
     * movimiento. Basicamente filtra todos los grupos que existen al inicio y
     * los guarda en un nuevo arreglo. tambien crea la copia de el mazo en ese
     * momento.
     *
     * @return El tablero copiado.
     */
    public Tablero copiaProfunda() {
        Tablero copia = new Tablero();
        List<Grupo> gruposCopia = this.fichasEnTablero.stream()
                .map(g -> {
                    List<Ficha> fichasCopia = g.getFichas().stream()
                            .map(f -> new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                            .collect(Collectors.toList());
                    return new Grupo(g.getTipo(), fichasCopia.size(), fichasCopia, g.getFila(), g.getColumna());
                })
                .collect(Collectors.toList());
        copia.setFichasEnTablero(gruposCopia);
        copia.setMazo(new ArrayList<>(this.mazo));
        return copia;
    }

    /**
     * Metodo para repartir fichas en la mano de un jugador.
     *
     * @param jugador el jugador a recibir las fichas.
     * @param cantidadFichas la cantidad de fichas que se van a repartir , por
     * defecto 14.
     */
    public void repartirMano(Jugador jugador, int cantidadFichas) {
        List<Ficha> mano = new ArrayList<>();
        for (int i = 0; i < cantidadFichas && !this.mazo.isEmpty(); i++) {
            mano.add(this.mazo.remove(0));
        }
        jugador.getManoJugador().setFichasEnMano(mano);
    }

    /**
     * Metodo para tomar la ficha de un mazo.
     *
     * @return La ficha tomada , en caso de no haber fichas se regresa null.
     */
    public Ficha tomarFichaMazo() {
        return mazo.isEmpty() ? null : mazo.remove(0);
    }

    /**
     * Método para crear un mazo completo de 108 fichas 4 sets de colores.
     * Primero crea los ids de las fichas de 1 - 108, los barajea. Crea cada
     * ficha con un color random y un numero random.
     */
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

    //GETS Y SETS//
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
