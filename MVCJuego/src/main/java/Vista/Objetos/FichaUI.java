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
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;
    private Controlador controlador;
    private VistaTablero vista;

    private JComponent glassPane;
    private Point glassPaneOffset;

    public enum Origen {
        MANO, TABLERO
    }

    public FichaUI(int idFicha, int numero, Color color, boolean comodin, Controlador controlador, VistaTablero vista) {
        this.vista = vista;
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
        this.controlador = controlador;
        setSize(28, 45); // Ajustado a la altura estándar que manejamos
        setPreferredSize(new Dimension(28, 45));
        setOpaque(false);
        initDrag();
    }

    private void initDrag() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                originalParent = (JPanel) getParent();
                originalLocation = getLocation();
                glassPaneOffset = e.getPoint();

                // **ORDEN CORREGIDO Y CRUCIAL:**
                // 1. PRIMERO obtenemos la referencia a la ventana y al glassPane.
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(FichaUI.this);
                if (frame == null) {
                    return;
                }
                glassPane = (JComponent) frame.getGlassPane();

                // 2. AHORA SÍ, si la ficha venía del tablero, le decimos que la recoja (y posiblemente divida el grupo).
                if (origen == Origen.TABLERO) {
                    vista.getPanelTablero().recogerFichaDeGrupo(FichaUI.this);
                }

                // 3. Movemos la ficha al glassPane para arrastrarla libremente.
                Point locOnGlass = SwingUtilities.convertPoint(originalParent, getLocation(), glassPane);
                setLocation(locOnGlass);

                // El 'originalParent' podría ya no existir si el grupo se dividió, 
                // pero la ficha ya no está en él, así que esta línea es segura.
                originalParent.remove(FichaUI.this);

                glassPane.add(FichaUI.this);
                glassPane.setVisible(true);
                glassPane.revalidate();
                glassPane.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (glassPane == null) {
                    return;
                }
                Point glassPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), glassPane);
                setLocation(glassPoint.x - glassPaneOffset.x, glassPoint.y - glassPaneOffset.y);

                // Lógica de resaltado
                TableroUI panelTablero = vista.getPanelTablero();
                Point puntoEnTablero = SwingUtilities.convertPoint(FichaUI.this, new Point(0, 0), panelTablero);
                boolean dentro = panelTablero.contains(puntoEnTablero);
                if (dentro) {
                    panelTablero.resaltarCeldaEn(puntoEnTablero);
                } else {
                    panelTablero.limpiarResaltado();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (glassPane == null) {
                    return;
                }
                TableroUI panelTablero = vista.getPanelTablero();

                panelTablero.limpiarResaltado();
                Point dropPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelTablero);

                glassPane.remove(FichaUI.this);
                glassPane.setVisible(false);

                // LÓGICA DE LÍMITES CORREGIDA
                boolean dentroDelTablero = dropPoint.x >= 0 && dropPoint.y >= 0
                        && dropPoint.x < panelTablero.getWidth()
                        && dropPoint.y < panelTablero.getHeight();

                if (dentroDelTablero) {
                    origen = Origen.TABLERO;
                    // LLAMADA CORREGIDA PARA FUSIONAR GRUPOS
                    Component dropTarget = panelTablero.getComponentAt(dropPoint);
                    panelTablero.procesarFichaSoltada(FichaUI.this, dropTarget, dropPoint);
                } else {
                    // Devolver a la mano
                    setLocation(originalLocation);
                    originalParent.add(FichaUI.this);
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
        FontMetrics fm = g.getFontMetrics();
        String texto = comodin ? "★" : String.valueOf(numero);
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, x, y);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public int getIdFicha() {
        return idFicha;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public Origen getOrigen() {
        return origen;
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
    // </editor-fold>
}
