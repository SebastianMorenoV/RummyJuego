package Vista;

import Modelo.IModeloLobby;
import eventos.Evento;
import contratos.controladoresMVC.iControlCUPrincipal;
import gestor.GestorSonidos;
import instructivo.InstructivoRummy;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
// Asegúrate de importar PanelRound correctamente según dónde lo tengas (ej. vista.PanelRound o Vista.PanelRound)
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * VistaLobby con diseño de Sidebar y Video Grande.
 */
public class VistaLobby extends javax.swing.JFrame implements ObservadorLobby {

    iControlCUPrincipal control;
    private final Border BORDE_HOVER = new LineBorder(new Color(255, 215, 0), 3, true); // Amarillo, 3px, Redondeado

    // --- Declaración de componentes como PanelRound ---
    private PanelRound btnCrearPartida;
    private PanelRound btnUnirsePartida;
    private PanelRound btnAyuda;

    // Componente del video
    private JLabel lblVideoTutorial;
    // Título y fondo
    private JLabel title;
    private JLabel jLabel1;

    public VistaLobby(iControlCUPrincipal control) {
        this.control = control;

        // 1. Inicialización de componentes visuales
        initComponents();

        // 2. Configuración manual extra
        configuracionManual();
        configurarHovers();

        // 3. Cargar Video e Icono
        cargarVideoTutorial();
        configurarIconoVentana();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RummyKub | Vive la experiencia!");
        setMinimumSize(new java.awt.Dimension(920, 550));
        setResizable(false);
        getContentPane().setLayout(null);

        // --- TÍTULO ---
        title = new JLabel();
        title.setFont(new java.awt.Font("Segoe UI", 1, 60));
        title.setForeground(new java.awt.Color(255, 255, 255)); // Blanco
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("RUMMY");
        getContentPane().add(title);
        title.setBounds(40, 30, 250, 70);

        // --- BOTÓN CREAR PARTIDA ---
        // Inicializamos la variable btnCrearPartida correctamente
        btnCrearPartida = crearBotonRedondo("Crear partida");
        // Agregamos el listener a la misma variable que acabamos de crear
        agregarListenerClick(btnCrearPartida, evt -> btnCrearPartidaMouseClicked(null));
        getContentPane().add(btnCrearPartida);
        btnCrearPartida.setBounds(40, 150, 260, 80);

        // --- BOTÓN UNIRSE A PARTIDA ---
        btnUnirsePartida = crearBotonRedondo("Unirse a partida");
        agregarListenerClick(btnUnirsePartida, evt -> btnUnirsePartidaMouseClicked(null));
        getContentPane().add(btnUnirsePartida);
        btnUnirsePartida.setBounds(40, 250, 260, 80);

        // --- BOTÓN AYUDA ---
        btnAyuda = crearBotonRedondo("Ayuda (?)");
        agregarListenerClick(btnAyuda, evt -> btnAyudaMouseClicked(null));
        getContentPane().add(btnAyuda);
        btnAyuda.setBounds(40, 350, 260, 80);

        // --- FONDO ---
        jLabel1 = new JLabel();
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/fondoR.png")));
        jLabel1.setText("Fondo");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 920, 550);

