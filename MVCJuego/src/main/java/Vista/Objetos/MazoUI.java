package Vista.Objetos;

import Controlador.Controlador;
import contratos.controladoresMVC.iControlEjercerTurno;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazoUI extends JPanel {

    private String numeroFichasRestantes;
    private iControlEjercerTurno control;
    
    // Variables para efectos visuales
    private boolean isHovered = false;
    private Timer animacionTimer;
    private double anguloRotacion = 0;
    private double velocidadRotacion = 0.08; // Un poco más rápido para que se note
    private int direccion = 1;
    
    // Contadores para el ciclo de "Recordatorio"
    private int tickCounter = 0;
    private static final int TIMER_DELAY = 40; // Actualizar cada 40ms
    // 40ms * 75 = 3000ms (3 segundos de espera)
    private static final int TIEMPO_ESPERA = 75; 
    // 40ms * 20 = 800ms (0.8 segundos de baile)
    private static final int TIEMPO_BAILE = 20;  

    public MazoUI(String numeroFichasRestantes, iControlEjercerTurno control) {
        this.control = control;
        this.numeroFichasRestantes = numeroFichasRestantes;
        setPreferredSize(new Dimension(70, 90)); 
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        initListeners();
        initAnimacion();
    }

    public MazoUI() {
    }

    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isEnabled()) {
                    reproducir("error.wav");
                    return;
                }
                reproducir("carta2.wav");
                control.pasarTurno();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    // Cuando entras con el mouse, reiniciamos para que esté quieto y listo para clic
                    tickCounter = 0; 
                    anguloRotacion = 0;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    // Timer INTELIGENTE con intervalos
    private void initAnimacion() {
        animacionTimer = new Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEnabled()) {
                    // Si el usuario tiene el mouse encima, NO bailamos (sería molesto para clickear)
                    if (isHovered) {
                        if (anguloRotacion != 0) {
                            anguloRotacion = 0;
                            repaint();
                        }
                        return;
                    }

                    tickCounter++;

                    // Lógica del ciclo: Espera -> Baile -> Reset
                    if (tickCounter < TIEMPO_ESPERA) {
                        // FASE 1: ESPERA (Quieto)
                        if (anguloRotacion != 0) {
                            anguloRotacion = 0; // Asegurar que quede recto
                            repaint();
                        }
                    } else if (tickCounter < (TIEMPO_ESPERA + TIEMPO_BAILE)) {
                        // FASE 2: BAILE (Recordatorio)
                        anguloRotacion += (velocidadRotacion * direccion);
                        // Límite de inclinación (0.08 radianes es sutil pero visible)
                        if (Math.abs(anguloRotacion) > 0.08) { 
                            direccion *= -1; 
                        }
                        repaint();
                    } else {
                        // Fin del ciclo, reiniciar contadores
                        tickCounter = 0;
                    }
                } else {
                    // Si no es mi turno, resetear todo
                    if (anguloRotacion != 0) {
                        anguloRotacion = 0;
                        repaint();
                    }
                    tickCounter = 0; 
                }
            }
        });
        animacionTimer.start();
    }

    private void reproducir(String nombreArchivo) {
        new Thread(() -> {
            try {
                java.net.URL url = getClass().getResource("/sonidos/" + nombreArchivo);
                if (url != null) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(url));
                    clip.start();
                }
            } catch (Exception e) { }
        }).start();
    }

   
    public void setNumeroFichasRestantes(String numeroFichasRestantes) {
        this.numeroFichasRestantes = numeroFichasRestantes;
        repaint();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(enabled) {
            if(!animacionTimer.isRunning()) animacionTimer.start();
        } else {
            anguloRotacion = 0; 
            tickCounter = 0;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); 
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        
        AffineTransform oldTransform = g2.getTransform();
        
        // Solo rotamos si es necesario
        if (isEnabled() && anguloRotacion != 0) {
            g2.rotate(anguloRotacion, cx, cy);
        }

        // --- DIBUJO ---
        // Sombra / Borde
        g2.setColor(new Color(80, 50, 20)); 
        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 15, 15);
        
        // Cuerpo
        g2.setColor(new Color(243, 206, 177)); 
        g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);

        // Interior
        int margen = 6;
        g2.setColor(new Color(250, 241, 232)); 
        g2.fillRoundRect(margen, margen, getWidth() - 4 - (margen*2), getHeight() - 4 - (margen*2), 10, 10);

        // Texto
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22)); 

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - 4 - fm.stringWidth(numeroFichasRestantes)) / 2;
        int y = (getHeight() - 4 - fm.getHeight()) / 2 + fm.getAscent() - 2;
        g2.drawString(numeroFichasRestantes, x, y);

        // Efecto Hover
        if (isHovered && isEnabled()) {
            g2.setColor(new Color(255, 215, 0)); // Dorado
            g2.setStroke(new BasicStroke(3f));   
            g2.drawRoundRect(1, 1, getWidth()-6, getHeight()-6, 15, 15);
            
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillRoundRect(margen, margen, getWidth() - 4 - (margen*2), (getHeight()/2), 10, 10);
        }

        g2.setTransform(oldTransform);
        g2.dispose();
    }

    public void actualizarNumeroFichas(int nuevoNumero) {
        this.numeroFichasRestantes = String.valueOf(nuevoNumero);
        repaint();
    }
}