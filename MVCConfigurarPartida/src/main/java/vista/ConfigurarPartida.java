package vista;

import TipoEventos.EventoConfig;
import controlador.ControladorConfig;
import gestor.GestorSonidos;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import modelo.iModeloConfig;

/**
 * Esta vista representa la pantalla para configurar una partida.
 *
 * @author Sebastian Moreno
 */
public class ConfigurarPartida extends javax.swing.JFrame implements ObservadorConfig {

    ControladorConfig control;
    private final Border BORDE_HOVER = new LineBorder(new Color(255, 215, 0), 3, true); // Amarillo, 3px, Redondeado

    /*Atributos privados temporales*/
    int numComodines = 1;
    int numFichas;

    public ConfigurarPartida(ControladorConfig control) {
        this.control = control;
        this.setTitle("RummyKub | Vive la experiencia!");
        this.setSize(920, 550);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initComponents();
        listenerSpinner();
        configurarHovers();
        configurarIconoVentana();
    }

    private void configurarHovers() {

        // Botón Terminar Partida:
        // El trigger es el texto (btnTerminarPartida), pero el que se anima es el panel azul (panelRound1)
        agregarEfectoHover(txt10Fichas, btn10Fichas);
        agregarEfectoHover(txt13Fichas, btn13Fichas);
        agregarEfectoHover(txtCrearPartida, btnCrearPartida);
        agregarEfectoHover(txtNumComodines1, btnRegresar);

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtTitulo = new javax.swing.JLabel();
        spinnerComidines = new javax.swing.JSpinner();
        btn13Fichas = new vista.PanelRound();
        txt13Fichas = new javax.swing.JLabel();
        btnCrearPartida = new vista.PanelRound();
        txtCrearPartida = new javax.swing.JLabel();
        btn10Fichas = new vista.PanelRound();
        txt10Fichas = new javax.swing.JLabel();
        txtNumComodines = new javax.swing.JLabel();
        btnRegresar = new vista.PanelRound();
        txtNumComodines1 = new javax.swing.JLabel();
        txtNumComodines2 = new javax.swing.JLabel();
        txtFichasCont = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(900, 500));
        getContentPane().setLayout(null);

        txtTitulo.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        txtTitulo.setForeground(new java.awt.Color(255, 255, 255));
        txtTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTitulo.setText("Configurar Partida");
        getContentPane().add(txtTitulo);
        txtTitulo.setBounds(0, 0, 900, 90);

