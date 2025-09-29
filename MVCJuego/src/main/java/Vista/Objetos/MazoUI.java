/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista.Objetos;

import Controlador.Controlador;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Sebastian Moreno
 */
public class MazoUI extends JPanel implements MouseListener {

    List<FichaUI> fichasMazo;
    String numeroFichasRestantes;
    Controlador control;

    
    public MazoUI(String numeroFichasRestantes, Controlador control) {
        this.control = control;
        this.numeroFichasRestantes = numeroFichasRestantes;
        setPreferredSize(new Dimension(40, 70));

        // Agrega el MouseListener al componente en el constructor
        addMouseListener(this);
    }

    public MazoUI() {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        control.pasarTurno();
    }

 
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void setNumeroFichasRestantes(String numeroFichasRestantes) {
        this.numeroFichasRestantes = numeroFichasRestantes;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo marrón (redondeado, más grande)
        g.setColor(new Color(156, 113, 17));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Dimensiones del rectángulo blanco interior
        int margenX = 10;
        int margenY = 15;
        int anchoBlanco = getWidth() - 2 * margenX;
        int altoBlanco = getHeight() - 2 * margenY;
        int posX = margenX;
        int posY = margenY;

        // Rectángulo blanco centrado
        g.setColor(new Color(239, 220, 168));
        g.fillRoundRect(posX, posY, anchoBlanco, altoBlanco, 20, 20);

        // Número centrado dentro del rectángulo blanco en negrita
        g.setColor(Color.BLACK);
        Font original = g.getFont();
        Font bold = original.deriveFont(Font.BOLD, original.getSize() + 6);
        g.setFont(bold);

        FontMetrics fm = g.getFontMetrics();
        int x = posX + (anchoBlanco - fm.stringWidth(numeroFichasRestantes)) / 2;
        int y = posY + (altoBlanco + fm.getAscent()) / 2 - 2;
        g.drawString(numeroFichasRestantes, x, y);

        // Restaurar la fuente original
        g.setFont(original);
    }

    public void actualizarNumeroFichas(int nuevoNumero) {
        this.numeroFichasRestantes = String.valueOf(nuevoNumero);
        // Repinta el componente para mostrar el nuevo número
        repaint();
    }
}