        pack();
        setLocationRelativeTo(null);
    }

    // --- MÉTODOS AUXILIARES DE DISEÑO ---
    private PanelRound crearBotonRedondo(String texto) {
        PanelRound panel = new PanelRound();
        panel.setRoundTopLeft(30);
        panel.setRoundTopRight(30);
        panel.setRoundBottomLeft(30);
        panel.setRoundBottomRight(30);
        panel.setBackground(new Color(18, 88, 114)); // #125872
        panel.setLayout(new BorderLayout());
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTexto.setForeground(Color.WHITE);
        lblTexto.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(lblTexto, BorderLayout.CENTER);
        return panel;
    }

    private void agregarListenerClick(JComponent componente, java.awt.event.ActionListener accion) {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
                accion.actionPerformed(null);
            }
        };
        componente.addMouseListener(ma);
        // Agregar también a los hijos (la etiqueta de texto) para que el click funcione sobre las letras
        for (Component c : componente.getComponents()) {
            c.addMouseListener(ma);
        }
    }

    private void configurarIconoVentana() {
        try {
            // 1. Intentar cargar la ruta del icono
            URL urlIcono = getClass().getResource("/Imagenes/iconoJuego.png");
            if (urlIcono == null) {
                urlIcono = getClass().getResource("/Imagenes/InstructivoIcon.png"); // Fallback
            }

            if (urlIcono != null) {
                // 2. Cargar la imagen original
                ImageIcon imagenOriginal = new ImageIcon(urlIcono);

                // 3. Forzar el redimensionado a un tamaño grande (ej. 256x256 píxeles)
                //    Usamos SCALE_SMOOTH para que mantenga la calidad al estirarse.
                Image imagenEscalada = imagenOriginal.getImage()
                        .getScaledInstance(600, 600, java.awt.Image.SCALE_SMOOTH);

                // 4. Asignar la imagen grande
                this.setIconImage(imagenEscalada);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }
    }

    private void configuracionManual() {
        // Configuracion del Video
        lblVideoTutorial = new JLabel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                if (getIcon() != null && getIcon() instanceof ImageIcon) {
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    super.paintComponent(g);
                }
            }
        };
        lblVideoTutorial.setBackground(new java.awt.Color(0, 0, 0));
        lblVideoTutorial.setOpaque(true);
        lblVideoTutorial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVideoTutorial.setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 215, 0), 4));

        getContentPane().add(lblVideoTutorial);
        lblVideoTutorial.setBounds(315, 50, 580, 400);

        // Asegurar que el fondo (jLabel1) quede detrás de todo
        getContentPane().setComponentZOrder(jLabel1, getContentPane().getComponentCount() - 1);

        this.revalidate();
        this.repaint();
    }

    private void cargarVideoTutorial() {
        URL urlVideo = getClass().getResource("/Imagenes/tutorial.gif");
        if (urlVideo != null) {
            lblVideoTutorial.setIcon(new ImageIcon(urlVideo));
            lblVideoTutorial.setText("");
        } else {
            lblVideoTutorial.setText("<html><center>VIDEO TUTORIAL<br>(No encontrado)</center></html>");
            lblVideoTutorial.setForeground(Color.WHITE);
        }
    }

    private void configurarHovers() {
        // Ahora usamos las variables PanelRound correctas
        agregarEfectoHover(btnAyuda);
        agregarEfectoHover(btnCrearPartida);
        agregarEfectoHover(btnUnirsePartida);
    }

    private void agregarEfectoHover(JComponent target) {
        if (target == null) {
            return; // Protección contra nulos
        }
        MouseAdapter hoverAdapter = new MouseAdapter() {
            private Rectangle boundsOriginales;
            private Border bordeOriginal;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!target.isEnabled() || !target.isVisible()) {
                    return;
                }

                if (boundsOriginales == null) {
                    boundsOriginales = target.getBounds();
                    bordeOriginal = target.getBorder();
                }

                int pixelCrecer = 4;
                int offset = pixelCrecer / 2;

                target.setBounds(
                        boundsOriginales.x - offset,
                        boundsOriginales.y - offset,
                        boundsOriginales.width + pixelCrecer,
                        boundsOriginales.height + pixelCrecer
                );

                target.setBorder(BORDE_HOVER);
                target.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Verificar si el mouse realmente salió del componente padre
                if (target.contains(SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), target))) {
                    return;
                }

                if (boundsOriginales != null) {
                    target.setBounds(boundsOriginales);
                    target.setBorder(bordeOriginal);
                }
            }
        };

        target.addMouseListener(hoverAdapter);
        if (target instanceof Container) {
            for (Component c : ((Container) target).getComponents()) {
                c.addMouseListener(hoverAdapter);
            }
        }
    }

    // --- MÉTODOS DE LÓGICA / CONTROLADOR ---
    private void btnCrearPartidaMouseClicked(java.awt.event.MouseEvent evt) {
        control.iniciarCreacionPartida();
    }

    private void btnUnirsePartidaMouseClicked(java.awt.event.MouseEvent evt) {
        control.SolicitarUnirseAPartida();
        bloquearBotones(true);
        JOptionPane.showMessageDialog(this, "Solicitud enviada al servidor...", "Conectando", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnAyudaMouseClicked(java.awt.event.MouseEvent evt) {
        new InstructivoRummy().setVisible(true); // Descomentar si la clase existe y el import es correcto

    }

    private void bloquearBotones(boolean bloquear) {
        if (btnCrearPartida != null) {
            btnCrearPartida.setEnabled(!bloquear);
        }
        if (btnUnirsePartida != null) {
            btnUnirsePartida.setEnabled(!bloquear);
        }
        if (btnAyuda != null) {
            btnAyuda.setEnabled(!bloquear);
        }
    }

    @Override
    public void actualiza(IModeloLobby modelo, Evento evento) {
        bloquearBotones(false);

        switch (evento) {
            case PARTIDA_EXISTENTE:
                JOptionPane.showMessageDialog(this, "Ya existe una partida localmente.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case CERRAR_CU:
                this.setVisible(false);
                break;
            case INICIO:
                this.setVisible(true);
                break;
            case CREAR_PARTIDA:
                this.setVisible(false);
                control.casoUsoConfigurarPartida();
                break;
            case SOLICITAR_UNIRSE_A_PARTIDA:
                this.setVisible(false);
                control.procesarNavegacionRegistrarJugador();
                break;
            case ERROR_SALA_LLENA:
                JOptionPane.showMessageDialog(this, "La sala está llena.", "Sala Llena", JOptionPane.WARNING_MESSAGE);
                break;
            case ERROR_VOTACION_EN_CURSO:
                JOptionPane.showMessageDialog(this, "Votación en curso, espera un momento.", "Ocupado", JOptionPane.WARNING_MESSAGE);
                break;
            case ERROR_PARTIDA_YA_INICIADA:
                JOptionPane.showMessageDialog(this, "La partida ya comenzó.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case UNION_RECHAZADA:
                JOptionPane.showMessageDialog(this, "Solicitud rechazada.", "Acceso Denegado", JOptionPane.INFORMATION_MESSAGE);
                break;
            case SOLICITUD_RECHAZADA_VACIA:
                JOptionPane.showMessageDialog(this, "La sala está vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                break;
        }
    }
}
