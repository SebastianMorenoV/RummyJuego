
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista.Objetos;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Clase de presentación para dibujar un jugador en la interfaz de usuario. El
 * avatar es reemplazado por una imagen proporcionada como un array de bytes.
 *
 * @author Sebastian Moreno
 */
public class JugadorUI extends JPanel {

    private BufferedImage avatarImage;
    private int fichasRestantes;
    private String nombreJugador;

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

        setPreferredSize(new Dimension(200, 200));
        setOpaque(false);
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
        g2d.setColor(new Color(150, 120, 90));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(cardX, cardY, cardWidth, cardHeight, cornerRadius, cornerRadius);

        int avatarSize = (int) (cardWidth * 0.5);
        int avatarX = cardX + (cardWidth - avatarSize) / 2;
        int avatarY = cardY + (int) (cardHeight * 0.08);

        // Si el objeto ya esta decodificado, solo lo dibujamos
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
        g2d.drawString(chipsText, chipBoxX + (chipBoxWidth - chipsWidth) / 2, chipBoxY + fm.getAscent() + (chipBoxHeight - fm.getHeight()) / 2);
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

    /**
     * Metodo para settear el numero de fichas restantes de un jugador. El
     * numero de fichas restante sale debajo del nombre de un jugador
     *
     * @param fichasRestantes
     */
    public void setFichasRestantes(int fichasRestantes) {
        this.fichasRestantes = fichasRestantes;
        repaint();
    }

    /**
     * Metodo para settear la imagen o "avatar" de un jugador.
     *
     * @param imagenAvatarBytes
     */
    public void setImagenAvatar(byte[] imagenAvatarBytes) {

        // Vuelve a decodificar la imagen si se actualiza
        if (imagenAvatarBytes != null && imagenAvatarBytes.length > 0) {
            try {
                this.avatarImage = ImageIO.read(new ByteArrayInputStream(imagenAvatarBytes));
            } catch (IOException e) {
                this.avatarImage = null;
                System.err.println("Error al decodificar la nueva imagen del avatar: "
                        + e.getMessage());
            }
        }
        repaint();
    }

    /**
     * Metodo para settear el nombre de un jugador.
     *
     * @param nombreJugador
     */
    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        repaint();
    }
}
