package Vista.Objetos;

/**
 *
 * @author moren
 */
import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class FichaUI extends JPanel {

    private int numero;
    private Color color;
    private boolean comodin;

    private int mouseX, mouseY; // para arrastre
    private JPanel originalParent;

    public FichaUI(int numero, Color color, boolean comodin, Controlador controlador) {
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
                getParent().setComponentZOrder(FichaUI.this, 0); // Traer al frente
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Convertir la posición relativa al panelTablero
                java.awt.Point punto = SwingUtilities.convertPoint(FichaUI.this, e.getPoint(), originalParent.getParent());
                System.out.println("Soltaste la ficha en x: " + punto.x + " y: " + punto.y);

                int posicionX = punto.x;
                int posicionY = punto.y;

                FichaJuegoDTO ficha = new FichaJuegoDTO();
                ficha.setNumeroFicha(numero);
                ficha.setColor(color);
                ficha.setComodin(comodin);

                // Aquí puedes avisar al controlador
                controlador.fichaSoltada(ficha, punto.x, punto.y);
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
}
