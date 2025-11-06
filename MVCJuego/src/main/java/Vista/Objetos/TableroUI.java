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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author Sebastian Moreno
 */
public class TableroUI extends JPanel {

    private final int filas = 5;
    private final int columnas = 22;
    private final Map<Integer, FichaUI> fichasEnTablero;
    private final Map<Integer, FichaUI> fichasEnTableroValidas;
    private final Map<Integer, Point> posicionesValidas;
    private final IModelo modelo;
    private final Controlador control;
    private final VistaTablero vista;

    public TableroUI(IModelo modelo, Controlador control, VistaTablero vista) {
        this.vista = vista;
        this.modelo = modelo;
        this.control = control;
        this.fichasEnTablero = new HashMap<>();
        this.fichasEnTableroValidas = new HashMap<>();
        this.posicionesValidas = new HashMap<>();

        setSize(660, 245);
        setPreferredSize(new Dimension(660, 245));
        setLayout(null);
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
     * Coloca una ficha en la celda más cercana y la guarda en el estado local.
     */
    public boolean colocarFichaEnCelda(FichaUI ficha, Point punto) {
        Point celda = calcularCeldaParaPunto(punto);

        // Verificamos si la celda esta ocupada
        for (FichaUI fichaExistente : fichasEnTablero.values()) {
            Point celdaExistente = calcularCeldaParaPunto(fichaExistente.getLocation());
            if (celdaExistente.equals(celda)) {
                return false; // Celda ocupada
            }
        }

        centrarYPosicionarFicha(ficha, celda.y, celda.x);
        this.add(ficha);
        fichasEnTablero.put(ficha.getIdFicha(), ficha);

        this.revalidate();
        this.repaint();
        return true;
    }

    /**
     * Remueve una ficha del estado local del tablero.
     */
    public void removerFicha(int idFicha) {
        FichaUI fichaParaRemover = fichasEnTablero.remove(idFicha);
        if (fichaParaRemover != null) {
            this.remove(fichaParaRemover);
            this.revalidate();
            this.repaint();
        }
    }

    /**
     * Genera los grupos basándose en el estado visual actual del tablero.
     */
    public List<GrupoDTO> generarGruposDesdeCeldas() {
        FichaUI[][] celdasTemporales = new FichaUI[filas][columnas];
        for (FichaUI ficha : fichasEnTablero.values()) {
            Point celda = calcularCeldaParaPunto(ficha.getLocation());
            if (celda.y < filas && celda.x < columnas) {
                celdasTemporales[celda.y][celda.x] = ficha;
            }
        }

        List<GrupoDTO> gruposEncontrados = new ArrayList<>();
        boolean[][] visitadas = new boolean[filas][columnas];

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                if (celdasTemporales[r][c] != null && !visitadas[r][c]) {
                    List<FichaUI> grupoActual = new ArrayList<>();
                    int columnaActual = c;

                    while (columnaActual < columnas && celdasTemporales[r][columnaActual] != null) {
                        grupoActual.add(celdasTemporales[r][columnaActual]);
                        visitadas[r][columnaActual] = true;
                        columnaActual++;
                    }

                    if (!grupoActual.isEmpty()) {
                        List<FichaJuegoDTO> fichasDTO = new ArrayList<>();
                        for (FichaUI fichaUI : grupoActual) {
                            Point celda = calcularCeldaParaPunto(fichaUI.getLocation());
                            fichasDTO.add(new FichaJuegoDTO(fichaUI.getIdFicha(), fichaUI.getNumero(),
                                    fichaUI.getColor(), fichaUI.isComodin(), celda.y, celda.x));
                        }
                        gruposEncontrados.add(new GrupoDTO("No establecido", fichasDTO.size(), fichasDTO, r, c, true));

                    }
                }
            }
        }
        return gruposEncontrados;
    }

    /**
     * Guarda la disposición actual de las fichas como el nuevo estado "válido".
     * Se debe llamar después de una jugada exitosa.
     */
    public void guardarEstadoVisualValido() {
        // Limpiamos el estado anterior
        fichasEnTableroValidas.clear();
        posicionesValidas.clear();

        // Clonamos el estado actual
        for (Map.Entry<Integer, FichaUI> entry : fichasEnTablero.entrySet()) {
            fichasEnTableroValidas.put(entry.getKey(), entry.getValue());
            posicionesValidas.put(entry.getKey(), (Point) entry.getValue().getLocation().clone());
        }
    }

    /**
     * Restaura visualmente el tablero a la última disposición guardada. Esta
     * versión es completamente autónoma y reconstruye los feedbacks basándose
     * en su propio estado visual guardado, sin consultar al modelo.
     */
    public void revertirCambiosVisuales() {
        removeAll();
        fichasEnTablero.clear();

        for (Map.Entry<Integer, FichaUI> entry : fichasEnTableroValidas.entrySet()) {
            Integer idFicha = entry.getKey();
            FichaUI ficha = entry.getValue();
            Point pos = posicionesValidas.get(idFicha);

            add(ficha);
            if (pos != null) {
                ficha.setLocation(pos);
            }
            fichasEnTablero.put(idFicha, ficha);
        }

        List<GrupoDTO> gruposRestaurados = generarGruposDesdeCeldas();

        for (GrupoDTO grupo : gruposRestaurados) {
            grupo.setTipo("Valido");
        }

        if (gruposRestaurados != null) {
            for (GrupoDTO grupoDTO : gruposRestaurados) {
                if (grupoDTO.getFichasGrupo().isEmpty()) {
                    continue;
                }

                int primeraFichaId = grupoDTO.getFichasGrupo().get(0).getIdFicha();
                Point posFicha = posicionesValidas.get(primeraFichaId);

                if (posFicha != null) {
                    Point celdaAncla = calcularCeldaParaPunto(posFicha);
                    dibujarFeedbackParaGrupo(grupoDTO, celdaAncla);
                }
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Sincroniza el tablero visual con el estado del Modelo. Esta versión
     * respeta las posiciones elegidas por el jugador, reposiciona los grupos y
     * actualiza los feedbacks visuales.
     */
    public void repintarTablero(boolean esJugadaFinal) {
        JuegoDTO juego = modelo.getTablero();
        if (juego == null) {
            return;
        }
        List<GrupoDTO> gruposDelModelo = juego.getGruposEnTablero();

        Set<Integer> idsValidosDelModelo = new HashSet<>();
        if (gruposDelModelo != null) {
            for (GrupoDTO grupo : gruposDelModelo) {
                for (FichaJuegoDTO ficha : grupo.getFichasGrupo()) {
                    idsValidosDelModelo.add(ficha.getIdFicha());
                }
            }
        }

        Iterator<Map.Entry<Integer, FichaUI>> iter = fichasEnTablero.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, FichaUI> entry = iter.next();
            if (!idsValidosDelModelo.contains(entry.getKey())) {
                this.remove(entry.getValue());
                iter.remove();
            }
        }

        for (Component c : getComponents()) {
            if (c instanceof JPanel && !(c instanceof FichaUI)) {
                remove(c);
            }
        }

        if (gruposDelModelo != null) {
            for (GrupoDTO grupoDTO : gruposDelModelo) {
                if (grupoDTO.getFichasGrupo().isEmpty()) {
                    continue;
                }

                Point celdaAncla = new Point(grupoDTO.getColumna(), grupoDTO.getFila());

                // Reposicionar las fichas
                for (int i = 0; i < grupoDTO.getFichasGrupo().size(); i++) {
                    FichaJuegoDTO fichaDTO = grupoDTO.getFichasGrupo().get(i);
                    FichaUI fichaActualUI = fichasEnTablero.get(fichaDTO.getIdFicha());

                    if (fichaActualUI == null) {
                        fichaActualUI = new FichaUI(fichaDTO.getIdFicha(), fichaDTO.getNumeroFicha(),
                                fichaDTO.getColor(), fichaDTO.isComodin(), control, vista);
                        fichasEnTablero.put(fichaDTO.getIdFicha(), fichaActualUI);
                        this.add(fichaActualUI);
                    }
                    centrarYPosicionarFicha(fichaActualUI, celdaAncla.y, celdaAncla.x + i);
                }

                dibujarFeedbackParaGrupo(grupoDTO, celdaAncla);
            }
        }

        revalidate();
        repaint();

        if (esJugadaFinal) {
            boolean todaLaJugadaEsValida = true;
            if (gruposDelModelo != null) {
                for (GrupoDTO grupo : gruposDelModelo) {
                    if ("Invalido".equals(grupo.getTipo())) {
                        todaLaJugadaEsValida = false;
                        break;
                    }
                }
            }

            if (todaLaJugadaEsValida) {
                guardarEstadoVisualValido();
            } else {
            }
        }
    }

    /**
     * Metodo que dibuja una linea de color dependiendo el estado del grupo.
     *
     * @param grupo grupo sobre el que se pintara la linea
     * @param celdaInicio
     */
    private void dibujarFeedbackParaGrupo(GrupoDTO grupo, Point celdaInicio) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;
        int numFichas = grupo.getFichasGrupo().size();

        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(null);
        feedbackPanel.setSize(numFichas * anchoCelda, altoCelda);
        feedbackPanel.setLocation(celdaInicio.x * anchoCelda, celdaInicio.y * altoCelda);
        feedbackPanel.setOpaque(false);

        Color borderColor;
        switch (grupo.getTipo()) {
            case "Invalido":
                borderColor = Color.RED;
                break;
            case "Temporal":
                borderColor = Color.YELLOW.darker();
                break;
            default:
                borderColor = Color.GREEN.darker();
                break;
        }
        feedbackPanel.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        this.add(feedbackPanel);
        this.setComponentZOrder(feedbackPanel, getComponentCount() - 1); // Ponerlo al fondo
    }

    /**
     * Metodo que calcula una celda en el TableroUI dependiendo de la posicion
     * en pixeles que cayo la ficha.
     *
     * @param puntoEnPixeles donde cayo la ficha
     * @return un punto de pixeles donde pintar
     */
    private Point calcularCeldaParaPunto(Point puntoEnPixeles) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;
        int col = Math.max(0, Math.min(puntoEnPixeles.x / anchoCelda, columnas - 1));
        int fila = Math.max(0, Math.min(puntoEnPixeles.y / altoCelda, filas - 1));
        return new Point(col, fila);
    }

    /**
     * Metodo que agarra la ficha y la centra dentro del panel dependiendo de la
     * fila y columna.
     *
     * @param ficha ficha a pintar
     * @param fila fila donde pintar la ficha
     * @param columna columna donde pintar la ficha
     */
    private void centrarYPosicionarFicha(FichaUI ficha, int fila, int columna) {
        int anchoCelda = getWidth() / columnas;
        int altoCelda = getHeight() / filas;
        int x = (columna * anchoCelda) + (anchoCelda - ficha.getWidth()) / 2;
        int y = (fila * altoCelda) + (altoCelda - ficha.getHeight()) / 2;
        ficha.setLocation(x, y);
    }

    public Map<Integer, FichaUI> getFichasEnTableroValidas() {
        return fichasEnTableroValidas;
    }

    public Map<Integer, FichaUI> getFichasEnTablero() {
        return fichasEnTablero;
    }
}