        spinnerComidines.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));
        spinnerComidines.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spinnerComidinesPropertyChange(evt);
            }
        });
        getContentPane().add(spinnerComidines);
        spinnerComidines.setBounds(450, 142, 260, 30);

        btn13Fichas.setBackground(new java.awt.Color(18, 88, 114));
        btn13Fichas.setRoundBottomLeft(30);
        btn13Fichas.setRoundBottomRight(30);
        btn13Fichas.setRoundTopLeft(30);
        btn13Fichas.setRoundTopRight(30);
        btn13Fichas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt13Fichas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txt13Fichas.setForeground(new java.awt.Color(255, 255, 255));
        txt13Fichas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt13Fichas.setText("13 Fichas");
        txt13Fichas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txt13Fichas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt13FichasMouseClicked(evt);
            }
        });
        btn13Fichas.add(txt13Fichas, new org.netbeans.lib.awtextra.AbsoluteConstraints(-3, 2, 240, 60));

        getContentPane().add(btn13Fichas);
        btn13Fichas.setBounds(470, 210, 240, 60);

        btnCrearPartida.setBackground(new java.awt.Color(26, 83, 162));
        btnCrearPartida.setRoundBottomLeft(30);
        btnCrearPartida.setRoundBottomRight(30);
        btnCrearPartida.setRoundTopLeft(30);
        btnCrearPartida.setRoundTopRight(30);
        btnCrearPartida.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtCrearPartida.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtCrearPartida.setForeground(new java.awt.Color(255, 255, 255));
        txtCrearPartida.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtCrearPartida.setText("Crear Partida");
        txtCrearPartida.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtCrearPartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCrearPartidaMouseClicked(evt);
            }
        });
        btnCrearPartida.add(txtCrearPartida, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 70));

        getContentPane().add(btnCrearPartida);
        btnCrearPartida.setBounds(320, 300, 240, 70);

        btn10Fichas.setBackground(new java.awt.Color(18, 88, 114));
        btn10Fichas.setRoundBottomLeft(30);
        btn10Fichas.setRoundBottomRight(30);
        btn10Fichas.setRoundTopLeft(30);
        btn10Fichas.setRoundTopRight(30);
        btn10Fichas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt10Fichas.setBackground(new java.awt.Color(21, 56, 96));
        txt10Fichas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txt10Fichas.setForeground(new java.awt.Color(255, 255, 255));
        txt10Fichas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt10Fichas.setText("10 Fichas");
        txt10Fichas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txt10Fichas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt10FichasMouseClicked(evt);
            }
        });
        btn10Fichas.add(txt10Fichas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 240, 60));

        getContentPane().add(btn10Fichas);
        btn10Fichas.setBounds(170, 210, 240, 60);

        txtNumComodines.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtNumComodines.setForeground(new java.awt.Color(255, 255, 255));
        txtNumComodines.setText("Numero de Comodines");
        getContentPane().add(txtNumComodines);
        txtNumComodines.setBounds(190, 140, 280, 32);

        btnRegresar.setBackground(new java.awt.Color(206, 70, 70));
        btnRegresar.setRoundBottomLeft(20);
        btnRegresar.setRoundBottomRight(20);
        btnRegresar.setRoundTopLeft(20);
        btnRegresar.setRoundTopRight(20);
        btnRegresar.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNumComodines1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtNumComodines1.setForeground(new java.awt.Color(255, 255, 255));
        txtNumComodines1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNumComodines1.setText("Regresar");
        txtNumComodines1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtNumComodines1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtNumComodines1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtNumComodines1MouseEntered(evt);
            }
        });
        btnRegresar.add(txtNumComodines1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, -3, 170, 40));

        getContentPane().add(btnRegresar);
        btnRegresar.setBounds(30, 440, 170, 40);

        txtNumComodines2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtNumComodines2.setForeground(new java.awt.Color(255, 255, 255));
        txtNumComodines2.setText("Comodines: 1");
        getContentPane().add(txtNumComodines2);
        txtNumComodines2.setBounds(190, 102, 200, 30);

        txtFichasCont.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtFichasCont.setForeground(new java.awt.Color(255, 255, 255));
        txtFichasCont.setText("Fichas: No establecido");
        getContentPane().add(txtFichasCont);
        txtFichasCont.setBounds(430, 102, 250, 30);

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoR.png"))); // NOI18N
        getContentPane().add(fondo);
        fondo.setBounds(0, 0, 930, 540);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCrearPartidaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCrearPartidaMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
        int fichasSeleccionadas = this.numFichas;
        int comodinesSeleccionados = (Integer) spinnerComidines.getValue();

        if (fichasSeleccionadas > 0 && comodinesSeleccionados >= 0) {
            control.configurarPartida(comodinesSeleccionados, fichasSeleccionadas);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor selecciona el modo de juego (10 o 13 fichas).", "Faltan datos", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtCrearPartidaMouseClicked

    private void txt10FichasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt10FichasMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
        btn10Fichas.setEnabled(false);
        txt10Fichas.setEnabled(false);
        btn13Fichas.setEnabled(true);
        txt13Fichas.setEnabled(true);

        numFichas = 10;
        txtFichasCont.setText("Fichas: 10Fichas");

    }//GEN-LAST:event_txt10FichasMouseClicked

    private void txt13FichasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt13FichasMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

        btn13Fichas.setEnabled(false);
        txt13Fichas.setEnabled(false);
        btn10Fichas.setEnabled(true);
        txt10Fichas.setEnabled(true);

        numFichas = 13;
        txtFichasCont.setText("Fichas: 13Fichas");

    }//GEN-LAST:event_txt13FichasMouseClicked

    private void spinnerComidinesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_spinnerComidinesPropertyChange

    }//GEN-LAST:event_spinnerComidinesPropertyChange

    private void txtNumComodines1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNumComodines1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumComodines1MouseEntered

    private void txtNumComodines1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNumComodines1MouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

        this.setVisible(false);
        control.regresarPantallaPrincipal();
    }//GEN-LAST:event_txtNumComodines1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vista.PanelRound btn10Fichas;
    private vista.PanelRound btn13Fichas;
    private vista.PanelRound btnCrearPartida;
    private vista.PanelRound btnRegresar;
    private javax.swing.JLabel fondo;
    private javax.swing.JSpinner spinnerComidines;
    private javax.swing.JLabel txt10Fichas;
    private javax.swing.JLabel txt13Fichas;
    private javax.swing.JLabel txtCrearPartida;
    private javax.swing.JLabel txtFichasCont;
    private javax.swing.JLabel txtNumComodines;
    private javax.swing.JLabel txtNumComodines1;
    private javax.swing.JLabel txtNumComodines2;
    private javax.swing.JLabel txtTitulo;
    // End of variables declaration//GEN-END:variables
    /**
     * Metodo para obtener datos o notificaciones del modelo, sin conocerlo.
     *
     * @param modelo cualquier clase que implemente modelo.
     * @param evento un tipo de evento en especifico, el cual es la variable a
     * hacer en el switch.
     */
    @Override
    public void actualiza(iModeloConfig modelo, EventoConfig evento) {
        switch (evento) {
            case CERRAR_CU:
                this.setVisible(false);
                break;
            case CREAR_PARTIDA:
                this.setVisible(true);
                break;

            case PARTIDA_CREADA:
                JOptionPane.showMessageDialog(this, "Partida Configurada y Creada con: " + numComodines + " comodin(es) y " + numFichas + " fichas!, Esperando Jugadores...");
                control.avanzarARegistro();
                break;
            default:

        }
    }

    /**
     * Metodo auxiliar para ponerle un listener al input cuando cambie de
     * estado.
     */
    public void listenerSpinner() {
        spinnerComidines.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
                numComodines = (Integer) spinnerComidines.getValue();

                txtNumComodines2.setText("Comodines: " + numComodines);
            }
        });
    }

    private void configurarIconoVentana() {
        try {
            // 1. Intentar cargar la ruta del icono
            URL urlIcono = getClass().getResource("/imagenes/iconoJuego.png");
            if (urlIcono == null) {
                urlIcono = getClass().getResource("iImagenes/InstructivoIcon.png"); // Fallback
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
}
