package Vista.Objetos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class GrupoUI extends JPanel {

    private List<FichaUI> fichas;
    private final int FICHA_ANCHO = 28;
    private final int FICHA_ALTO = 45;
    private final int ESPACIO_ENTRE_FICHAS = 2;

    public GrupoUI() {
        this.fichas = new ArrayList<>();
        setLayout(null);
        setOpaque(false);
    }

    /**
     * Este método es llamado por Swing para ordenar los componentes. Aquí es
     * donde implementamos nuestra lógica de layout manual y perfecta.
     */
    @Override
    public void doLayout() {
        super.doLayout();
        int x = 0; 
        for (Component comp : getComponents()) {
            if (comp instanceof FichaUI) {
                comp.setBounds(x, 0, FICHA_ANCHO, FICHA_ALTO);
                x += FICHA_ANCHO + ESPACIO_ENTRE_FICHAS; // Avanzamos la posición para la siguiente.
            }
        }
    }

  
    public void setFichas(List<FichaUI> nuevasFichas) {
        this.removeAll();
        this.fichas.clear();
        for (FichaUI ficha : nuevasFichas) {
            this.fichas.add(ficha);
            this.add(ficha);
        }
        actualizarTamano();
        revalidate();
        repaint();
    }

    public void agregarFicha(FichaUI ficha, Point puntoDeSoltado) {
        int indiceDeInsercion = this.fichas.size();
        int xRelativo = puntoDeSoltado.x;
        int posicionDeCorte = (FICHA_ANCHO + ESPACIO_ENTRE_FICHAS) / 2;

        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            if (xRelativo < comp.getX() + posicionDeCorte) {
                indiceDeInsercion = i;
                break;
            }
        }

        this.fichas.add(indiceDeInsercion, ficha);
        this.add(ficha, indiceDeInsercion);
        actualizarTamano();
        revalidate();
        repaint();
    }

    public void agregarFicha(FichaUI ficha) {
        agregarFicha(ficha, new Point(Integer.MAX_VALUE, 0));
    }

    public void removerFicha(FichaUI fichaARemover) {
        this.remove(fichaARemover);
        this.fichas.removeIf(fichaEnLista -> fichaEnLista.getIdFicha() == fichaARemover.getIdFicha());
        actualizarTamano();
        revalidate();
        repaint();
    }

    private void actualizarTamano() {
        if (fichas.isEmpty()) {
            setPreferredSize(new Dimension(0, 0));
        } else {
            int anchoTotal = (fichas.size() * FICHA_ANCHO) + Math.max(0, (fichas.size() - 1) * ESPACIO_ENTRE_FICHAS);
            setPreferredSize(new Dimension(anchoTotal, FICHA_ALTO));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fichas != null && !fichas.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    /**
     * Devuelve el indice (posicion) de una ficha en la lista interna.
     * @param ficha La ficha a buscar.
     * @return El índice de la ficha, o -1 si no se encuentra.
     */
    public int getIndiceDeFicha(FichaUI ficha) {
        for (int i = 0; i < fichas.size(); i++) {
            if (fichas.get(i).getIdFicha() == ficha.getIdFicha()) {
                return i;
            }
        }
        return -1;
    }

    public List<FichaUI> getFichas() {
        return fichas;
    }

}
