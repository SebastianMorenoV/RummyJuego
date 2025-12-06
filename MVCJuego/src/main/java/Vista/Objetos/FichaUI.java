package Vista.Objetos;

import Controlador.Controlador;
import DTO.GrupoDTO;
import Vista.VistaTablero;
import contratos.controladoresMVC.iControlEjercerTurno;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Representa gráficamente una ficha y permite arrastrarla entre mano y tablero.
 * Maneja drag & drop y notifica al controlador según acciones del jugador.
 *
 * @author chris
 */
public class FichaUI extends JPanel {

    private int idFicha;
    private int numero;
    private Color color;
    private boolean comodin;
    private VistaTablero vista;
    private iControlEjercerTurno control;
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;

    private JComponent glassPane;
    private Point glassPaneOffset;

    public enum Origen {
        MANO, TABLERO
    }

    public FichaUI(int idFicha, int numero, Color color, boolean comodin,
           iControlEjercerTurno controlador, VistaTablero vista) {
        this.control = controlador;
        this.vista = vista;
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;

        setSize(28, 45);
        setPreferredSize(new Dimension(28, 45));
        setOpaque(false);
        initDrag();
    }

    public FichaUI(int idFicha, int numero, Color color, boolean comodin,
            iControlEjercerTurno controlador, Point originalLocation, VistaTablero vista) {
        this.control = controlador;
        this.vista = vista;
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
        this.originalLocation = originalLocation;
    }

    private void initDrag() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getParent() != null && !getParent().isEnabled()) {
                    return;
                }
                originalParent = (JPanel) getParent();
                originalLocation = getLocation();
                glassPaneOffset = e.getPoint();

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(FichaUI.this);
                if (frame == null) {
                    return;
                }
                glassPane = (JComponent) frame.getGlassPane();

                Point locOnGlass = SwingUtilities.convertPoint(originalParent, getLocation(), glassPane);
                setLocation(locOnGlass);

                originalParent.remove(FichaUI.this);
                glassPane.add(FichaUI.this);
                glassPane.setVisible(true);
                glassPane.revalidate();
                glassPane.repaint();
            }

            /**
             * Metodo para arrastrar las fichasUI que estan en la mano. se
             * pueden colocar de la mano al tablero o viceversa
             *
             * @param e
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                if (getParent() != null && !getParent().isEnabled()) {
                    return;
                }
                if (glassPane == null) {
                    return;
                }

                Point glassPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), glassPane);
                setLocation(glassPoint.x - glassPaneOffset.x, glassPoint.y - glassPaneOffset.y);
            }

            /**
             * Este metodo determina donde fue soltada la ficha (en el tablero,
             * en la mano o fuera de ambos). En base a donde se haya soltado
             * realiza las acciones correspondientes, como colocarla en una
             * nueva celda, devolverla a su posicion original o regresar la
             * ficha desde el tablero a la mano.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getParent() != null && !getParent().isEnabled()) {
                    return;
                }
                if (glassPane == null) {
                    return;
                }

                TableroUI panelTablero = vista.getPanelTablero();
                ManoUI panelMano = vista.getPanelMano();

                Point dropPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelTablero);
                Point dropPointEnMano = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelMano);

                glassPane.remove(FichaUI.this);
                glassPane.setVisible(false);

                boolean dentroDelTablero = dropPoint.x >= 0 && dropPoint.y >= 0
                        && dropPoint.x < panelTablero.getWidth()
                        && dropPoint.y < panelTablero.getHeight();

                boolean dentroDeMano = dropPointEnMano.x >= 0 && dropPointEnMano.y >= 0
                        && dropPointEnMano.x < panelMano.getWidth()
                        && dropPointEnMano.y < panelMano.getHeight();

                if (dentroDelTablero) {

                    if (origen == Origen.TABLERO) {
                        panelTablero.removerFicha(FichaUI.this.idFicha);

                    }

                    boolean colocada = panelTablero.colocarFichaEnCelda(FichaUI.this, dropPoint);

                    if (colocada) {
                        origen = Origen.TABLERO;

                        List<GrupoDTO> gruposColocados = panelTablero.generarGruposDesdeCeldas();
                        control.colocarFicha(gruposColocados);
                    } else {
                        devolverFichaAlOrigen();
                    }
                } else if (dentroDeMano) {

                    if (origen == Origen.TABLERO) {
                        TableroUI tablero = vista.getPanelTablero();
                        Map<Integer, FichaUI> fichasValidadas = tablero.getFichasEnTableroValidas();

                        if (fichasValidadas.containsValue(FichaUI.this)) {
                            devolverFichaAlOrigen();
                        } else {
                              control.regresarFichaAMano(FichaUI.this.idFicha);
                        }

                    } else {

                        devolverFichaAlOrigen();
                    }

                } else {

                    devolverFichaAlOrigen();
                }

                panelTablero.revalidate();
                panelTablero.repaint();
                panelMano.revalidate();
                panelMano.repaint();
            }

            /**
             * Metodo que devuelve la ficha a su origen. si la ficha fue movida
             * a un lugar en el cual no puede colocarse o que necesita volver
             * por una jugada invalida regresa a su lugar de origen que es donde
             * estaba colocada.
             */
            private void devolverFichaAlOrigen() {
                setLocation(originalLocation);
                originalParent.add(FichaUI.this);
                originalParent.revalidate();
                originalParent.repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    /**
     * Dibuja la ficha (número o estrella si es comodín)
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String texto = comodin ? "★" : String.valueOf(numero);

        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g.drawString(texto, x, y);
    }

    public int getIdFicha() {
        return idFicha;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public Color getColor() {
        return color;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isComodin() {
        return comodin;
    }
}
