/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
class PanelRedondo extends JPanel {

    private int radio;

    public PanelRedondo(int radio) {
        this.radio = radio;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Convertimos a Graphics2D para poder activar el suavizado
        Graphics2D g2 = (Graphics2D) g;

        // Activamos ANTIALIASING para que los bordes no se vean pixelados ("fatales")
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Usamos el color de fondo configurado en el panel
        g2.setColor(getBackground());

        // Dibujamos el rectángulo redondeado relleno
        // (x, y, ancho, alto, radioHorizontal, radioVertical)
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);

        // borde extra:
        // g2.setColor(java.awt.Color.BLACK);
        // g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radio, radio);
        super.paintComponent(g); // Pinta los componentes hijos (como los números 1, 2, 3, 4)
    }
}
