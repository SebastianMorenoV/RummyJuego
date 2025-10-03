package Vista.Objetos;

import DTO.GrupoDTO;
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
        this.add(ficha);
        this.revalidate();
        this.repaint();
    }

    /**
     * Limpia todas las fichas de la mano
     */
    @Override
    public void removeAll() {
        fichas.clear();
        super.removeAll();
    }

    /**
     * Obtiene la cantidad de fichas en la mano
     *
     * @return
     */
    public int getCantidadFichas() {
        return fichas.size();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fondo marrón redondeado
        g.setColor(new Color(156, 113, 17));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
    }

    public void removerFicha(FichaUI ficha) {
        if (fichas.remove(ficha)) { // <-- PASO CLAVE: Remueve de la lista
            this.remove(ficha); // Remueve visualmente
            // Opcional: Reorganizar las fichas restantes si es necesario
            this.revalidate();
            this.repaint();
        }

    }

    public void limpiarMano() {
        this.removeAll(); // Limpia visualmente
        this.fichas.clear(); // Limpia la lista lógica
    }
}
