package Vista.Objetos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class ManoUI extends JPanel {

    private List<FichaUI> fichas; // lista de fichas dentro de la mano

    public ManoUI() {
        fichas = new ArrayList<>();
        setPreferredSize(new Dimension(870, 150)); // altura ajustada para la mano
        setOpaque(false); // dejamos transparente para que se vea el fondo marrón redondo
    }

    /**
     * Agrega una ficha a la mano y la posiciona automáticamente
     */
    public void agregarFicha(FichaUI ficha) {
        fichas.add(ficha);
        int separacion = 40; // separación entre fichas
        int x = 20 + fichas.size() * separacion; // posición horizontal
        int y = getHeight() / 2 - ficha.getHeight() / 2; // centrado vertical
        ficha.setLocation(x, y);
        this.add(ficha);
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fondo marrón redondeado
        g.setColor(new Color(156, 113, 17));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        // Línea horizontal en el medio
        g.setColor(Color.BLACK);
        int yLinea = getHeight() / 2;
        g.drawLine(10, yLinea, getWidth() - 10, yLinea);
    }
}
