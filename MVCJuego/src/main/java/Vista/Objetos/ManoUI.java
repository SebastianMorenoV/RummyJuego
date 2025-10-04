package Vista.Objetos;

import DTO.GrupoDTO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class ManoUI extends JPanel {

    private List<FichaUI> fichas; // lista de fichas dentro de la mano
    private static final int MARGEN_IZQUIERDO = 20;
    private static final int FICHAS_POR_FILA = 14;
    private static final int SEPARACION_HORIZONTAL = 38;
    private static final int ALTURA_FILA = 60;
    private static final int MARGEN_VERTICAL = 20;
    private static final int ANCHO_FICHA = 28;
    private static final int ALTO_FICHA = 45;

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
    
    /**
     * Este método es llamado por Swing para posicionar y ordenar todos los componentes (fichas dentro)
     * del contenedor (el panel ManoUI).
     * Aquí definimos la lógica de "wrapping" para colocar las fichas en filas sucesivas.
     */
    @Override
    public void doLayout() {
        super.doLayout();
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            int fila = i / FICHAS_POR_FILA;
            int columna = i % FICHAS_POR_FILA;
            int x = MARGEN_IZQUIERDO + (columna * SEPARACION_HORIZONTAL);
            int y = MARGEN_VERTICAL + (fila * ALTURA_FILA);
            comp.setBounds(x, y, ANCHO_FICHA, ALTO_FICHA);
        }
    }
    
    /**
     * Este método le dice al JScrollPane qué tan grande debe ser el panel.
     * El ancho es fijo, pero el alto crece a medida que se añaden filas.
     */
    @Override
    public Dimension getPreferredSize() {
        int numComponentes = getComponentCount();
        if (numComponentes == 0) {
            // Devuelve un tamaño mínimo para que el panel vacío sea visible
            return new Dimension(580, 120);
        }

        // El ancho siempre es el mismo, basado en 14 fichas por fila
        int panelWidth = MARGEN_IZQUIERDO + (FICHAS_POR_FILA * SEPARACION_HORIZONTAL);
        
        // El alto se calcula basado en cuántas filas se necesitan
        int numFilas = (int) Math.ceil((double) numComponentes / FICHAS_POR_FILA);
        int panelHeight = (numFilas * ALTURA_FILA) + (2 * MARGEN_VERTICAL);
        
        return new Dimension(panelWidth, panelHeight);
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
