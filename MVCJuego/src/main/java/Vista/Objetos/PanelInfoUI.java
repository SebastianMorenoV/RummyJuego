package Vista.Objetos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Panel informativo COMPACTO que muestra el estado de la partida.
 */
public class PanelInfoUI extends JPanel {

    private JLabel lblTurnoTitulo;
    private JLabel lblTurnoValor;
    private JLabel lblSiguienteTitulo;
    private JLabel lblSiguienteValor;
    private JLabel lblTiempoTitulo;
    private JLabel lblTiempoValor;

    private Timer cronometro;
    private long segundosTranscurridos = 0;
    private boolean corriendo = false;

    public PanelInfoUI() {
        setLayout(null); 
        setOpaque(false); 
        initComponentes();
        initCronometro();
    }

    private void initComponentes() {
        // FUENTES MÁS PEQUEÑAS
        Font fontTitulo = new Font("Segoe UI", Font.BOLD, 11); // Antes 14
        Font fontValor = new Font("Segoe UI", Font.PLAIN, 12);  // Antes 16
        Color colorTexto = Color.WHITE;
        Color colorResaltado = new Color(255, 215, 0); // Dorado

        // REDUCCIÓN DE ESPACIADO VERTICAL (coord Y)
        int xPad = 10;
        int yStart = 8;
        int yGapTitulo = 15; // Espacio entre título y valor
        int yGapSeccion = 35; // Espacio entre secciones

        // --- TURNO ACTUAL ---
        lblTurnoTitulo = crearLabel("TURNO ACTUAL:", fontTitulo, colorResaltado, xPad, yStart);
        lblTurnoValor = crearLabel("Esperando...", fontValor, colorTexto, xPad, yStart + yGapTitulo);

        // --- SIGUIENTE TURNO ---
        int ySig = yStart + yGapSeccion;
        lblSiguienteTitulo = crearLabel("SIGUIENTE:", fontTitulo, colorResaltado, xPad, ySig);
        lblSiguienteValor = crearLabel("-", fontValor, colorTexto, xPad, ySig + yGapTitulo);

        // --- TIEMPO DE JUEGO ---
        int yTime = ySig + yGapSeccion;
        lblTiempoTitulo = crearLabel("TIEMPO:", fontTitulo, colorResaltado, xPad, yTime);
        lblTiempoValor = crearLabel("00:00", fontValor, colorTexto, xPad, yTime + yGapTitulo);
    }
    
    // Ajustamos el ancho del label para que quepa en el panel más chico
    private JLabel crearLabel(String texto, Font fuente, Color color, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        // Ancho reducido a 110
        lbl.setBounds(x, y, 110, 20); 
        add(lbl);
        return lbl;
    }

    private void initCronometro() {
        cronometro = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundosTranscurridos++;
                actualizarLabelTiempo();
            }
        });
    }

    public void iniciarCronometro() {
        if (!corriendo) {
            segundosTranscurridos = 0;
            cronometro.start();
            corriendo = true;
        }
    }
    
    public void detenerCronometro() {
        if (corriendo) {
            cronometro.stop();
            corriendo = false;
        }
    }

    private void actualizarLabelTiempo() {
        long minutos = segundosTranscurridos / 60;
        long segundos = segundosTranscurridos % 60;
        String tiempoStr = String.format("%02d:%02d", minutos, segundos);
        lblTiempoValor.setText(tiempoStr);
    }

    public void actualizarTurnos(String jugadorActual, String siguienteJugador) {
        // Truncar nombres muy largos para que no rompan el diseño pequeño
        if (jugadorActual.length() > 12) jugadorActual = jugadorActual.substring(0, 10) + "..";
        if (siguienteJugador.length() > 12) siguienteJugador = siguienteJugador.substring(0, 10) + "..";
        
        lblTurnoValor.setText(jugadorActual);
        lblSiguienteValor.setText(siguienteJugador);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 180));
        // Radio de borde más pequeño (15 en vez de 20)
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new java.awt.BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }
}