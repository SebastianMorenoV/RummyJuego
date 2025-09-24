package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.VistaTablero;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class TableroUI extends JPanel {

    private final int filas = 5;
    private final int columnas = 22;
    private FichaUI[][] celdas; // Control de fichas en cada celda
    private IModelo modelo;
    private Controlador control;
    private VistaTablero vista;

    public TableroUI(IModelo modelo, Controlador control, VistaTablero vista) {
        this.vista = vista;
        this.modelo = modelo;
        this.control = control;
        setPreferredSize(new Dimension(880, 275)); // tamaño base
        setBorder(new LineBorder(new Color(6, 71, 34), 3, true));
        setLayout(null); // absolute layout para posicionar fichas manualmente
        celdas = new FichaUI[filas][columnas];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;

        // Fondo verde redondeado
        g.setColor(new Color(15, 89, 46));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Dibujar líneas de las celdas
        g.setColor(new Color(0, 0, 0, 50));
        for (int r = 0; r <= filas; r++) {
            g.drawLine(0, r * altoCelda, getWidth(), r * altoCelda);
        }
        for (int c = 0; c <= columnas; c++) {
            g.drawLine(c * anchoCelda, 0, c * anchoCelda, getHeight());
        }
    }

    /**
     * Metodo para colocar una ficha desde dentro o fuera de el tablero.
     * @param ficha La ficha que se quiere colocar.
     * @param punto Las coordenadas de la ficha.
     * @return true si la ficha se logra colocar , false si no se puede colocar porque no cabe.
     */
    public boolean colocarFichaEnCelda(FichaUI ficha, Point punto) {
        // 1. Si la ficha no está en el tablero, busca la celda libre más cercana
        Point celda = obtenerCeldaLibreCercana(punto);

        if (celda == null) {
            return false;
        }

        int fila = celda.y;
        int columna = celda.x;

        // 1. Llama al método auxiliar para el cálculo y posicionamiento
        centrarYPosicionarFicha(ficha, fila, columna);

        // Marcar la celda ocupada
        celdas[fila][columna] = ficha;

        add(ficha);
        setComponentZOrder(ficha, 0);
        revalidate();
        repaint();
        return true;
    }

    /**
     * Metodo que obtiene la celda mas cercana al punto que le pasamos.
     * @param punto El punto que se tiene que evaluar.
     * @return El punto donde esta la celda.
     */
    public Point obtenerCeldaLibreCercana(Point punto) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;

        int col = Math.min(punto.x / anchoCelda, columnas - 1);
        int fila = Math.min(punto.y / altoCelda, filas - 1);

        if (celdas[fila][col] == null) {
            return new Point(col, fila);
        }

        // Buscar la celda vacía más cercana
        int distancia = 1;
        while (distancia < Math.max(filas, columnas)) {
            for (int dr = -distancia; dr <= distancia; dr++) {
                for (int dc = -distancia; dc <= distancia; dc++) {
                    int nuevaFila = fila + dr;
                    int nuevaCol = col + dc;
                    if (nuevaFila >= 0 && nuevaFila < filas
                            && nuevaCol >= 0 && nuevaCol < columnas
                            && celdas[nuevaFila][nuevaCol] == null) {
                        return new Point(nuevaCol, nuevaFila);
                    }
                }
            }
            distancia++;
        }
        return null;
    }

    public void removerFicha(int idFicha) {
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[r][c] != null && celdas[r][c].getIdFicha() == idFicha) {
                    FichaUI ficha = celdas[r][c];
                    celdas[r][c] = null;
                    remove(ficha);
                    revalidate();
                    repaint();
                    return;
                }
            }
        }
    }

    public void repintarTablero() {
        this.removeAll(); // Elimina todos los componentes
        this.celdas = new FichaUI[filas][columnas]; // Reinicia el control de celdas

        JuegoDTO juego = modelo.getTablero();
        List<GrupoDTO> grupos = juego.getGruposEnTablero();

        // Iterar sobre los datos del modelo
        for (GrupoDTO grupoDTO : grupos) {
            if (grupoDTO.getTipo().equals("No establecido")) {
                continue;
            }

            for (FichaJuegoDTO fichaDTO : grupoDTO.getFichasGrupo()) {
                FichaUI fichaUI = new FichaUI(
                        fichaDTO.getIdFicha(),
                        fichaDTO.getNumeroFicha(),
                        fichaDTO.getColor(),
                        fichaDTO.isComodin(),
                        control,
                        new Point(fichaDTO.getX(), fichaDTO.getY()),
                        vista
                );

                // Calcular la fila y columna desde las coordenadas x,y del DTO
                int anchoCelda = getWidth() / columnas;
                int altoCelda = getHeight() / filas;
                int columna = fichaDTO.getX() / anchoCelda;
                int fila = fichaDTO.getY() / altoCelda;

                // Usar un método para colocar la ficha en la celda exacta
                colocarFichaEnCeldaExacta(fichaUI, fila, columna);
            }
        }

        this.revalidate();
        this.repaint();
    }

    public void colocarFichaEnCeldaExacta(FichaUI ficha, int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return;
        }
        // 1. Llama al método auxiliar para el cálculo y posicionamiento
        centrarYPosicionarFicha(ficha, fila, columna);
        // 2. Lógica restante (actualizar el panel y el arreglo)
        celdas[fila][columna] = ficha;
        add(ficha);
        setComponentZOrder(ficha, 0);
        revalidate();
        repaint();
    }

    /**
     * Calcula las coordenadas para centrar la ficha en una celda y la
     * posiciona.
     */
    private void centrarYPosicionarFicha(FichaUI ficha, int fila, int columna) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;

        // Lógica de centrado (ahora vive en un solo lugar)
        int x = columna * anchoCelda + (anchoCelda - ficha.getWidth()) / 2;
        int y = fila * altoCelda + (altoCelda - ficha.getHeight()) / 2;

        ficha.setLocation(x, y);
    }
}
