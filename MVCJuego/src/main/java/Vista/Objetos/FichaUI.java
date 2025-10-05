package Vista.Objetos;

import Controlador.Controlador;
import DTO.GrupoDTO;
import Vista.VistaTablero;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FichaUI extends JPanel {

    private int idFicha;
    private int numero;
    private Color color;
    private boolean comodin;
    private VistaTablero vista;
    private Controlador control;
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;

    private JComponent glassPane;
    private Point glassPaneOffset;

    public enum Origen {
        MANO, TABLERO
    }

    public FichaUI(int idFicha, int numero, Color color, boolean comodin,
            Controlador controlador, VistaTablero vista) {
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
            Controlador controlador, Point originalLocation, VistaTablero vista) {
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

            @Override
            /**
             * Este metodo determina donde fue soltada la ficha (en el
             * tablero, en la mano o fuera de ambos).
             * En base a donde se haya soltado realiza las acciones correspondientes, como colocarla en una nueva celda,
             * devolverla a su posicion original o regresar la ficha desde el tablero a la mano. 
             */
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
                    // Si la ficha ya estaba en el tablero, primero la quitamos de su celda vieja.
                    if (origen == Origen.TABLERO) {
                        panelTablero.removerFicha(FichaUI.this.idFicha);

                    }

                    // Intentamos colocar la ficha en una nueva celda.
                    boolean colocada = panelTablero.colocarFichaEnCelda(FichaUI.this, dropPoint);

                    if (colocada) {
                        origen = Origen.TABLERO;

                        List<GrupoDTO> gruposColocados = panelTablero.generarGruposDesdeCeldas();
                        control.colocarFicha(gruposColocados);
                    } else {
                        // Si no habia espacio, la devolvemos a su origen (la mano).
                        devolverFichaAlOrigen();
                    }
                } else if (dentroDeMano) {
                    // Solo tiene sentido hacer esto si la ficha venia del tablero
                    if (origen == Origen.TABLERO) {
                        TableroUI tablero = vista.getPanelTablero();
                        Map<Integer, FichaUI> fichasValidadas = tablero.getFichasEnTableroValidas();

                        if (fichasValidadas.containsValue(FichaUI.this)) {
                            devolverFichaAlOrigen();
                        } else {
                            control.regresarFichaAMano(FichaUI.this.idFicha);
                            List<GrupoDTO> gruposColocados = panelTablero.generarGruposDesdeCeldas();
                            control.colocarFicha(gruposColocados);
                        }

                    } else {
                        /*si su origen no es tablero entonces es mano por lo que se debe
                        quedar donde mismo*/
                        devolverFichaAlOrigen();//para que se repinte
                    }

                } else {
                    // se solto en cualquier otro lado
                    // La devolvemos a su panel y posicion originales.
                    devolverFichaAlOrigen();
                }

                panelTablero.revalidate();
                panelTablero.repaint();
                panelMano.revalidate();
                panelMano.repaint();
            }

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

    // --- Getters y Setters ---
    public int getIdFicha() {
        return idFicha;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public int getNumero() {
        return numero;
    }

    public Color getColor() {
        return color;
    }

    public boolean isComodin() {
        return comodin;
    }
}
