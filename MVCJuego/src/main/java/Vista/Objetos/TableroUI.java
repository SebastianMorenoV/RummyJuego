package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.VistaTablero;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

public class TableroUI extends JPanel {

    private final int filas = 5;
    private final int columnas = 22;
    private FichaUI[][] celdas; // Matriz para controlar las fichas en la cuadrícula

    private final IModelo modelo;
    private final Controlador control;
    private final VistaTablero vista;

    public TableroUI(IModelo modelo, Controlador control, VistaTablero vista) {
        this.vista = vista;
        this.modelo = modelo;
        this.control = control;

        // Usamos las dimensiones "perfectas" para evitar desfases.
        setSize(660, 245);
        setPreferredSize(new Dimension(660, 245));

        setLayout(null); // Layout absoluto para posicionar fichas manualmente.
        this.celdas = new FichaUI[filas][columnas];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(15, 89, 46));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;

        g.setColor(new Color(0, 0, 0, 40));
        for (int r = 0; r <= filas; r++) {
            g.drawLine(0, r * altoCelda, getWidth(), r * altoCelda);
        }
        for (int c = 0; c <= columnas; c++) {
            g.drawLine(c * anchoCelda, 0, c * anchoCelda, getHeight());
        }
    }

    /**
     * Coloca una ficha en la celda más cercana al punto donde se soltó.
     *
     * @param ficha La ficha a colocar.
     * @param punto El punto (en píxeles) donde se soltó.
     * @return true si se pudo colocar, false si no había espacio.
     */
    public boolean colocarFichaEnCelda(FichaUI ficha, Point punto) {
        Point celda = calcularCeldaParaPunto(punto);

        if (celdas[celda.y][celda.x] == null) { // Si la celda está libre
            centrarYPosicionarFicha(ficha, celda.y, celda.x);
            celdas[celda.y][celda.x] = ficha;
            this.add(ficha);
            return true;
        }
        // Si la celda principal está ocupada, no busca otra.
        return false;
    }

    /**
     * Remueve una ficha del tablero usando su ID.
     *
     * @param idFicha El ID de la ficha a remover.
     */
    public void removerFicha(int idFicha) {
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[r][c] != null && celdas[r][c].getIdFicha() == idFicha) {
                    Component fichaParaRemover = celdas[r][c];
                    celdas[r][c] = null;
                    this.remove(fichaParaRemover);
                    return;
                }
            }
        }
    }

    /**
     * El "puente" al Modelo. Recorre la cuadrícula, descubre los grupos y los
     * prepara para ser validados.
     *
     * @return Una lista de GrupoDTO representando el estado del tablero.
     */
    public List<GrupoDTO> generarGruposDesdeCeldas() {
        List<GrupoDTO> gruposEncontrados = new ArrayList<>();
        boolean[][] visitadas = new boolean[filas][columnas];

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[r][c] != null && !visitadas[r][c]) {
                    List<FichaUI> grupoActual = new ArrayList<>();
                    int columnaActual = c;

                    while (columnaActual < columnas && celdas[r][columnaActual] != null) {
                        grupoActual.add(celdas[r][columnaActual]);
                        visitadas[r][columnaActual] = true;
                        columnaActual++;
                    }

                    if (!grupoActual.isEmpty()) {
                        List<FichaJuegoDTO> fichasDTO = new ArrayList<>();
                        for (FichaUI fichaUI : grupoActual) {
                            fichasDTO.add(new FichaJuegoDTO(
                                    fichaUI.getIdFicha(),
                                    fichaUI.getNumero(),
                                    fichaUI.getColor(),
                                    fichaUI.isComodin()
                            ));
                        }
                        gruposEncontrados.add(new GrupoDTO("No establecido", fichasDTO.size(), fichasDTO));
                    }
                }
            }
        }
        return gruposEncontrados;
    }

    /**
     * Limpia el tablero y lo reconstruye desde cero basándose en los datos del
     * Modelo. Se usa para revertir turnos o cargar partidas.
     */
    public void repintarTablero() {
        this.removeAll();
        this.celdas = new FichaUI[filas][columnas];

        JuegoDTO juego = modelo.getTablero();
        if (juego == null || juego.getGruposEnTablero() == null) {
            return;
        }

        List<GrupoDTO> grupos = juego.getGruposEnTablero();

        int filaActual = 0;
        int colActual = 1; // Empezar en la columna 1 para dejar un margen

        for (GrupoDTO grupoDTO : grupos) {
            if ("Invalido".equals(grupoDTO.getTipo())) {
                continue;
            }

            for (FichaJuegoDTO fichaDTO : grupoDTO.getFichasGrupo()) {
                if (colActual >= this.columnas) { // Si no cabe, salta a la siguiente fila
                    filaActual++;
                    colActual = 1;
                }
                if (filaActual >= this.filas) {
                    break; // Si no hay más filas, para.
                }
                FichaUI fichaUI = new FichaUI(
                        fichaDTO.getIdFicha(),
                        fichaDTO.getNumeroFicha(),
                        fichaDTO.getColor(),
                        fichaDTO.isComodin(),
                        control,
                        vista
                );

                // Coloca la ficha en la celda correspondiente
                celdas[filaActual][colActual] = fichaUI;
                centrarYPosicionarFicha(fichaUI, filaActual, colActual);
                this.add(fichaUI);

                colActual++;
            }
            colActual++; // Deja una celda de espacio entre grupos
        }
        revalidate();
        repaint();
    }

    // --- Métodos de Ayuda para Posicionamiento ---
    private Point calcularCeldaParaPunto(Point puntoEnPixeles) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;
        int col = Math.max(0, Math.min(puntoEnPixeles.x / anchoCelda, columnas - 1));
        int fila = Math.max(0, Math.min(puntoEnPixeles.y / altoCelda, filas - 1));
        return new Point(col, fila);
    }

    private void centrarYPosicionarFicha(FichaUI ficha, int fila, int columna) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;
        int x = (columna * anchoCelda) + (anchoCelda - ficha.getWidth()) / 2;
        int y = (fila * altoCelda) + (altoCelda - ficha.getHeight()) / 2;
        ficha.setLocation(x, y);
    }
}
