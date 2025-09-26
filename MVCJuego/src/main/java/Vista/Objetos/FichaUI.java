package Vista.Objetos;

import Controlador.Controlador;
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
    private Point originalLocation;
    private JPanel originalParent;
    private Origen origen;
    
    // El glassPane se usa para un arrastre fluido por toda la ventana.
    private JComponent glassPane;
    private Point glassPaneOffset;

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
        // El controlador ya no es necesario aquí, pero lo mantenemos por si acaso.
        
        // Usamos las dimensiones que acordamos para un encaje perfecto.
        setSize(28, 45);
        setPreferredSize(new Dimension(28, 45));
        setOpaque(false);
        initDrag();
    }
    
    // Este constructor con Point ya no es necesario con la nueva lógica, pero se puede mantener.
    public FichaUI(int idFicha, int numero, Color color, boolean comodin, 
                   Controlador controlador, Point originalLocation, VistaTablero vista) {
        this(idFicha, numero, color, comodin, controlador, vista);
        this.originalLocation = originalLocation;
    }

    private void initDrag() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                originalParent = (JPanel) getParent();
                originalLocation = getLocation();
                glassPaneOffset = e.getPoint();

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(FichaUI.this);
                if (frame == null) return;
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
                if (glassPane == null) return;
                Point glassPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), glassPane);
                setLocation(glassPoint.x - glassPaneOffset.x, glassPoint.y - glassPaneOffset.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (glassPane == null) return;
                TableroUI panelTablero = vista.getPanelTablero();
                Point dropPoint = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelTablero);
                
                glassPane.remove(FichaUI.this);
                glassPane.setVisible(false);
                
                boolean dentroDelTablero = dropPoint.x >= 0 && dropPoint.y >= 0 &&
                                            dropPoint.x < panelTablero.getWidth() && 
                                            dropPoint.y < panelTablero.getHeight();

                if (dentroDelTablero) {
                    // Si la ficha ya estaba en el tablero, primero la quitamos de su celda vieja.
                    if (origen == Origen.TABLERO) {
                        panelTablero.removerFicha(FichaUI.this.idFicha);
                    }
                    
                    // Intentamos colocar la ficha en una nueva celda.
                    boolean colocada = panelTablero.colocarFichaEnCelda(FichaUI.this, dropPoint);

                    if (colocada) {
                        // Si se pudo colocar, su nuevo origen es el tablero.
                        origen = Origen.TABLERO;
                    } else {
                        // Si no había espacio, la devolvemos a su origen (la mano).
                        devolverFichaAlOrigen();
                    }
                } else {
                    // Si se soltó fuera del tablero, también la devolvemos.
                    devolverFichaAlOrigen();
                }
                
                panelTablero.revalidate();
                panelTablero.repaint();
            }
            
            private void devolverFichaAlOrigen() {
                setLocation(originalLocation);
                originalParent.add(FichaUI.this);
                originalParent.revalidate();
                originalParent.repaint();
                origen = Origen.MANO;
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
    public int getIdFicha() { return idFicha; }
    public void setOrigen(Origen origen) { this.origen = origen; }
    public int getNumero() { return numero; }
    public Color getColor() { return color; }
    public boolean isComodin() { return comodin; }
}