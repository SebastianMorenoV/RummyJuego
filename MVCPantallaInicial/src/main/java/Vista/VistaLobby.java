package Vista;

import Modelo.IModeloLobby;
import eventos.Evento;
import contratos.controladoresMVC.iControlCUPrincipal;
import gestor.GestorSonidos;
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
import instructivo.InstructivoRummy;

/**
 * VistaLobby con diseño de Sidebar y Video Grande.
 */
public class VistaLobby extends javax.swing.JFrame implements ObservadorLobby {

    iControlCUPrincipal control;
    private final Border BORDE_HOVER = new LineBorder(new Color(255, 215, 0), 3, true); // Amarillo, 3px, Redondeado

    // Componente del video (manual)
    private JLabel lblVideoTutorial;

    public VistaLobby(iControlCUPrincipal control) {
        this.control = control;

        // 1. Inicialización (NetBeans)
        initComponents();

        // 2. Configuración Manual (Video y ajustes visuales)
        configuracionManual();
        configurarHovers();
        // 3. Cargar Video
        cargarVideoTutorial();
    }

    private void configurarHovers() {

        // Botón Terminar Partida:
        // El trigger es el texto (btnTerminarPartida), pero el que se anima es el panel azul (panelRound1)
        agregarEfectoHover(btnAyuda);
        agregarEfectoHover(btnCrearPartida);
        agregarEfectoHover(btnUnirsePartida);

    }

    private void agregarEfectoHover(JComponent componente) {
        agregarEfectoHover(componente, componente);
    }

