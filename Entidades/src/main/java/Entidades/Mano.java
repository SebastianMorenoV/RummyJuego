package Entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mano {

    private List<Ficha> fichasEnMano;

    public Mano() {
        fichasEnMano = new ArrayList<>();
    }

    /**
     * Remueve de la mano las fichas que fueron jugadas en el tablero.
     */
    public void removerFichasJugadas(List<Integer> idsFichasEnTablero) {
        this.fichasEnMano.removeIf(ficha -> idsFichasEnTablero.contains(ficha.getId()));
    }

    /**
     * Crea una copia profunda de la mano.
     */
    public Mano copiaProfunda() {
        Mano copia = new Mano();
        List<Ficha> fichasCopia = this.fichasEnMano.stream()
                .map(f -> new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
        copia.setFichasEnMano(fichasCopia);
        return copia;
    }

    public boolean estaVacia() {
        return this.fichasEnMano.isEmpty();
    }

    // --- Getters y Setters ---
    public List<Ficha> getFichasEnMano() {
        return fichasEnMano;
    }

    public void setFichasEnMano(List<Ficha> fichasEnMano) {
        this.fichasEnMano = fichasEnMano;
    }
}
