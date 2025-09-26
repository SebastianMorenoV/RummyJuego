package Vista.Objetos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Sebastian Moreno
 */
public class GrupoUI extends JPanel {

    // Lista de las fichas que se van a dibujar
    private List<FichaUI> fichas;
    // Dimensiones constantes para cada ficha
    private final int FICHA_ANCHO = 25;
    private final int FICHA_ALTO = 45;

    public GrupoUI() {
        this.fichas = new java.util.ArrayList<>();
        // Usamos un FlowLayout para que las fichas se acomoden una al lado de la otra.
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        setOpaque(false); // Hacemos el panel del grupo transparente
    }

    public List<FichaUI> getFichas() {
        return fichas;
    }

    public void setFichas(List<FichaUI> nuevasFichas) {
        // 1. Limpiar componentes y lista anteriores
        this.removeAll();
        this.fichas.clear();

        // 2. Agregar las nuevas fichas
        for (FichaUI ficha : nuevasFichas) {
            this.fichas.add(ficha);
            this.add(ficha); // ¡Añadimos el componente FichaUI directamente al panel!
        }

        // 3. Actualizar el tamaño y repintar
        actualizarTamano();
        this.revalidate();
        this.repaint();
    }

    // Método para añadir una sola ficha (útil para el drag-and-drop)
    // En la clase Vista.Objetos.GrupoUI.java
// El método ahora recibe el punto donde se soltó la ficha
    public void agregarFicha(FichaUI ficha, Point puntoDeSoltado) {
        // Por defecto, se añade al final
        int indiceDeInsercion = this.fichas.size();

        // Calculamos en qué posición (índice) debería insertarse la nueva ficha
        for (int i = 0; i < this.fichas.size(); i++) {
            FichaUI fichaActual = this.fichas.get(i);
            // Si el punto X está a la izquierda del centro de una ficha existente,
            // encontramos nuestro punto de inserción.
            if (puntoDeSoltado.x < fichaActual.getX() + (fichaActual.getWidth() / 2)) {
                indiceDeInsercion = i;
                break;
            }
        }

        // Añadimos la ficha a la lista de datos y al panel en el índice correcto
        this.fichas.add(indiceDeInsercion, ficha);
        this.add(ficha, indiceDeInsercion);

        actualizarTamano();
        revalidate();
        repaint();
    }

// Mantenemos el método antiguo por si se necesita en otro lugar, 
// pero lo hacemos llamar al nuevo.
    public void agregarFicha(FichaUI ficha) {
        agregarFicha(ficha, new Point(Integer.MAX_VALUE, 0)); // Añade al final
    }
    
    
    // En la clase Vista.Objetos.GrupoUI.java

    public void removerFicha(FichaUI fichaARemover) {
        // Elimina la ficha del panel visual
        this.remove(fichaARemover);

        // Elimina la ficha de la lista de datos interna
        this.fichas.removeIf(fichaEnLista -> fichaEnLista.getIdFicha() == fichaARemover.getIdFicha());

        // Actualiza el tamaño preferido del panel
        actualizarTamano();

        // ¡LÍNEAS CLAVE! Forzamos al propio grupo a actualizar su layout y redibujarse.
        revalidate();
        repaint();
    }

    private void actualizarTamano() {
        if (fichas.isEmpty()) {
            // Si no hay fichas, el grupo no debe ocupar espacio
            setPreferredSize(new Dimension(0, 0));
        } else {
            int anchoTotal = fichas.size() * (FICHA_ANCHO); // Un pequeño espacio entre fichas
            setPreferredSize(new Dimension(anchoTotal, FICHA_ALTO));
        }
    }

    // El paintComponent ahora solo dibuja el borde, las fichas se dibujan solas.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fichas != null && !fichas.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
