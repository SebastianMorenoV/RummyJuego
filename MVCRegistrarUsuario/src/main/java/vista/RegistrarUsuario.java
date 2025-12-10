/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import TipoEventos.EventoRegistro;
import contratos.controladoresMVC.iControlRegistro;
import contratos.iNavegacion;
import gestorPadre.GestorSonidos;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author chris
 */
public class RegistrarUsuario extends javax.swing.JFrame implements ObservadorRegistro {

    private iControlRegistro control;
    private final int TOTAL_AVATARES = 10;
    // Estado del usuario
    private String avatarSeleccionado = "1";
    private int currentAvatarIndex = 1; // Índice para controlar el carrusel (1-4)
    private final Color DEFAULT_C1 = new Color(185, 29, 29);   // Rojo Intenso
    private final Color DEFAULT_C2 = new Color(21, 101, 192);  // Azul Fuerte
    private final Color DEFAULT_C3 = new Color(46, 125, 50);   // Verde Bosque
    private final Color DEFAULT_C4 = new Color(249, 168, 37);  // Dorado
    // Colores de los 4 sets (se llenarán al registrar o elegir color)
    private Color colorSet1;
    private Color colorSet2;
    private Color colorSet3;
    private Color colorSet4;

    private final Border BORDE_HOVER = new LineBorder(new Color(255, 215, 0), 3, true); // Amarillo, 3px, Redondeado

