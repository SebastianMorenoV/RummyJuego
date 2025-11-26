package Vista;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import Dtos.ActualizacionDTO;
import Modelo.IModelo;
import Vista.Objetos.FichaUI;
import Vista.Objetos.JugadorUI;
import Vista.Objetos.ManoUI;
import Vista.Objetos.MazoUI;
import Vista.Objetos.TableroUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Esta clase representa la vistaGeneral de el tablero y todos sus elementos de
 * el juego.
 *
 * @author benja
 */
public class VistaTablero extends javax.swing.JFrame implements Observador {

    private Controlador control;
    private TableroUI tableroUI;
    private ManoUI manoUI;
    private MazoUI mazoUI;

    /**
     * Constructor que recibe el control para poder ejecutar la logica hacia el
     * siguiente componente de MVC.
     *
     * @param control el control de el mvc.
     */
    public VistaTablero(Controlador control) {
        this.control = control;
        this.setSize(920, 550);
        this.setTitle("Rummy Juego");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        initComponents();
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GUIjuego = new javax.swing.JPanel();
        btnOrdenarMayorAMenor = new javax.swing.JLabel();
        btnOrdenarPorGrupos = new javax.swing.JLabel();
        btnFinalizarTurno = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        GUIjuego.setBackground(new java.awt.Color(0, 0, 0));
        GUIjuego.setLayout(null);

        btnOrdenarMayorAMenor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/button.png"))); // NOI18N
        btnOrdenarMayorAMenor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOrdenarMayorAMenor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrdenarMayorAMenorMouseClicked(evt);
            }
        });
        GUIjuego.add(btnOrdenarMayorAMenor);
        btnOrdenarMayorAMenor.setBounds(820, 280, 50, 29);

        btnOrdenarPorGrupos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/button (1).png"))); // NOI18N
        btnOrdenarPorGrupos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOrdenarPorGrupos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrdenarPorGruposMouseClicked(evt);
            }
        });
        GUIjuego.add(btnOrdenarPorGrupos);
        btnOrdenarPorGrupos.setBounds(820, 241, 50, 29);

        btnFinalizarTurno.setForeground(new java.awt.Color(255, 51, 51));
        btnFinalizarTurno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnFinalizarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/finalizarTurno.png"))); // NOI18N
        btnFinalizarTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFinalizarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFinalizarTurnoMouseClicked(evt);
            }
        });
        GUIjuego.add(btnFinalizarTurno);
        btnFinalizarTurno.setBounds(800, 320, 90, 50);

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fondoRummy.jpg"))); // NOI18N
        fondo.setText("jLabel1");
        GUIjuego.add(fondo);
        fondo.setBounds(0, 0, 900, 500);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(GUIjuego, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(GUIjuego, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFinalizarTurnoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFinalizarTurnoMouseClicked
        control.terminarTurno();
    }//GEN-LAST:event_btnFinalizarTurnoMouseClicked

    private void btnOrdenarPorGruposMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrdenarPorGruposMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_btnOrdenarPorGruposMouseClicked

    private void btnOrdenarMayorAMenorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrdenarMayorAMenorMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_btnOrdenarMayorAMenorMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GUIjuego;
    private javax.swing.JLabel btnFinalizarTurno;
    private javax.swing.JLabel btnOrdenarMayorAMenor;
    private javax.swing.JLabel btnOrdenarPorGrupos;
    private javax.swing.JLabel fondo;
    // End of variables declaration//GEN-END:variables
    /**
     * Metodo implementado por la interfaz Observer. Reacciona a las
     * notificaciones del Modelo para actualizar la interfaz gráfica.
     *
     * @param modelo El modelo del juego.
     * @param dto El paquete de datos con el evento y el estado del turno.
     */
    @Override
    public void actualiza(IModelo modelo, ActualizacionDTO dto) {

        if (dto.getTipoEvento() == TipoEvento.INCIALIZAR_FICHAS) {
            iniciarComponentesDeJuego(modelo, dto);
            habilitarControles(dto.esMiTurno());
            return;
        }

        habilitarControles(dto.esMiTurno());

        switch (dto.getTipoEvento()) {
            case CAMBIO_DE_TURNO:
                if (dto.esMiTurno()) {
                    setTitle("Rummy - ¡Es tu turno!");
                    JOptionPane.showMessageDialog(this, "¡Ahora es tu turno! ", "Estas en turno", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    setTitle("Rummy - Esperando al oponente...");
                    JOptionPane.showMessageDialog(this, "Esperando al oponente.. ", "Esperando..", JOptionPane.INFORMATION_MESSAGE);
                }
                break;

            case REPINTAR_MANO:
                repintarMano(modelo, dto);
                break;

            case ACTUALIZAR_TABLERO_TEMPORAL:
                if (tableroUI != null) {
                    tableroUI.repintarTablero(false);
                }
                break;

            case JUGADA_VALIDA_FINALIZADA:
                if (tableroUI != null) {
                    tableroUI.repintarTablero(true);
                }
                break;

            case JUGADA_INVALIDA_REVERTIR:
                if (tableroUI != null) {
                    tableroUI.revertirCambiosVisuales();
                    JOptionPane.showMessageDialog(this,
                            "Movimiento inválido: El tablero no cumple las reglas.\nSe ha revertido la jugada.",
                            "Jugada Inválida",
                            JOptionPane.WARNING_MESSAGE);
                }
                break;

            case JUGADA_INVALIDA_REVERTIR_TOMO_FICHA:
                if (tableroUI != null) {
                    tableroUI.revertirCambiosVisuales();
                }
                break;
                
            case TOMO_FICHA:
                repintarMazo(modelo);
                break;

            case NO_ES_MI_TURNO:
                JOptionPane.showMessageDialog(this, "No es tu turno.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case TOMAR_FICHA_POR_FINALIZARTURNO:
                JOptionPane.showMessageDialog(this, "Turno finalizado, tomando ficha... ", "Turno finalizado", JOptionPane.INFORMATION_MESSAGE);
                control.pasarTurno();
                break;
        }

        btnOrdenarMayorAMenor.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event
            ) {
                repintarManoOrdenadaPorNumero(modelo, dto);
            }
        }
        );

        btnOrdenarPorGrupos.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event
            ) {
                repintarManoOrdenadaPorGrupos(modelo, dto);
            }
        }
        );

    }

    /**
     * Metodo para cargar los jugadores aun no terminado (MOCK).
     */
    private void cargarJugadores() {
        String rutaImagen = "src/main/resources/avatares/avatar.png";
        try {
            Path path = new File(rutaImagen).toPath();
            byte[] imagenAvatarBytes = Files.readAllBytes(path);

            JugadorUI jugador1 = new JugadorUI("Sebastian", 7, imagenAvatarBytes);
            jugador1.setSize(130, 130);
            jugador1.setLocation(-10, -10);
            GUIjuego.add(jugador1);

            JugadorUI jugador2 = new JugadorUI("Benjamin", 15, imagenAvatarBytes);
            jugador2.setSize(130, 130);
            jugador2.setLocation(-10, 380);
            GUIjuego.add(jugador2);

            JugadorUI jugador3 = new JugadorUI("Luciano", 10, imagenAvatarBytes);
            jugador3.setSize(130, 130);
            jugador3.setLocation(780, -10);
            GUIjuego.add(jugador3);

            JugadorUI jugador4 = new JugadorUI("Mr.Fitch", 5, imagenAvatarBytes);
            jugador4.setSize(130, 130);
            jugador4.setLocation(780, 380);
            GUIjuego.add(jugador4);

        } catch (IOException e) {
            System.err.println("Error: No se pudo encontrar o leer el archivo de imagen en la ruta: " + rutaImagen);
        }
    }

    /**
     * Metodo que pinta los componentes necesarios para la vista. Los crea con
     * su estructura base.
     *
     * @param modelo el modelo de donde se sacan los datos para crearse.
     */
    private void iniciarComponentesDeJuego(IModelo modelo, ActualizacionDTO dto) {
        crearTablero(modelo);
        crearManoUI();
        repintarMano(modelo, dto);
        crearMazo(modelo);
        cargarJugadores();
        btnFinalizarTurno.setVisible(false);
        GUIjuego.add(fondo);

        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    /**
     * Metodo para crear el panel del Tablero visualmente.
     *
     * @param modelo
     */
    private void crearTablero(IModelo modelo) {
        if (tableroUI == null) {
            tableroUI = new TableroUI(modelo, control, this);
            tableroUI.setLocation(120, 32);
            tableroUI.setOpaque(false);

            GUIjuego.add(tableroUI);
        }
    }

    /**
     * Metodo para crear la el panel de la Mano visualmente.
     */
    private void crearManoUI() {
        if (manoUI == null) {
            manoUI = new ManoUI();
            manoUI.setLocation(160, 380);
            manoUI.setSize(580, 120); // El tamaño de la mano no cambia

            JScrollPane scrollPane = new JScrollPane(
                    manoUI,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scrollPane.setOpaque(false);//Para que tenga un fondo transparente
            scrollPane.getViewport().setOpaque(false);

            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

            //Diseño personal del Scroll
            verticalBar.setUI(new BasicScrollBarUI() {

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE); // Color de la linea
                    g2.fillRoundRect(
                            thumbBounds.x + (thumbBounds.width / 8),
                            thumbBounds.y,
                            thumbBounds.width / 2,
                            thumbBounds.height,
                            1, 1
                    );
                    g2.dispose();
                }

                @Override
                protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

                }

                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }

                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }

                // Método de ayuda para crear botones invisibles
                private JButton createZeroButton() {
                    JButton button = new JButton();
                    Dimension zeroDim = new Dimension(0, 0);
                    button.setPreferredSize(zeroDim);
                    button.setMinimumSize(zeroDim);
                    button.setMaximumSize(zeroDim);
                    return button;
                }
            });

            // 4. Hacemos la barra en sí transparente (para que se vea el "track" transparente)
            verticalBar.setOpaque(false);

            // 5. Opcional: puedes definir un ancho fijo para la barra
            verticalBar.setPreferredSize(new Dimension(10, 0)); // 8 píxeles de ancho

            // --- FIN DEL CÓDIGO NUEVO ---
            scrollPane.setBounds(160, 380, 580, 120);
            scrollPane.setBorder(null);

            GUIjuego.add(scrollPane);
        }
    }

    /**
     * Metodo que repinta la mano colocandole las fichas necesarias para
     * mostrarse.
     *
     * @param modelo el modelo que pasa los datos a actualizar.
     */
    private void repintarMano(IModelo modelo, ActualizacionDTO dto) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = dto.getManoDelJugador();

        validacionesDeManoUI(fichasMano);
    }

    /**
     * Metodo para crear el mazo visual en el tablero.
     *
     * @param modelo para obtener las fichas restantes del juego
     */
    private void crearMazo(IModelo modelo) {
        if (mazoUI == null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI = new MazoUI(String.valueOf(fichasRestantes), control);
            mazoUI.setLocation(807, 140);
            mazoUI.setSize(70, 90);
            mazoUI.setOpaque(false);
            mazoUI.setCursor(new Cursor(HAND_CURSOR) {
            });
            GUIjuego.add(mazoUI);
        }
    }

    /**
     * Metodo para repintar el mazo o "actualizar" dentro del tablero. actualiza
     * el numero de fichas restantes en el juego.
     *
     * @param modelo para obtener las fichas restantes del juego
     */
    private void repintarMazo(IModelo modelo) {
        if (mazoUI != null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI.setNumeroFichasRestantes(String.valueOf(fichasRestantes));
            mazoUI.setCursor(new Cursor(HAND_CURSOR) {
            });
        }
    }

    /**
     *
     * Metodo que repinta la mano del jugador en la interfaz con un orden de
     * "Grupos". ordenando las fichas por grupos en la mano
     *
     * @param modelo
     * @param dto objeto con la info del jugador actualizada (mano)
     */
    private void repintarManoOrdenadaPorGrupos(IModelo modelo, ActualizacionDTO dto) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = dto.getManoDelJugador();

        fichasMano.sort(Comparator.comparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);

    }

    /**
     *
     * Metodo que repinta la mano del jugador en la interfaz con un orden de
     * "Numero". ordenando las fichas por numero en la mano
     *
     * @param modelo
     * @param dto objeto con la info del jugador actualizada (mano)
     */
    private void repintarManoOrdenadaPorNumero(IModelo modelo, ActualizacionDTO dto) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = dto.getManoDelJugador();

        fichasMano.sort(Comparator.comparing((FichaJuegoDTO f) -> f.getColor().toString())
                .thenComparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);
    }

    /**
     * Metodo para hacer validaciones en ManoUI. Valida si una ficha ya esta
     * colocada en el tablero para no mostrarla en la mano o si todavia
     * pertenece a la mano para no duplicarla
     *
     * @param fichasMano fichas que tiene la mano
     */
    public void validacionesDeManoUI(List<FichaJuegoDTO> fichasMano) {
        Collection<FichaUI> fichasEnTablero = tableroUI.getFichasEnTablero().values();
        Collection<FichaUI> fichasValidasEnTablero = tableroUI.getFichasEnTableroValidas().values();

        for (FichaJuegoDTO fichaDTO : fichasMano) {
            boolean yaEstaEnTablero = false;
            for (FichaUI fichaTablero : fichasEnTablero) {
                if (fichaTablero.getIdFicha() == fichaDTO.getIdFicha()) {
                    yaEstaEnTablero = true;
                    break;
                }
            }

            if (!yaEstaEnTablero) {
                for (FichaUI fichaTablero : fichasValidasEnTablero) {
                    if (fichaTablero.getIdFicha() == fichaDTO.getIdFicha()) {
                        yaEstaEnTablero = true;
                        break;
                    }
                }
            }

            if (!yaEstaEnTablero) {
                FichaUI fichaUI = new FichaUI(
                        fichaDTO.getIdFicha(),
                        fichaDTO.getNumeroFicha(),
                        fichaDTO.getColor(),
                        fichaDTO.isComodin(),
                        control, this
                );
                fichaUI.setOrigen(FichaUI.Origen.MANO);
                manoUI.add(fichaUI);
            }
        }
        manoUI.revalidate();
        manoUI.repaint();
    }

    /**
     * Metodo que deja al jugador "jugar" en un turno. basicamente si no esta en
     * turno se le bloquea el mazo, el boton de finalizar turno la mano y el
     * tablero para que no pueda manipular el turno del que si esta en turno.
     *
     * @param estaEnTurno para verificar que este en turno
     */
    public void habilitarControles(boolean estaEnTurno) {
        if (mazoUI != null) {
            mazoUI.setEnabled(estaEnTurno);
        }
        if (btnFinalizarTurno != null) {
            btnFinalizarTurno.setVisible(estaEnTurno);
        }

        if (manoUI != null) {
            manoUI.setEnabled(estaEnTurno);
        }

        if (tableroUI != null) {
            tableroUI.setEnabled(estaEnTurno);
        }
    }

    public TableroUI getPanelTablero() {
        return tableroUI;
    }

    public ManoUI getPanelMano() {
        return this.manoUI;
    }
}
