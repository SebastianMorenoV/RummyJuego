package Vista.Objetos;

/**
 *
 * @author moren
 */
import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import Vista.VistaTablero;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class FichaUI extends JPanel {

    private int idFicha;
    private int numero;
    private Color color;
    private boolean comodin;
    private int originalX, originalY;

    public enum Origen {
        MANO, TABLERO
    }
    private Origen origen;

    private int mouseX, mouseY; // para arrastre
    private JPanel originalParent;

    public FichaUI(int idFicha, int numero, Color color, boolean comodin, Controlador controlador, VistaTablero vista) {
        this.idFicha = idFicha;
        this.numero = numero;
        this.color = color;
        this.comodin = comodin;
        setSize(20, 50);
        setPreferredSize(new Dimension(20, 50));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                originalParent = (JPanel) getParent();
                originalX = getX(); // posición original dentro del panel
                originalY = getY();
                getParent().setComponentZOrder(FichaUI.this, 0); // Traer al frente
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JPanel panelTablero = vista.getPanelTablero();

                // Convertir la posición relativa al panelTablero
                java.awt.Point punto = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), panelTablero);
                System.out.println("Soltaste la ficha en x: " + punto.x + " y: " + punto.y);

                // Verificar si la ficha quedó dentro del panel
                if (punto.x >= 0 && punto.y >= 0
                        && punto.x <= panelTablero.getWidth()
                        && punto.y <= panelTablero.getHeight()) {

                    // Crear DTO de ficha
                    FichaJuegoDTO ficha = new FichaJuegoDTO();
                    ficha.setIdFicha(idFicha);
                    ficha.setNumeroFicha(numero);
                    ficha.setColor(color);
                    ficha.setComodin(comodin);

                    // Avisar al controlador que se soltó en una posición válida
                    controlador.fichaSoltada(ficha, punto.x, punto.y);

                } else {

                    // opcional: devolver la ficha a su posición original
                    System.out.println("Ficha soltada fuera del tablero. Regresando a su posición original.");
                    setLocation(originalX, originalY);
                    getParent().repaint(); // repintar para reflejar el cambio
                }
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = getX() + e.getX() - mouseX;
                int y = getY() + e.getY() - mouseY;
                setLocation(x, y);
                getParent().repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g.setColor(Color.WHITE);
        g.drawString(comodin ? "★" : String.valueOf(numero), getWidth() / 2 - 5, getHeight() / 2 + 5);
    }

    // Getters si necesitas pasar info al controlador
    public int getNumero() {
        return numero;
    }

    public Color getColor() {
        return color;
    }

    public boolean isComodin() {
        return comodin;
    }

    public void setOrigen(Origen origen) {
        this.origen = origen;
    }

    public Origen getOrigen() {
        return origen;
    }
}
