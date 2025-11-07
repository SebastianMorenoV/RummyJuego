package DTO;

import java.awt.Color;

/**
 *
 * @author benja
 */
public class FichaJuegoDTO {

    int idFicha;
    int numeroFicha;
    Color color;
    boolean comodin;
    int fila;
    int columna;

    public FichaJuegoDTO(int numeroFicha, Color color, boolean comodin) {
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
    }

    public FichaJuegoDTO(int idFicha, int numeroFicha, Color color, boolean comodin) {
        this.idFicha = idFicha;
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
    }

    public FichaJuegoDTO(int idFicha, int numeroFicha, Color color, boolean comodin, int fila, int columna) {
        this.idFicha = idFicha;
        this.numeroFicha = numeroFicha;
        this.color = color;
        this.comodin = comodin;
        this.fila = fila;
        this.columna = columna;
    }

    public FichaJuegoDTO() {

    }

    // Getters and setters
    public int getNumeroFicha() {
        return numeroFicha;
    }

    public void setNumeroFicha(int numeroFicha) {
        this.numeroFicha = numeroFicha;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isComodin() {
        return comodin;
    }

    public void setComodin(boolean comodin) {
        this.comodin = comodin;
    }

    public int getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(int idFicha) {
        this.idFicha = idFicha;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Convierte la ficha a un string plano usando comas. El Color se convierte
     * a un único número entero (RGB).
     *
     * Formato: idFicha,numeroFicha,rgbColor,comodin,fila,columna
     */
    public String serializar() {
        // Obtenemos el valor RGB del color como int
        // Si el color fuera nulo(null), usamos negro como default
        int rgb = (this.color != null) ? this.color.getRGB() : Color.BLACK.getRGB();

        return this.idFicha + ","
                + this.numeroFicha + ","
                + rgb + ","
                + this.comodin + ","
                + this.fila + ","
                + this.columna;
    }

    /**
     * Método estático (factory) para crear una FichaJuegoDTO desde un string
     * serializado.
     *
     * @param data El string generado por .serializar()
     * @return una nueva instancia de FichaJuegoDTO
     */
    public static FichaJuegoDTO deserializar(String data) {
        try {
            String[] parts = data.split(",");

            int id = Integer.parseInt(parts[0]);
            int num = Integer.parseInt(parts[1]);
            int rgb = Integer.parseInt(parts[2]);
            boolean com = Boolean.parseBoolean(parts[3]);
            int f = Integer.parseInt(parts[4]);
            int c = Integer.parseInt(parts[5]);

            // Creamos el color de vuelta a partir del entero RGB
            Color color = new Color(rgb);

            return new FichaJuegoDTO(id, num, color, com, f, c);

        } catch (Exception e) {
            System.err.println("ERROR al deserializar FichaJuegoDTO: " + data);
            e.printStackTrace();
            return null; // Devuelve null si el formato es incorrecto
        }
    }

    @Override
    public String toString() {
        return "FichaJuegoDTO{"
                + "idFicha=" + idFicha
                + ", numeroFicha=" + numeroFicha
                + ", color=" + color
                + ", comodin=" + comodin + '}';
    }
}
