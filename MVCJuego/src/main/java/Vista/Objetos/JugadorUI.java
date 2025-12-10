package Vista.Objetos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;

/**
 * Clase de presentación para dibujar un jugador en la interfaz de usuario.
 * Incluye animación de "respiración" Y CHAT.
 *
 * @author Sebastian Moreno
 */
public class JugadorUI extends JPanel {

    private BufferedImage avatarImage;
    private int fichasRestantes;
    private String nombreJugador;
    private boolean esTuTurno = false;

    // Variables para la animación de turno (Respiración)
    private Timer timerAnimacion;
    private float alpha = 1.0f; 
    private boolean desvaneciendo = true;

    // --- CHAT COMPONENTS ---
    private JLabel btnChat; 
    private JLabel lblBurbuja; 
    private Timer burbujaTimer; 
    private boolean esMiJugador = false; 
    private ActionListener onEnviarMensaje;

    public JugadorUI(String nombreJugador, int fichasRestantes, byte[] imagenAvatarBytes) {
        this.nombreJugador = nombreJugador;
        this.fichasRestantes = fichasRestantes;

        // IMPORTANTE: Layout nulo para poder mover la burbuja y el botón libremente
        setLayout(null); 

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

        initAnimacion(); // Tu animación original
        initComponentesChat(); // El chat nuevo
    }

    private void initComponentesChat() {
        // 1. Botón de Chat (Invisible por defecto)
        btnChat = new JLabel("💬");
        btnChat.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnChat.setForeground(Color.WHITE);
        btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChat.setHorizontalAlignment(SwingConstants.CENTER);
        btnChat.setBounds(75, 5, 30, 30); // Esquina superior derecha
        btnChat.setVisible(false); 
        
        // Menú desplegable
        JPopupMenu menuChat = new JPopupMenu();
        String[] mensajes = {
            "¡Hola!", "¡Buena jugada!", "¡Apúrate!", 
            "Qué suerte...", "Jajaja", "Buena partida", "😢", "😡", "😎"
        };
        
        for (String msg : mensajes) {
            JMenuItem item = new JMenuItem(msg);
            item.addActionListener(e -> {
                System.out.println("[JugadorUI] Enviando mensaje: " + msg);
                if (onEnviarMensaje != null) {
                    onEnviarMensaje.actionPerformed(new ActionEvent(this, 1, msg));
                }
            });
            menuChat.add(item);
        }

        btnChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menuChat.show(btnChat, 0, btnChat.getHeight());
            }
        });
        
        add(btnChat);

        // 2. Burbuja de Mensaje
        lblBurbuja = new JLabel("");
        lblBurbuja.setOpaque(true);
        lblBurbuja.setBackground(Color.WHITE);
        lblBurbuja.setForeground(Color.BLACK);
        lblBurbuja.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblBurbuja.setHorizontalAlignment(SwingConstants.CENTER);
        lblBurbuja.setBorder(new javax.swing.border.LineBorder(Color.BLACK, 1, true));
        lblBurbuja.setVisible(false);
        add(lblBurbuja); 
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println("[JugadorUI] Mostrando burbuja para " + nombreJugador + ": " + mensaje);
        lblBurbuja.setText(mensaje);
        
        FontMetrics fm = getFontMetrics(lblBurbuja.getFont());
        int ancho = fm.stringWidth(mensaje) + 20;
        int alto = 25;
        
        int xPos = (getWidth() - ancho) / 2;
        if (xPos < 0) xPos = 0;
        
        lblBurbuja.setBounds(xPos, 0, ancho, alto);
        lblBurbuja.setVisible(true);
        setComponentZOrder(lblBurbuja, 0); 
        repaint();

        if (burbujaTimer != null && burbujaTimer.isRunning()) burbujaTimer.stop();
        
        burbujaTimer = new Timer(3000, e -> {
            lblBurbuja.setVisible(false);
            repaint();
        });
        burbujaTimer.setRepeats(false);
        burbujaTimer.start();
    }

    public void setEsMiJugador(boolean esMiJugador, ActionListener listenerEnvio) {
        this.esMiJugador = esMiJugador;
        this.onEnviarMensaje = listenerEnvio;
        btnChat.setVisible(esMiJugador);
        System.out.println("[JugadorUI] setEsMiJugador: " + esMiJugador + " (" + nombreJugador + ")");
    }

    private void initAnimacion() {
        timerAnimacion = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (desvaneciendo) {
                    alpha -= 0.05f;
                    if (alpha <= 0.3f) {
                        alpha = 0.3f;
                        desvaneciendo = false;
                    }
                } else {
                    alpha += 0.05f;
                    if (alpha >= 1.0f) {
                        alpha = 1.0f;
                        desvaneciendo = true;
                    }
                }
                repaint();
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

        g2d.setColor(new Color(232, 240, 251));
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        // --- LÓGICA DE BORDE ANIMADO ---
        if (this.esTuTurno) {
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 255)));
            g2d.setColor(new Color(0, 255, 0, alphaInt));
            g2d.setStroke(new BasicStroke(6));
            g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

            g2d.setColor(new Color(0, 200, 0));
            g2d.setStroke(new BasicStroke(2));
        } else {
            g2d.setColor(new Color(22, 98, 98));
            g2d.setStroke(new BasicStroke(3));
        }

        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

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
        g2d.fillOval(avatarX + (avatarSize - headSize) / 2, avatarY + (int) (avatarSize * 0.15), headSize, headSize);
        int bodyHeight = (int) (avatarSize * 0.5);
        g2d.fillArc(avatarX + (int) (avatarSize * 0.1), avatarY + (int) (avatarSize * 0.5), (int) (avatarSize * 0.8), bodyHeight, 0, 180);
    }

    public void setAvatarBytes(byte[] imagenAvatarBytes) {
        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
                this.repaint();
            } catch (IOException e) { }
        }
    }

    public void setFichasRestantes(int fichasRestantes) {
        this.fichasRestantes = fichasRestantes;
        repaint();
    }
    public String getNombre() { return nombreJugador; }
    public void setEsTuTurno(boolean esTuTurno) {
        this.esTuTurno = esTuTurno;
        if (esTuTurno) { if (!timerAnimacion.isRunning()) timerAnimacion.start(); } 
        else { if (timerAnimacion.isRunning()) timerAnimacion.stop(); alpha = 1.0f; repaint(); }
    }
}