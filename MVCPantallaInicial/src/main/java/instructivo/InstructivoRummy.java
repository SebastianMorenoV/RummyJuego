package instructivo; // Asegúrate que sea el mismo paquete que tu VistaLobby

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InstructivoRummy extends JFrame {

    private final Color COLOR_FONDO = new Color(0, 85, 95); 
    private final Color COLOR_TEXTO_AMARILLO = new Color(255, 200, 0); 
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_ROJO_BOTON = new Color(220, 0, 0); 

    private final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 24); 
    private final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 13);
    private final Font FUENTE_FICHA = new Font("SansSerif", Font.BOLD, 15);
    private final Font FUENTE_TEXTO = new Font("SansSerif", Font.PLAIN, 11); 
    
    private final Font FUENTE_REGLAS = new Font("SansSerif", Font.PLAIN, 15); 

    private int xMouse, yMouse;

    public InstructivoRummy() {
        setUndecorated(true);
        setTitle("¿Como se juega?");
        
        // --- CORRECCIÓN 1: Usar DISPOSE para cerrar solo esta ventana ---
        // Antes tenías EXIT_ON_CLOSE, que cierra toda la aplicación.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setSize(520, 340); 
        setLocationRelativeTo(null);
        
        setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(COLOR_FONDO);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
                
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3)); 
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 30, 30);
                
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnClose.setForeground(Color.WHITE);       
        btnClose.setBackground(COLOR_ROJO_BOTON);  
        btnClose.setFocusPainted(false);            
        btnClose.setBorderPainted(false);
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setPreferredSize(new Dimension(40, 30)); 
        
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // --- CORRECCIÓN 2: Usar dispose() en el botón X ---
        // Antes tenías System.exit(0), que mata todo el proceso.
        btnClose.addActionListener(e -> dispose());
        
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnContainer.setOpaque(false);
        btnContainer.add(btnClose);

        JPanel dummyPanel = new JPanel();
        dummyPanel.setPreferredSize(new Dimension(40, 30));
        dummyPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("¿Como se juega?", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(COLOR_TEXTO_BLANCO);

        headerPanel.add(dummyPanel, BorderLayout.WEST);
        headerPanel.add(lblTitulo, BorderLayout.CENTER);
        headerPanel.add(btnContainer, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                setLocation(x - xMouse, y - yMouse);
            }
        };
        mainPanel.addMouseListener(ma);
        mainPanel.addMouseMotionListener(ma);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        addLabel(leftPanel, "Jugadas validas:", COLOR_TEXTO_AMARILLO, FUENTE_SUBTITULO);

        addLabel(leftPanel, "Pares", COLOR_TEXTO_BLANCO, new Font("SansSerif", Font.BOLD, 11));
        JPanel panelPares = createTileGroupPanel(
                new TileData[]{new TileData("10", Color.RED), new TileData("10", Color.GREEN), new TileData("10", Color.BLUE)},
                "<html>Grupo valido solo<br>usando colores<br>diferentes.</html>"
        );
        leftPanel.add(panelPares);
        leftPanel.add(Box.createVerticalStrut(3));

        addLabel(leftPanel, "Escalera", COLOR_TEXTO_BLANCO, new Font("SansSerif", Font.BOLD, 11));
        JPanel panelEscalera = createTileGroupPanel(
                new TileData[]{new TileData("1", Color.GREEN), new TileData("2", Color.GREEN), new TileData("3", Color.GREEN)},
                "<html>Grupo valido solo<br>usando colores<br>iguales.</html>"
        );
        leftPanel.add(panelEscalera);
        leftPanel.add(Box.createVerticalStrut(3));

        addLabel(leftPanel, "Comodin:", COLOR_TEXTO_AMARILLO, FUENTE_SUBTITULO);
        JPanel panelComodin = createTileGroupPanel(
                new TileData[]{new TileData("★", Color.GRAY)},
                "<html>Sirve como remplazo<br>a cualquier ficha.</html>"
        );
        leftPanel.add(panelComodin);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.38; gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 8); 
        centerPanel.add(leftPanel, gbc);

        JPanel rightPanelContainer = new JPanel(new BorderLayout());
        rightPanelContainer.setOpaque(false);

        JPanel rulesBox = new JPanel();
        rulesBox.setLayout(new BoxLayout(rulesBox, BoxLayout.Y_AXIS));
        rulesBox.setBackground(Color.WHITE);
        rulesBox.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12)); 

        JLabel lblReglasTitulo = new JLabel("Reglas:");
        lblReglasTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblReglasTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblReglasTitulo.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        rulesBox.add(lblReglasTitulo);
        rulesBox.add(Box.createVerticalStrut(3));

        String htmlReglas = "<html><ul style='margin-top: 0px; margin-left:15px; padding-top:0px;'>"
                + "<li style='margin-bottom:5px'>Tu primera jugada debe ser mayor a 30 puntos utilizando fichas de tu mano.</li>"
                + "<li style='margin-bottom:5px'>Solo se pueden hacer Grupos de 3 fichas en adelante.</li>"
                + "<li>La escalera termina en el 13, no existe la ficha \"14\" en rummy.</li>"
                + "</ul></html>";

        JLabel lblReglasTexto = new JLabel(htmlReglas);
        lblReglasTexto.setFont(FUENTE_REGLAS);
        lblReglasTexto.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rulesBox.add(lblReglasTexto);
        rulesBox.add(Box.createVerticalGlue());

        rightPanelContainer.add(rulesBox, BorderLayout.CENTER); 
        rightPanelContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        gbc.gridx = 1; gbc.weightx = 0.62; 
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(rightPanelContainer, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel lblFooter = new JLabel("¡Gana el primero en quedarse sin fichas!", SwingConstants.CENTER);
        lblFooter.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 16));
        lblFooter.setForeground(COLOR_TEXTO_AMARILLO);
        lblFooter.setBorder(new EmptyBorder(5, 0, 0, 0));
        mainPanel.add(lblFooter, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addLabel(JPanel panel, String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(2)); 
    }

    private JPanel createTileGroupPanel(TileData[] tiles, String htmlDescription) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        for (TileData tile : tiles) {
            panel.add(new TileComponent(tile.text, tile.color));
        }

        JLabel lblDesc = new JLabel(htmlDescription);
        lblDesc.setFont(FUENTE_TEXTO);
        lblDesc.setForeground(COLOR_TEXTO_BLANCO);
        panel.add(lblDesc);
        return panel;
    }

    private static class TileData {
        String text; Color color;
        public TileData(String text, Color color) { this.text = text; this.color = color; }
    }

    private class TileComponent extends JComponent {
        private String number; private Color color;
        public TileComponent(String number, Color color) {
            this.number = number; this.color = color;
            setPreferredSize(new Dimension(28, 40)); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int offset = 1; int w = getWidth() - (offset * 2); int h = getHeight() - (offset * 2);
            g2.setColor(color); g2.fillRoundRect(offset, offset, w, h, 8, 8);
            g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(1)); 
            g2.drawRoundRect(offset, offset, w, h, 8, 8);
            g2.setColor(Color.WHITE); g2.setFont(FUENTE_FICHA);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(number)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(new Color(0,0,0,50)); g2.drawString(number, x+1, y+1);
            g2.setColor(Color.WHITE); g2.drawString(number, x, y);
        }
    }

    // Esto solo sirve para probar la ventana sola, no afecta al juego.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InstructivoRummy().setVisible(true);
        });
    }
}