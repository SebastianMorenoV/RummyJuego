package Vista.Objetos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
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
 * Incluye animación de "respiración", sistema de CHAT HD estilo iOS.
 *
 * @author Sebastian Moreno
 */
public class JugadorUI extends JPanel {

    private BufferedImage avatarImage;
    private int fichasRestantes;
    private String nombreJugador;
    private boolean esTuTurno = false;

    // Animación respiración
    private Timer timerAnimacion;
    private float alpha = 1.0f;
    private boolean desvaneciendo = true;

    // --- COMPONENTES DE CHAT ---
    private JPanel btnChat; // Cambiamos JLabel por JPanel para dibujar bonito
    private JLabel lblBurbuja;
    private Timer burbujaTimer;
    private boolean esMiJugador = false;
    private ActionListener onEnviarMensaje;

    // Fuente Emojis HD (Intenta cargar la de Apple si existe, si no la de Windows)
    private Font fontEmojis;

    public JugadorUI(String nombreJugador, int fichasRestantes, byte[] imagenAvatarBytes) {
        this.nombreJugador = nombreJugador;
        this.fichasRestantes = fichasRestantes;

        // Intentar cargar fuente de emojis del sistema
        // Segoe UI Emoji es la de Windows color, Apple Color Emoji la de Mac.
        this.fontEmojis = new Font("Segoe UI Emoji", Font.PLAIN, 24);
        if (this.fontEmojis.getFamily().equals("Dialog")) {
             // Fallback si no encuentra Segoe
            this.fontEmojis = new Font("SansSerif", Font.PLAIN, 24);
        }

        setLayout(null); // Layout absoluto para control total

        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
            } catch (IOException e) {
                System.err.println("Error avatar: " + e.getMessage());
            }
        }

        setPreferredSize(new Dimension(100, 100));
        setOpaque(false);

        initAnimacion();
        initComponentesChat();
    }

    private void initComponentesChat() {
        // 1. BOTÓN DE CHAT ESTILO iOS (Dibujado a mano, no texto)
        btnChat = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // No llamamos super para que sea transparente real
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);

                // Círculo de fondo (Azul iMessage o Blanco limpio)
                g2.setColor(new Color(255, 255, 255)); 
                g2.fillOval(0, 0, getWidth() - 4, getHeight() - 4);
                
                // Borde suave
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(0, 0, getWidth() - 4, getHeight() - 4);

                // Icono de "burbuja de texto" adentro (3 puntitos)
                g2.setColor(new Color(50, 50, 50)); // Gris oscuro
                int size = 4;
                int gap = 3;
                int startX = (getWidth() - 4) / 2 - size - gap;
                int centerY = (getHeight() - 4) / 2;
                
                // Dibujar 3 puntos
                g2.fillOval(startX, centerY - size/2, size, size);
                g2.fillOval(startX + size + gap, centerY - size/2, size, size);
                g2.fillOval(startX + (size + gap)*2, centerY - size/2, size, size);
            }
        };
        
        btnChat.setOpaque(false);
        btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChat.setBounds(70, 0, 32, 32); // Posición esquina superior derecha
        btnChat.setVisible(false);

        // Menú de mensajes con FUENTE GRANDE
        JPopupMenu menuChat = new JPopupMenu();
        // Fondo blanco y borde redondeado (truco visual simple)
        menuChat.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1));
        menuChat.setBackground(Color.WHITE);

        String[] mensajes = {
            "👋 ¡Hola!", "🔥 ¡Buena jugada!", "⏳ ¡Apúrate!", 
            "🍀 Qué suerte...", "🤣 Jajaja", "🤝 Buena partida", "😭", "😡", "😎"
        };

        for (String msg : mensajes) {
            JMenuItem item = new JMenuItem(msg);
            item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); // Fuente legible
            item.setBackground(Color.WHITE);
            item.addActionListener(e -> {
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
            // Efecto Hover (crecer un poquito)
            @Override
            public void mouseEntered(MouseEvent e) {
               btnChat.setBounds(69, -1, 34, 34); // Crece 2px
               btnChat.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
               btnChat.setBounds(70, 0, 32, 32); // Vuelve
               btnChat.repaint();
            }
        });

        add(btnChat);

        // 2. BURBUJA DE MENSAJE (Estilo iOS)
        lblBurbuja = new JLabel("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dibujar burbuja redondeada
                g2.setColor(new Color(255, 255, 255)); // Fondo blanco
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20); // Bordes muy redondos
                
                // Borde sutil
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                // Dibujar texto (llamamos a super pero cuidado con el fondo)
                super.paintComponent(g);
            }
        };
        
        lblBurbuja.setOpaque(false); // Para que se vea nuestra pintura personalizada
        lblBurbuja.setForeground(Color.BLACK);
        // Usamos la fuente EMOJI grande para que se vean bien
        lblBurbuja.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); 
        lblBurbuja.setHorizontalAlignment(SwingConstants.CENTER);
        lblBurbuja.setVisible(false);
        add(lblBurbuja);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println("[JugadorUI] Burbuja: " + mensaje);
        lblBurbuja.setText(mensaje);

        // Calcular tamaño dinámico basado en la fuente Emoji
        FontMetrics fm = getFontMetrics(lblBurbuja.getFont());
        int textoAncho = fm.stringWidth(mensaje);
        int padding = 20; // Espacio extra a los lados
        int ancho = Math.max(60, textoAncho + padding); // Mínimo 60px
        int alto = 35; // Altura fija cómoda

        // Posicionar centrado arriba del avatar
        int xPos = (getWidth() - ancho) / 2;
        // Evitar que se salga del panel
        if (xPos < -10) xPos = -10; 
        
        // Animación de "pop" (Aparece un poco más abajo y sube)
        lblBurbuja.setBounds(xPos, 10, ancho, alto); 
        lblBurbuja.setVisible(true);
        setComponentZOrder(lblBurbuja, 0);
        repaint();

        // Timer para ocultar
        if (burbujaTimer != null && burbujaTimer.isRunning()) burbujaTimer.stop();

        burbujaTimer = new Timer(4000, e -> {
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

        // Fondo de tarjeta
        g2d.setColor(new Color(245, 245, 250)); // Blanco humo moderno
        g2d.fillRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        // --- BORDE ANIMADO ---
        if (this.esTuTurno) {
            int alphaInt = Math.max(0, Math.min(255, (int) (alpha * 255)));
            // Verde neón suave
            g2d.setColor(new Color(50, 205, 50, alphaInt));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);
        }

        // Borde fino gris siempre visible
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        // Avatar
        int avatarSize = (int) (cardWidth * 0.55); // Un poco más grande
        int avatarX = cardX + (cardWidth - avatarSize) / 2;
        int avatarY = cardY + (int) (cardHeight * 0.10);

        if (avatarImage != null) {
            g2d.setClip(new Ellipse2D.Double(avatarX, avatarY, avatarSize, avatarSize));
            g2d.drawImage(avatarImage, avatarX, avatarY, avatarSize, avatarSize, this);
            g2d.setClip(null);
        } else {
            drawDefaultAvatar(g2d, avatarX, avatarY, avatarSize);
        }
        
        // Borde del Avatar
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(avatarX, avatarY, avatarSize, avatarSize);

        // Nombre
        g2d.setColor(new Color(60, 60, 60));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(nombreJugador);
        int nameY = avatarY + avatarSize + 15;
        g2d.drawString(nombreJugador, cardX + (cardWidth - nameWidth) / 2, nameY);

        // Caja de Fichas (Estilo píldora)
        int chipBoxWidth = 40;
        int chipBoxHeight = 20;
        int chipBoxX = cardX + (cardWidth - chipBoxWidth) / 2;
        int chipBoxY = nameY + 5;

        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRoundRect(chipBoxX, chipBoxY, chipBoxWidth, chipBoxHeight, 20, 20);

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String chipsText = String.valueOf(fichasRestantes);
        fm = g2d.getFontMetrics();
        int chipsWidth = fm.stringWidth(chipsText);
        g2d.drawString(chipsText, chipBoxX + (chipBoxWidth - chipsWidth) / 2, chipBoxY + 14);
    }

    private void drawDefaultAvatar(Graphics2D g2d, int avatarX, int avatarY, int avatarSize) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(avatarX, avatarY, avatarSize, avatarSize);
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