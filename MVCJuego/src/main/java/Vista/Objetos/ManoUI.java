package Vista.Objetos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class ManoUI extends JPanel {
    private List<FichaUI> fichas; // lista de fichas dentro de la mano
    private static final int FICHAS_PARA_SCROLL = 18;
    private static final int SEPARACION_FICHAS = 40;
    private static final int MARGEN_IZQUIERDO = 20;
    
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
        actualizarPosicionesFichas();
        this.add(ficha);
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Actualiza las posiciones de todas las fichas y ajusta el tamaño del panel
     */
    private void actualizarPosicionesFichas() {
        int anchoNecesario = MARGEN_IZQUIERDO + (fichas.size() * SEPARACION_FICHAS) + 50;
        
        // Si necesitamos más espacio, ajustamos el tamaño preferido del panel
        if (fichas.size() > FICHAS_PARA_SCROLL) {
            setPreferredSize(new Dimension(anchoNecesario, 150));
        } else {
            setPreferredSize(new Dimension(870, 150));
        }
        
        // Posicionamos cada ficha
        for (int i = 0; i < fichas.size(); i++) {
            FichaUI ficha = fichas.get(i);
            int x = MARGEN_IZQUIERDO + (i * SEPARACION_FICHAS);
            int y = getHeight() / 2 - ficha.getHeight() / 2;
            ficha.setLocation(x, y);
        }
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
}