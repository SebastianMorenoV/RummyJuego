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
    private int mouseX, mouseY;
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;
    private Controlador controlador;

    //no escencial:
    private VistaTablero vista;

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

                // --- ORDEN CORREGIDO ---
                // 1. PRIMERO, obtenemos una referencia a la ventana y al glassPane.
                //    Mientras la ficha aún está en su panel, sabemos que tiene un "ancestro" JFrame.
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(FichaUI.this);

                // Agregamos una comprobación para evitar cualquier error.
                if (frame == null) {
                    System.err.println("Error: No se pudo encontrar el JFrame contenedor.");
                    return;
                }
                glassPane = (JComponent) frame.getGlassPane();

                // 2. AHORA SÍ, si la ficha venía del tablero, le decimos que la recoja.
                //    En este punto, ya no importa que la ficha se "desconecte" de la ventana,
                //    porque ya guardamos la referencia al glassPane.
                if (origen == Origen.TABLERO) {
                    TableroUI panelTablero = vista.getPanelTablero();
                    panelTablero.recogerFichaDeGrupo(FichaUI.this);
                }

                // 3. El resto del código para mover la ficha al glassPane se queda igual.
                glassPane.setVisible(true);
                glassPane.setLayout(null);
                Point locOnGlass = SwingUtilities.convertPoint(FichaUI.this, 0, 0, glassPane);
                glassPaneOffset = new Point(mouseX, mouseY);

                // Como la ficha ya fue removida por recogerFichaDeGrupo si venía del tablero,
                // esta línea solo es necesaria si venía de la mano. No hace daño dejarla.
                originalParent.remove(FichaUI.this);

                setLocation(locOnGlass);
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

                if (glassPane != null) {
                    glassPane.remove(FichaUI.this);
                    glassPane.setVisible(false);
                }

                // --- LÓGICA DE LÍMITES CORREGIDA ---
                // Comprobamos si el punto de soltado está dentro de los límites 0,0 y el ancho/alto del panel.
                boolean dentroDelTablero = releasePoint.x >= 0 && releasePoint.y >= 0
                        && releasePoint.x < panelTablero.getWidth()
                        && releasePoint.y < panelTablero.getHeight();

                if (dentroDelTablero) {
                    origen = Origen.TABLERO;
                    panelTablero.procesarFichaSoltada(FichaUI.this, releasePoint);
                } else {
                    // Devolver a la mano si se suelta fuera
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
