package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import Vista.VistaTablero;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FichaUI extends JPanel {

    private int idFicha;
    private int numero;
    private Color color;
    private boolean comodin;
    private VistaTablero vista;
    private int mouseX, mouseY;
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;
    private Controlador controlador;

    public enum Origen {
        MANO, TABLERO
    }
    
    public FichaUI(int idFicha, int numero, Color color, boolean comodin,
            Controlador controlador, VistaTablero vista) {
        this.vista = vista;
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
        this.controlador = controlador;
        setSize(25, 40);
        setPreferredSize(new Dimension(25, 40));
        setOpaque(false);
        initDrag();
    }

    public FichaUI(int idFicha, int numero, Color color, boolean comodin,
            Controlador controlador, Point originalLocation, VistaTablero vista) {
        this.vista = vista;
        this.originalLocation = originalLocation;
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
        this.controlador = controlador;
        setSize(25, 40);
        setPreferredSize(new Dimension(25, 40));
        setOpaque(false);
        initDrag();
    }

    private void initDrag() {
        MouseAdapter ma = new MouseAdapter() {
            private JComponent glassPane;
            private Point glassPaneOffset;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();

                originalParent = (JPanel) getParent();
                originalLocation = getLocation();

                // Activar GlassPane
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(FichaUI.this);
                glassPane = (JComponent) frame.getGlassPane();
                glassPane.setVisible(true);
                glassPane.setLayout(null);

                // Convertir posición a GlassPane
                Point locOnGlass = SwingUtilities.convertPoint(FichaUI.this, 0, 0, glassPane);
                glassPaneOffset = new Point(mouseX, mouseY);
                setLocation(locOnGlass);

                // Remover del panel original y agregar al GlassPane
                originalParent.remove(FichaUI.this);
                glassPane.add(FichaUI.this);
                glassPane.revalidate();
                glassPane.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point glassPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), glassPane);
                setLocation(glassPoint.x - glassPaneOffset.x, glassPoint.y - glassPaneOffset.y);
                glassPane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                TableroUI panelTablero = vista.getPanelTablero();
                Point releasePoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelTablero);

                // Quitar del GlassPane si lo estás usando
                if (glassPane != null) {
                    glassPane.remove(FichaUI.this);
                    glassPane.setVisible(false);
                }

                // Si se soltó dentro del tablero
                if (releasePoint.x >= 0 && releasePoint.y >= 0
                        && releasePoint.x <= panelTablero.getWidth()
                        && releasePoint.y <= panelTablero.getHeight()) {

                    // Remover de la celda anterior si existía
                    if (origen == Origen.TABLERO) {
                        panelTablero.removerFicha(FichaUI.this.idFicha);
                    }

                    // Intentar colocar la ficha en la celda libre más cercana
                    boolean colocada = panelTablero.colocarFichaEnCelda(FichaUI.this, releasePoint);

                    if (colocada) {
                        origen = Origen.TABLERO;

                        // Notificar al controlador
                        FichaJuegoDTO fichaDTO = new FichaJuegoDTO();
                        fichaDTO.setIdFicha(idFicha);
                        fichaDTO.setNumeroFicha(numero);
                        fichaDTO.setColor(color);
                        fichaDTO.setComodin(comodin);
                        controlador.fichaSoltada(fichaDTO, getX(), getY());

                    } else {
                        // Si no hay celda libre, devolver a la mano
                        setLocation(originalLocation);
                        originalParent.add(FichaUI.this);
                        originalParent.setComponentZOrder(FichaUI.this, 0);
                        originalParent.revalidate();
                        originalParent.repaint();
                        origen = Origen.MANO;
                    }

                } else {
                    // Si se soltó fuera del tablero, regresar a la mano
                    if (origen == Origen.TABLERO) {
                        panelTablero.removerFicha(FichaUI.this.idFicha);
                    }
                    setLocation(originalLocation);
                    originalParent.add(FichaUI.this);
                    originalParent.setComponentZOrder(FichaUI.this, 0);
                    originalParent.revalidate();
                    originalParent.repaint();
                    origen = Origen.MANO;
                }
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

        // Texto centrado
        FontMetrics fm = g.getFontMetrics();
        String texto = comodin ? "★" : String.valueOf(numero);
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 2;
        g.drawString(texto, x, y);
    }

    // Getters y setters
    public int getIdFicha() {
        return idFicha;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public Origen getOrigen() {
        return origen;
    }
}