    /**
     * Creates new form RegistrarUsuario
     *
     * @param control
     */
    public RegistrarUsuario(iControlRegistro control) {
        this.control = control;
        this.setTitle("RummyKub | Registrarse");
        this.setSize(900, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initComponents();
        initEvents();
        initCarousel();
        configurarHovers(); // <--- AQUI SE LLAMA AL NUEVO METODO
        resetearVista();
        configurarIconoVentana();
    }

    private void initCarousel() {
        updateAvatarDisplay();

        // Flecha Izquierda (Atrás)
        flechaCarruselIzquierda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentAvatarIndex--;
                if (currentAvatarIndex < 1) {
                    // En lugar de un 4 fijo, usa la variable
                    currentAvatarIndex = TOTAL_AVATARES;
                }
                updateAvatarDisplay();
            }
        });

        // Flecha Derecha (Adelante)
        flechaCarruselDerecha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentAvatarIndex++;
                // En lugar de un 4 fijo, usa la variable
                if (currentAvatarIndex > TOTAL_AVATARES) {
                    currentAvatarIndex = 1;
                }
                updateAvatarDisplay();
            }
        });
    }

    /**
     * Configura qué componentes tendrán la animación de zoom/borde.
     */
    private void configurarHovers() {
        // 1. Botón Registrar (Texto activa al Panel de fondo)
        agregarEfectoHover(btnRegistrar, panelRound2);

        // 2. Botón de Pintura (Se anima solo)
        agregarEfectoHover(btnColor);

        // 3. Flechas del carrusel (Se animan solas)
        agregarEfectoHover(flechaCarruselIzquierda);
        agregarEfectoHover(flechaCarruselDerecha);
    }

    /**
     * Sobrecarga para elementos simples que se animan a sí mismos.
     */
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

    private void updateAvatarDisplay() {
        String extension = ".png";
        // Excepciones para los archivos que no son png
        if (currentAvatarIndex == 7) {
            extension = ".jpeg";
        } else if (currentAvatarIndex == 8 || currentAvatarIndex == 9) {
            extension = ".jpg";
        }

        String ruta = "/avatares/avatar" + currentAvatarIndex + extension;
        colocarImagen(lblImagenesCarrusel, ruta);

        this.avatarSeleccionado = String.valueOf(currentAvatarIndex);
    }

    private void initEvents() {
        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String nombre = txtfldNombre.getText();
                if (!nombre.trim().isEmpty()) {
                    // Si no se eligieron colores, usar default
                    if (colorSet1 == null) {
                        colorSet1 = Color.RED;
                        colorSet2 = Color.GREEN;
                        colorSet3 = Color.YELLOW;
                        colorSet4 = Color.BLUE;
                    }
                    control.intentarRegistrar(nombre, avatarSeleccionado, colorSet1, colorSet2, colorSet3, colorSet4);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor ingresa un nombre.");
                }
            }
        });

        btnColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirEleccionColores();
            }
        });
    }

    /**
     * Lógica para resaltar el avatar seleccionado
     *
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblNombreJ1 = new javax.swing.JLabel();
        lblAvatarJ2 = new javax.swing.JLabel();
        lblAvatarJ3 = new javax.swing.JLabel();
        flechaCarruselIzquierda = new javax.swing.JLabel();
        lblAvatarJ4 = new javax.swing.JLabel();
        flechaCarruselDerecha = new javax.swing.JLabel();
        lblNombreJ2 = new javax.swing.JLabel();
        color1 = new vista.PanelRound();
        lblImagenesCarrusel = new javax.swing.JLabel();
        color3 = new vista.PanelRound();
        color2 = new vista.PanelRound();
        color4 = new vista.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        lblAvatarJ1 = new javax.swing.JLabel();
        lblNombreJ3 = new javax.swing.JLabel();
        lblNombreJ4 = new javax.swing.JLabel();
        btnColor = new javax.swing.JLabel();
        txtfldNombre = new javax.swing.JTextField();
        txt2 = new javax.swing.JLabel();
        txt1 = new javax.swing.JLabel();
        txtSubtitulo = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JLabel();
        panelRound2 = new vista.PanelRound();
        btnRegistrar = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNombreJ1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNombreJ1.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreJ1.setText("Espacio vacio...");
        jPanel1.add(lblNombreJ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 150, 270, 40));

        lblAvatarJ2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(lblAvatarJ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 220, 80, 80));

        lblAvatarJ3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(lblAvatarJ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 320, 80, 80));

        flechaCarruselIzquierda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaIzquierda.png"))); // NOI18N
        flechaCarruselIzquierda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        flechaCarruselIzquierda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flechaCarruselIzquierdaMouseClicked(evt);
            }
        });
        jPanel1.add(flechaCarruselIzquierda, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));

        lblAvatarJ4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(lblAvatarJ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 420, 80, 80));

        flechaCarruselDerecha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaDerecha.png"))); // NOI18N
        flechaCarruselDerecha.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        flechaCarruselDerecha.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flechaCarruselDerechaMouseClicked(evt);
            }
        });
        jPanel1.add(flechaCarruselDerecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, -1, -1));

        lblNombreJ2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNombreJ2.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreJ2.setText("Espacio vacio...");
        jPanel1.add(lblNombreJ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 250, 270, 20));

        color1.setRoundBottomLeft(80);
        color1.setRoundBottomRight(80);
        color1.setRoundTopLeft(80);
        color1.setRoundTopRight(80);

        javax.swing.GroupLayout color1Layout = new javax.swing.GroupLayout(color1);
        color1.setLayout(color1Layout);
        color1Layout.setHorizontalGroup(
            color1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        color1Layout.setVerticalGroup(
            color1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(color1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 410, 40, 40));
        jPanel1.add(lblImagenesCarrusel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 80, 80));

        color3.setRoundBottomLeft(80);
        color3.setRoundBottomRight(80);
        color3.setRoundTopLeft(80);
        color3.setRoundTopRight(80);

        javax.swing.GroupLayout color3Layout = new javax.swing.GroupLayout(color3);
        color3.setLayout(color3Layout);
        color3Layout.setHorizontalGroup(
            color3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        color3Layout.setVerticalGroup(
            color3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(color3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 410, -1, -1));

        color2.setRoundBottomLeft(80);
        color2.setRoundBottomRight(80);
        color2.setRoundTopLeft(80);
        color2.setRoundTopRight(80);

        javax.swing.GroupLayout color2Layout = new javax.swing.GroupLayout(color2);
        color2.setLayout(color2Layout);
        color2Layout.setHorizontalGroup(
            color2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        color2Layout.setVerticalGroup(
            color2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(color2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 410, -1, -1));

        color4.setRoundBottomLeft(80);
        color4.setRoundBottomRight(80);
        color4.setRoundTopLeft(80);
        color4.setRoundTopRight(80);

        javax.swing.GroupLayout color4Layout = new javax.swing.GroupLayout(color4);
        color4.setLayout(color4Layout);
        color4Layout.setHorizontalGroup(
            color4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        color4Layout.setVerticalGroup(
            color4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(color4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 410, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Jugadores en la sala de espera");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 90, -1, -1));

        lblAvatarJ1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPanel1.add(lblAvatarJ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 130, 80, 80));

        lblNombreJ3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNombreJ3.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreJ3.setText("Espacio vacio...");
        jPanel1.add(lblNombreJ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 350, 270, 20));

        lblNombreJ4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblNombreJ4.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreJ4.setText("Espacio vacio...");
        jPanel1.add(lblNombreJ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 450, 270, 20));

        btnColor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/paint.png"))); // NOI18N
        btnColor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnColorMouseClicked(evt);
            }
        });
        jPanel1.add(btnColor, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 350, -1, -1));

        txtfldNombre.setBackground(new java.awt.Color(255, 255, 255));
        txtfldNombre.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtfldNombre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtfldNombreMouseClicked(evt);
            }
        });
        txtfldNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfldNombreActionPerformed(evt);
            }
        });
        jPanel1.add(txtfldNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 220, 30));

        txt2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt2.setForeground(new java.awt.Color(255, 255, 255));
        txt2.setText("Selecciona el color de tus fichas");
        jPanel1.add(txt2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 310, -1, -1));

        txt1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txt1.setForeground(new java.awt.Color(255, 255, 255));
        txt1.setText("Nombre:");
        jPanel1.add(txt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, 30));

        txtSubtitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtSubtitulo.setForeground(new java.awt.Color(255, 255, 255));
        txtSubtitulo.setText("Selecciona tu avatar:");
        jPanel1.add(txtSubtitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

        txtTitulo.setFont(new java.awt.Font("Segoe UI", 1, 50)); // NOI18N
        txtTitulo.setForeground(new java.awt.Color(255, 255, 255));
        txtTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTitulo.setText("Registrar Usuario");
        txtTitulo.setToolTipText("");
        jPanel1.add(txtTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 450, 70));

        panelRound2.setBackground(new java.awt.Color(18, 88, 114));
        panelRound2.setForeground(new java.awt.Color(255, 255, 255));
        panelRound2.setToolTipText("");
        panelRound2.setRoundBottomLeft(40);
        panelRound2.setRoundBottomRight(40);
        panelRound2.setRoundTopLeft(40);
        panelRound2.setRoundTopRight(40);

        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnRegistrar.setText("Registrarse");
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRegistrarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnRegistrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 480, 240, 60));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoRummy.png"))); // NOI18N
        fondo.setText("jLabel1");
        jPanel1.add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, 570));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtfldNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfldNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfldNombreActionPerformed

    private void btnRegistrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRegistrarMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

    }//GEN-LAST:event_btnRegistrarMouseClicked

    private void flechaCarruselDerechaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flechaCarruselDerechaMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

    }//GEN-LAST:event_flechaCarruselDerechaMouseClicked

    private void flechaCarruselIzquierdaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flechaCarruselIzquierdaMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

    }//GEN-LAST:event_flechaCarruselIzquierdaMouseClicked

    private void txtfldNombreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtfldNombreMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
    }//GEN-LAST:event_txtfldNombreMouseClicked

    private void btnColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnColorMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
    }//GEN-LAST:event_btnColorMouseClicked

    private void abrirEleccionColores() {
        EleccionColores ventanaColores = new EleccionColores(this);
        ventanaColores.setVisible(true);
    }

    public void actualizarListaJugadores(String data) {
        limpiarJugador(lblNombreJ1, lblAvatarJ1);
        limpiarJugador(lblNombreJ2, lblAvatarJ2);
        limpiarJugador(lblNombreJ3, lblAvatarJ3);
        limpiarJugador(lblNombreJ4, lblAvatarJ4);

        if (data == null || data.isEmpty()) {
            return;
        }

        String[] jugadores = data.split(";");

        // 2. Pintar según cuántos lleguen
        if (jugadores.length > 0) {
            pintarJugadorEnLista(jugadores[0], lblNombreJ1, lblAvatarJ1);
        }
        if (jugadores.length > 1) {
            pintarJugadorEnLista(jugadores[1], lblNombreJ2, lblAvatarJ2);
        }
        if (jugadores.length > 2) {
            pintarJugadorEnLista(jugadores[2], lblNombreJ3, lblAvatarJ3);
        }
        if (jugadores.length > 3) {
            pintarJugadorEnLista(jugadores[3], lblNombreJ4, lblAvatarJ4);
        }
    }

    private void limpiarJugador(JLabel lblNombre, JLabel lblAvatar) {
        lblNombre.setText("Espacio disponible");
        lblAvatar.setIcon(null);
    }

    private void pintarJugadorEnLista(String rawData, JLabel lblNombre, JLabel lblAvatar) {
        try {
            // Formato esperado: ID,Nombre$Avatar$Color...
            String[] primeraSeparacion = rawData.split(",");
            if (primeraSeparacion.length > 1) {
                String payload = primeraSeparacion[1];
                String[] datosUsuario = payload.split("\\$");

                String nombre = datosUsuario[0];
                String idAvatar = (datosUsuario.length > 1) ? datosUsuario[1] : "1";

                lblNombre.setText(nombre);
                colocarImagen(lblAvatar, "/avatares/avatar" + idAvatar + ".png");
            }
        } catch (Exception e) {
            System.err.println("Error pintando jugador: " + e.getMessage());
        }
    }

    public void resetearVista() {
        if (txtfldNombre != null) {
            txtfldNombre.setText("");
        }

        currentAvatarIndex = 1;
        updateAvatarDisplay();

        actualizarColoresUsuario(DEFAULT_C1, DEFAULT_C2, DEFAULT_C3, DEFAULT_C4);

    }

    private void colocarImagen(JLabel label, String ruta) {
        try {
            java.net.URL imgURL = getClass().getResource(ruta);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(img));
            } else {
                System.err.println("No se encontró imagen: " + ruta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnColor;
    private javax.swing.JLabel btnRegistrar;
    private vista.PanelRound color1;
    private vista.PanelRound color2;
    private vista.PanelRound color3;
    private vista.PanelRound color4;
    private javax.swing.JLabel flechaCarruselDerecha;
    private javax.swing.JLabel flechaCarruselIzquierda;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAvatarJ1;
    private javax.swing.JLabel lblAvatarJ2;
    private javax.swing.JLabel lblAvatarJ3;
    private javax.swing.JLabel lblAvatarJ4;
    private javax.swing.JLabel lblImagenesCarrusel;
    private javax.swing.JLabel lblNombreJ1;
    private javax.swing.JLabel lblNombreJ2;
    private javax.swing.JLabel lblNombreJ3;
    private javax.swing.JLabel lblNombreJ4;
    private vista.PanelRound panelRound2;
    private javax.swing.JLabel txt1;
    private javax.swing.JLabel txt2;
    private javax.swing.JLabel txtSubtitulo;
    private javax.swing.JLabel txtTitulo;
    private javax.swing.JTextField txtfldNombre;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actualiza(EventoRegistro evento, String mensaje) {
        switch (evento) {
            case REGISTRO_EXITOSO:
                System.out.println("[VistaRegistro] Registro exitoso. Cerrando y navegando...");
                this.setVisible(false);

                control.entrarSalaEspera();

                break;

            case NOMBRE_REPETIDO:
                mostrarError("El nombre ya está en uso.");
                break;

            case ABRIR_VENTANA:
                resetearVista();
                this.setVisible(true);
                this.toFront();
                break;
            case ACTUALIZAR_SALA: // <--- Actualizar vista
                actualizarListaJugadores(mensaje);
                break;
        }
    }

    /**
     * Método público llamado por EleccionColores
     *
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     */
    public void actualizarColoresUsuario(Color c1, Color c2, Color c3, Color c4) {
        this.colorSet1 = c1;
        this.colorSet2 = c2;
        this.colorSet3 = c3;
        this.colorSet4 = c4;

        if (color1 != null) {
            color1.setBackground(c1);
        }
        if (color2 != null) {
            color2.setBackground(c2);
        }
        if (color3 != null) {
            color3.setBackground(c3);
        }
        if (color4 != null) {
            color4.setBackground(c4);
        }

        this.repaint();
    }

    public void mostrarError(String mensaje) {
        javax.swing.JOptionPane.showMessageDialog(this,
                mensaje,
                "Error de Registro",
                javax.swing.JOptionPane.ERROR_MESSAGE);
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