    /**
     * Crea un MouseAdapter que maneja el Zoom y el Borde al mismo tiempo.
     *
     * @param trigger El componente que recibe el mouse (ej. el JLabel con
     * texto/icono).
     * @param target El componente que se transforma (ej. el Panel de fondo).
     */
    private void agregarEfectoHover(JComponent trigger, JComponent target) {
        trigger.addMouseListener(new MouseAdapter() {
            private Rectangle boundsOriginales; // Para recordar dónde estaba
            private Border bordeOriginal;       // Para recordar si tenía borde antes

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!trigger.isEnabled() || !trigger.isVisible()) {
                    return;
                }

                // 1. Guardar estado original (solo la primera vez que entra para evitar bugs)
                if (boundsOriginales == null) {
                    boundsOriginales = target.getBounds();
                    bordeOriginal = target.getBorder();
                }

                // 2. EFECTO ZOOM (Crecer desde el centro)
                int pixelCrecer = 4; // Cuánto crece en total
                int offset = pixelCrecer / 2; // Cuánto se mueve para centrar

                target.setBounds(
                        boundsOriginales.x - offset,
                        boundsOriginales.y - offset,
                        boundsOriginales.width + pixelCrecer,
                        boundsOriginales.height + pixelCrecer
                );

                // 3. EFECTO BORDE DORADO
                // Le ponemos el borde amarillo brillante
                target.setBorder(BORDE_HOVER);

                // 4. CURSOR DE MANO
                trigger.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Opcional: Sonido muy sutil de "aire" o "tick" al pasar el mouse
                // GestorSonidos.reproducir("hover.wav"); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (boundsOriginales != null) {
                    // Restaurar todo a la normalidad
                    target.setBounds(boundsOriginales);
                    target.setBorder(bordeOriginal); // Quita el borde amarillo
                }
            }
        });
    }

    private void configuracionManual() {
        this.setLocationRelativeTo(null);

        // --- CONFIGURACIÓN DEL VIDEO (LADO DERECHO GRANDE) ---
        lblVideoTutorial = new JLabel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                // 1. Pintar el fondo (el color negro)
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                // 2. Pintar la imagen estirada
                if (getIcon() != null && getIcon() instanceof ImageIcon) {
                    // 'this' es importante: actúa como observador para que la animación avance
                    g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Si no hay imagen (ej. mensaje de error), usar el pintado normal de JLabel
                    super.paintComponent(g);
                }
            }
        };
        lblVideoTutorial.setBackground(new java.awt.Color(0, 0, 0));
        lblVideoTutorial.setOpaque(true);
        lblVideoTutorial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Borde dorado mantenido, puedes cambiarlo a blanco si prefieres: Color.WHITE
        lblVideoTutorial.setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 215, 0), 4));

        // COORDENADAS: x=340 (derecha), y=50, ancho=550, alto=400
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
            Icon icon = new ImageIcon(urlVideo);
            // Tip: Si el GIF es muy pequeño, Swing no lo estira automáticamente.
            // Se mostrará centrado en el recuadro negro grande.
            lblVideoTutorial.setIcon(icon);
            lblVideoTutorial.setText("");
        } else {
            lblVideoTutorial.setText("<html><center>VIDEO TUTORIAL<br>(No encontrado)</center></html>");
            lblVideoTutorial.setForeground(Color.WHITE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        title = new javax.swing.JLabel();
        btnCrearPartida = new javax.swing.JButton();
        btnUnirsePartida = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton(); // Nuevo Botón
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RummyKub | Vive la experiencia!");
        setMinimumSize(new java.awt.Dimension(920, 550));
        setResizable(false);
        getContentPane().setLayout(null);

        // --- TÍTULO (Alineado a la izquierda, Blanco, Mayúsculas) ---
        title.setFont(new java.awt.Font("Segoe UI", 1, 60));
        title.setForeground(new java.awt.Color(255, 255, 255)); // Blanco
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Alineado a la izq
        title.setText("RUMMY");
        getContentPane().add(title);
        // x=40 para margen izquierdo
        title.setBounds(40, 30, 250, 70);

        // --- BOTÓN CREAR PARTIDA ---
        btnCrearPartida.setBackground(new java.awt.Color(18, 88, 114)); // #125872
        btnCrearPartida.setFont(new java.awt.Font("Segoe UI", 1, 22));
        btnCrearPartida.setForeground(new java.awt.Color(255, 255, 255)); // Texto Blanco
        btnCrearPartida.setText("Crear partida");
        btnCrearPartida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCrearPartida.setBorderPainted(false); // Opcional: quita borde 3D para estilo más plano
        btnCrearPartida.setFocusPainted(false);
        btnCrearPartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

                btnCrearPartidaMouseClicked(evt);
            }
        });
        getContentPane().add(btnCrearPartida);
        // Columna izquierda
        btnCrearPartida.setBounds(40, 150, 260, 80);

        // --- BOTÓN UNIRSE A PARTIDA ---
        btnUnirsePartida.setBackground(new java.awt.Color(18, 88, 114)); // #125872
        btnUnirsePartida.setFont(new java.awt.Font("Segoe UI", 1, 22));
        btnUnirsePartida.setForeground(new java.awt.Color(255, 255, 255)); // Texto Blanco
        btnUnirsePartida.setText("Unirse a partida");
        btnUnirsePartida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUnirsePartida.setBorderPainted(false);
        btnUnirsePartida.setFocusPainted(false);
        btnUnirsePartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
                btnUnirsePartidaMouseClicked(evt);
            }
        });
        getContentPane().add(btnUnirsePartida);
        btnUnirsePartida.setBounds(40, 250, 260, 80);

        // --- BOTÓN AYUDA (Nuevo) ---
        btnAyuda.setBackground(new java.awt.Color(18, 88, 114)); // #125872
        btnAyuda.setFont(new java.awt.Font("Segoe UI", 1, 22));
        btnAyuda.setForeground(new java.awt.Color(255, 255, 255)); // Texto Blanco
        btnAyuda.setText("Ayuda (?)");
        btnAyuda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAyuda.setBorderPainted(false);
        btnAyuda.setFocusPainted(false);
        btnAyuda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAyudaMouseClicked(evt);
            }
        });
        getContentPane().add(btnAyuda);
        btnAyuda.setBounds(40, 350, 260, 80);

        // --- FONDO ---
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/fondoR.png")));
        jLabel1.setText("Fondo");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 920, 550);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void btnCrearPartidaMouseClicked(java.awt.event.MouseEvent evt) {
        control.iniciarCreacionPartida();
    }

    private void btnUnirsePartidaMouseClicked(java.awt.event.MouseEvent evt) {
        control.SolicitarUnirseAPartida();
        bloquearBotones(true);
        JOptionPane.showMessageDialog(this, "Solicitud enviada al servidor...", "Conectando", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnAyudaMouseClicked(java.awt.event.MouseEvent evt) {
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

        // Lógica para abrir el instructivo. 
        // Si tienes la clase InstructivoRummy, descomenta la linea de abajo:
        new InstructivoRummy().setVisible(true);
        // Si no, usa este mensaje temporal:
        // JOptionPane.showMessageDialog(this, "Aquí se abrirá el manual de usuario.", "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void bloquearBotones(boolean bloquear) {
        btnCrearPartida.setEnabled(!bloquear);
        btnUnirsePartida.setEnabled(!bloquear);
        btnAyuda.setEnabled(!bloquear);
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

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnCrearPartida;
    private javax.swing.JButton btnUnirsePartida;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel title;
    // End of variables declaration                   
}
