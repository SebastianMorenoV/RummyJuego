package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
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
    private VistaTablero vista;
    private Controlador controlador;

    public TableroUI() {
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

    // Coloca la ficha en la celda más cercana a un punto dado
    public boolean colocarFichaEnCelda(FichaUI ficha, Point punto) {
        Point celda = obtenerCeldaLibreCercana(punto);
        if (celda == null) {
            return false;
        }

        int fila = celda.y;
        int columna = celda.x;

        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;

        // Marcar la celda ocupada
        celdas[fila][columna] = ficha;

        // Posicionar la ficha centrada en la celda
        int x = columna * anchoCelda + (anchoCelda - ficha.getWidth()) / 2;
        int y = fila * altoCelda + (altoCelda - ficha.getHeight()) / 2;
        ficha.setLocation(x, y);

        add(ficha);
        setComponentZOrder(ficha, 0);
        revalidate();
        repaint();
        return true;
    }

    // Devuelve la celda vacía más cercana al punto
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

    // Remover ficha de su celda
    public void removerFicha(FichaUI ficha) {
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[r][c] == ficha) {
                    celdas[r][c] = null;
                    remove(ficha);
                    revalidate();
                    repaint();
                    return;
                }
            }
        }
    }
    
    public void actualiza(JuegoDTO juego) {
        // 1️⃣ Limpiar todas las fichas anteriores del tablero
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[r][c] != null) {
                    remove(celdas[r][c]);
                    celdas[r][c] = null;
                }
            }
        }

        // 2️⃣ Recorrer todos los grupos del tablero
        for (GrupoDTO grupo : juego.getGruposEnTablero()) {
            for (FichaJuegoDTO fichaDTO : grupo.getFichasGrupo()) {
                // 3️⃣ Crear componente visual usando el constructor correcto de FichaUI
                FichaUI fichaUI = new FichaUI(
                        fichaDTO.getIdFicha(),
                        fichaDTO.getNumeroFicha(),
                        fichaDTO.getColor(),
                        fichaDTO.isComodin(),
                        controlador, // referencia al controlador
                        vista // referencia a la vista contenedora
                );

                // 4️⃣ Calcular la celda más cercana a las coordenadas de la ficha
                Point celda = obtenerCeldaLibreCercana(new Point(fichaDTO.getX(), fichaDTO.getY()));
                if (celda != null) {
                    colocarFichaEnCelda(fichaUI, new Point(fichaDTO.getX(), fichaDTO.getY()));
                }
            }
        }

        // 5️⃣ Redibujar
        revalidate();
        repaint();
    }


}
