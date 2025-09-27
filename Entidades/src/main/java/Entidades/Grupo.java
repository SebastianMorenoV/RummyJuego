package Entidades;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Grupo {

    private String tipo;
    private int numFichas;
    private List<Ficha> fichas;

    public Grupo(String tipo, int numFichas, List<Ficha> fichas) {
        this.tipo = tipo;
        this.numFichas = numFichas;
        this.fichas = fichas;
    }

    public void validarYEstablecerTipo() {
        this.numFichas = this.fichas.size();

        if (this.numFichas < 3) {
            this.tipo = "Temporal";
            return;
        }

        if (esEscaleraValida()) {
            this.tipo = "escalera";
        } else if (esTerciaValida()) {
            this.tipo = "tercia";
        } else {
            this.tipo = "Invalido";
        }
    }

    /**
     * Verifica si el grupo es una tercia/cuarta válida (mismo número, diferente
     * color).
     */
    public boolean esTerciaValida() {
        if (this.fichas == null || this.fichas.size() < 3 || this.fichas.size() > 4) {
            return false;
        }

        List<Ficha> fichasReales = this.fichas.stream().filter(f -> !f.isComodin()).collect(Collectors.toList());
        if (fichasReales.isEmpty()) {
            return true; // Puros comodines es válido
        }
        int numeroBase = fichasReales.get(0).getNumero();
        boolean mismoNumero = fichasReales.stream().allMatch(f -> f.getNumero() == numeroBase);
        if (!mismoNumero) {
            return false;
        }

        long coloresUnicos = fichasReales.stream().map(Ficha::getColor).distinct().count();
        return coloresUnicos == fichasReales.size(); // No debe haber colores repetidos
    }

    /**
     * Verifica si el grupo es una escalera válida (mismo color, números
     * consecutivos).
     */
    public boolean esEscaleraValida() {
        if (this.fichas == null || this.fichas.size() < 3) {
            return false;
        }

        Color colorDelGrupo = null;
        Integer numeroAncla = null;
        int indiceAncla = -1;

        for (int i = 0; i < this.fichas.size(); i++) {
            if (!this.fichas.get(i).isComodin()) {
                colorDelGrupo = this.fichas.get(i).getColor();
                numeroAncla = this.fichas.get(i).getNumero();
                indiceAncla = i;
                break;
            }
        }

        if (colorDelGrupo == null) {
            return true; // Puros comodines es válido
        }
        for (int i = 0; i < this.fichas.size(); i++) {
            int numeroEsperado = numeroAncla + (i - indiceAncla);
            if (numeroEsperado > 13 || numeroEsperado < 1) {
                return false; // Fuera de rango
            }
            Ficha fichaActual = this.fichas.get(i);
            if (!fichaActual.isComodin()) {
                if (!fichaActual.getColor().equals(colorDelGrupo)) {
                    return false;
                }
                if (fichaActual.getNumero() != numeroEsperado) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calcula los puntos del grupo.
     */
    public int calcularPuntos() {
        if (!"escalera".equals(tipo) && !"tercia".equals(tipo)) {
            return 0;
        }

        int puntosGrupo = 0;
        if ("tercia".equals(tipo)) {
            int numBase = fichas.stream()
                    .filter(f -> !f.isComodin())
                    .mapToInt(Ficha::getNumero)
                    .findFirst()
                    .orElse(13); // Valor para grupo de puros comodines
            puntosGrupo = numBase * fichas.size();
        } else if ("escalera".equals(tipo)) {
            Integer numeroAncla = null;
            int indiceAncla = -1;
            for (int i = 0; i < fichas.size(); i++) {
                if (!fichas.get(i).isComodin()) {
                    numeroAncla = fichas.get(i).getNumero();
                    indiceAncla = i;
                    break;
                }
            }
            if (numeroAncla != null) {
                for (int i = 0; i < fichas.size(); i++) {
                    puntosGrupo += numeroAncla + (i - indiceAncla);
                }
            } else { // Escalera de puros comodines
                puntosGrupo = 13 * fichas.size();
            }
        }
        return puntosGrupo;
    }

    // --- Getters y Setters ---
    public String getTipo() {
        return tipo;
    }

    public List<Ficha> getFichas() {
        return fichas;
    }

    public void setFichas(List<Ficha> fichas) {
        this.fichas = fichas;
    }
}
