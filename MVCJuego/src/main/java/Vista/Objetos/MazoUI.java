/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista.Objetos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Sebastian Moreno
 */
public class MazoUI extends JPanel {

    List<FichaUI> fichasMazo;
    String numeroFichasRestantes;

    public MazoUI(String numeroFichasRestantes) {
        this.numeroFichasRestantes = numeroFichasRestantes;
        setPreferredSize(new Dimension(40, 70));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo marrón (redondeado, más grande)
        g.setColor(new Color(156, 113, 17));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Dimensiones del rectángulo blanco interior
        int margenX = 10; // más margen para que el marrón se vea más grande
        int margenY = 15;
        int anchoBlanco = getWidth() - 2 * margenX;
        int altoBlanco = getHeight() - 2 * margenY;
        int posX = margenX;
        int posY = margenY;

        // Rectángulo blanco centrado
        g.setColor(new Color(239, 220, 168));
        g.fillRoundRect(posX, posY, anchoBlanco, altoBlanco,20,20);

        // Número centrado dentro del rectángulo blanco en negrita
        g.setColor(Color.BLACK);
        Font original = g.getFont();
        Font bold = original.deriveFont(Font.BOLD, original.getSize() + 6); // un poco más grande y bold
        g.setFont(bold);

        FontMetrics fm = g.getFontMetrics();
        int x = posX + (anchoBlanco - fm.stringWidth(numeroFichasRestantes)) / 2;
        int y = posY + (altoBlanco + fm.getAscent()) / 2 - 2; // centrado vertical
        g.drawString(numeroFichasRestantes, x, y);

        // Restaurar la fuente original
        g.setFont(original);
    }
}
