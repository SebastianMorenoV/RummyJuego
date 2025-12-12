package Vista.Objetos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;

/**
 * Clase de presentación para dibujar un jugador en la interfaz de usuario. 
 * Incluye animación de "respiración" cuando es el turno.
 *
 * @author Sebastian Moreno
 */
public class JugadorUI extends JPanel {

    private BufferedImage avatarImage;
    private int fichasRestantes;
    private String nombreJugador;
    private boolean esTuTurno = false;

    // Variables para la animación
    private Timer timerAnimacion;
    private float alpha = 1.0f; // Opacidad actual (0.0 a 1.0)
    private boolean desvaneciendo = true; // Dirección de la animación

    public JugadorUI(String nombreJugador, int fichasRestantes, byte[] imagenAvatarBytes) {
        this.nombreJugador = nombreJugador;
        this.fichasRestantes = fichasRestantes;

        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
            } catch (IOException e) {
                this.avatarImage = null;
                System.err.println("Error al decodificar la imagen del avatar: " + e.getMessage());
            }
        }

        setPreferredSize(new Dimension(100, 100));
        setOpaque(false);
        
        initAnimacion(); // Inicializamos el timer
    }

    /**
     * Configura el timer para el efecto de respiración.
     */
    private void initAnimacion() {
        // Ejecuta cada 50ms (aprox 20 fps)
        timerAnimacion = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica de "Ping-Pong" para la opacidad
                if (desvaneciendo) {
                    alpha -= 0.05f; // Velocidad de desvanecimiento
                    if (alpha <= 0.3f) { // Límite inferior de transparencia
                        alpha = 0.3f;
                        desvaneciendo = false;
                    }
                } else {
                    alpha += 0.05f; // Velocidad de aparición
                    if (alpha >= 1.0f) { // Límite superior
                        alpha = 1.0f;
                        desvaneciendo = true;
                    }
                }
                repaint(); // Forzar repintado para ver el cambio
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int margin = 15;
        int cardX = margin;
        int cardY = margin;
        int cardWidth = panelWidth - (2 * margin);
        int cardHeight = panelHeight - (2 * margin);
        int cornerRadius = 25;

        g2d.setColor(new Color(210, 180, 140));
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        // --- LÓGICA DE BORDE ANIMADO ---
        if (this.esTuTurno) {
            // Creamos el color verde usando el Alpha dinámico
            // (R, G, B, Alpha) -> Alpha va de 0 a 255
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 255)));
            
            // Efecto de brillo: Dibujamos un borde semitransparente grueso y uno solido delgado
            g2d.setColor(new Color(0, 255, 0, alphaInt)); 
            g2d.setStroke(new BasicStroke(6)); // Borde grueso para el brillo
            g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);
            
            // Borde base sólido para que no desaparezca del todo
            g2d.setColor(new Color(0, 200, 0));
            g2d.setStroke(new BasicStroke(2));
            
        } else {
            g2d.setColor(new Color(150, 120, 90)); // Color original (café)
            g2d.setStroke(new BasicStroke(3)); // Grosor original
        }
        
        // Dibujar el borde final (o el café o el verde sólido interior)
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        // --- RESTO DEL DIBUJADO (Avatar, Texto, Fichas) ---
        int avatarSize = (int) (cardWidth * 0.5);
        int avatarX = cardX + (cardWidth - avatarSize) / 2;
        int avatarY = cardY + (int) (cardHeight * 0.08);

        if (avatarImage != null) {
            g2d.setClip(new Ellipse2D.Double(avatarX, avatarY, avatarSize, avatarSize));
            g2d.drawImage(avatarImage, avatarX, avatarY, avatarSize, avatarSize, this);
            g2d.setClip(null);
        } else {
            drawDefaultAvatar(g2d, avatarX, avatarY, avatarSize);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, (int) (cardWidth * 0.11)));
        FontMetrics fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(nombreJugador);
        int nameY = avatarY + avatarSize + (int) (cardHeight * 0.05);
        g2d.drawString(nombreJugador, cardX + (cardWidth - nameWidth) / 2, nameY);

        int chipBoxWidth = (int) (cardWidth * 0.35);
        int chipBoxHeight = (int) (cardHeight * 0.18);
        int chipBoxX = cardX + (cardWidth - chipBoxWidth) / 2;
        int chipBoxY = nameY + (int) (cardHeight * 0.04);
        int chipBoxCornerRadius = 10;

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(chipBoxX, chipBoxY, chipBoxWidth, chipBoxHeight, chipBoxCornerRadius, chipBoxCornerRadius);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(chipBoxX, chipBoxY, chipBoxWidth, chipBoxHeight, chipBoxCornerRadius, chipBoxCornerRadius);

        g2d.setFont(new Font("Arial", Font.BOLD, (int) (chipBoxHeight * 0.6)));
        String chipsText = String.valueOf(fichasRestantes);
        fm = g2d.getFontMetrics();
        int chipsWidth = fm.stringWidth(chipsText);
        g2d.drawString(chipsText, chipBoxX + (chipBoxWidth - chipsWidth)
                / 2, chipBoxY + fm.getAscent() + (chipBoxHeight - fm.getHeight()) / 2);
    }

    private void drawDefaultAvatar(Graphics2D g2d, int avatarX, int avatarY, int avatarSize) {
        g2d.setColor(Color.BLACK);
        g2d.fillOval(avatarX, avatarY, avatarSize, avatarSize);

        g2d.setColor(Color.WHITE);
        int headSize = (int) (avatarSize * 0.4);
        g2d.fillOval(avatarX + (avatarSize - headSize) / 2, avatarY
                + (int) (avatarSize * 0.15), headSize, headSize);

        int bodyHeight = (int) (avatarSize * 0.5);
        g2d.fillArc(avatarX + (int) (avatarSize * 0.1), avatarY
                + (int) (avatarSize * 0.5), (int) (avatarSize * 0.8), bodyHeight, 0, 180);
    }

    public void setAvatarBytes(byte[] imagenAvatarBytes) {
        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
                this.repaint();
            } catch (IOException e) {
                System.err.println("Error actualizando avatar en UI: " + e.getMessage());
            }
        }
    }

    public void setFichasRestantes(int fichasRestantes) {
        this.fichasRestantes = fichasRestantes;
        repaint();
    }

    public void setImagenAvatar(byte[] imagenAvatarBytes) {
        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
            } catch (IOException e) {
                this.avatarImage = null;
                System.err.println("Error al decodificar la nueva imagen del avatar: " + e.getMessage());
            }
        }
        repaint();
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        repaint();
    }

    /**
     * Activa o desactiva la animación de turno.
     * @param esTuTurno 
     */
    public void setEsTuTurno(boolean esTuTurno) {
        this.esTuTurno = esTuTurno;
        
        // Controlamos el timer para no gastar recursos si no es el turno
        if (esTuTurno) {
            if (!timerAnimacion.isRunning()) {
                timerAnimacion.start();
            }
        } else {
            if (timerAnimacion.isRunning()) {
                timerAnimacion.stop();
            }
            // Restauramos valores por defecto al terminar
            alpha = 1.0f;
            repaint();
        }
    }
}