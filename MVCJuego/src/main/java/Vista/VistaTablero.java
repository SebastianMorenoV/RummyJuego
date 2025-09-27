package Vista;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import Modelo.IModelo;
import Vista.Objetos.FichaUI;
import Vista.Objetos.JugadorUI;
import Vista.Objetos.ManoUI;
import Vista.Objetos.MazoUI;
import Vista.Objetos.TableroUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        btnFinalizarTurno = new javax.swing.JLabel();
        panelFichasArmadas = new javax.swing.JPanel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        GUIjuego.setBackground(new java.awt.Color(0, 0, 0));
        GUIjuego.setLayout(null);

        btnFinalizarTurno.setForeground(new java.awt.Color(255, 51, 51));
        btnFinalizarTurno.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnFinalizarTurno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/finalizarTurno.png"))); // NOI18N
        btnFinalizarTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnFinalizarTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFinalizarTurnoMouseClicked(evt);
            }
        });
        GUIjuego.add(btnFinalizarTurno);
        btnFinalizarTurno.setBounds(790, 260, 100, 100);

        panelFichasArmadas.setBackground(new java.awt.Color(23, 57, 134));

        javax.swing.GroupLayout panelFichasArmadasLayout = new javax.swing.GroupLayout(panelFichasArmadas);
        panelFichasArmadas.setLayout(panelFichasArmadasLayout);
        panelFichasArmadasLayout.setHorizontalGroup(
            panelFichasArmadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        panelFichasArmadasLayout.setVerticalGroup(
            panelFichasArmadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        GUIjuego.add(panelFichasArmadas);
        panelFichasArmadas.setBounds(130, 0, 640, 120);

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
        if (tableroUI != null) {
            control.terminarTurno();
        }
    }//GEN-LAST:event_btnFinalizarTurnoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GUIjuego;
    private javax.swing.JLabel btnFinalizarTurno;
    private javax.swing.JLabel fondo;
    private javax.swing.JPanel panelFichasArmadas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actualiza(IModelo modelo, TipoEvento tipoEvento) {
        // Usamos SwingUtilities para asegurar que las actualizaciones de UI ocurran en el hilo correcto.
        javax.swing.SwingUtilities.invokeLater(() -> {
            switch (tipoEvento) {
                case INCIALIZAR_FICHAS:
                    iniciarComponentesDeJuego(modelo);
                    break;
                case REPINTAR_MANO:
                    repintarMano(modelo);
                    break;
                case ACTUALIZAR_TABLERO_TEMPORAL:
                    if (tableroUI != null) {
                        // Llamamos al repintado sin guardar el estado
                        tableroUI.repintarTablero(false);
                    }
                    break;
                case JUGADA_VALIDA_FINALIZADA:
                    if (tableroUI != null) {
                        // Llamamos al repintado y le decimos que guarde el estado
                        tableroUI.repintarTablero(true);
                    }
                    break;
                case JUGADA_INVALIDA_REVERTIR:
                    if (tableroUI != null) {
                        tableroUI.revertirCambiosVisuales();
                    }
                    break;
                case TOMO_FICHA:
                    repintarMazo(modelo);
                    break;
            }
        });
    }

    public TableroUI getPanelTablero() {
        return tableroUI;
    }

    // <editor-fold defaultstate="collapsed" desc="Método para cargar avatares de jugadores">
    private void cargarJugadores() {
        String rutaImagen = "src/main/resources/avatares/avatar.png";
        try {
            Path path = new File(rutaImagen).toPath();
            byte[] imagenAvatarBytes = Files.readAllBytes(path);

            JugadorUI jugador1 = new JugadorUI("Sebastian", 7, imagenAvatarBytes);
            jugador1.setSize(150, 150);
            jugador1.setLocation(-10, -10);
            GUIjuego.add(jugador1);

            JugadorUI jugador2 = new JugadorUI("Benjamin", 15, imagenAvatarBytes);
            jugador2.setSize(150, 150);
            jugador2.setLocation(-10, 360);
            GUIjuego.add(jugador2);

            JugadorUI jugador3 = new JugadorUI("Luciano", 10, imagenAvatarBytes);
            jugador3.setSize(150, 150);
            jugador3.setLocation(760, -10);
            GUIjuego.add(jugador3);

            JugadorUI jugador4 = new JugadorUI("Mr.Fitch", 5, imagenAvatarBytes);
            jugador4.setSize(150, 150);
            jugador4.setLocation(760, 360);
            GUIjuego.add(jugador4);

        } catch (IOException e) {
            System.err.println("Error: No se pudo encontrar o leer el archivo de imagen en la ruta: " + rutaImagen);
        }
    }

    private void iniciarComponentesDeJuego(IModelo modelo) {
        crearTablero(modelo);
        crearManoUI();
        repintarMano(modelo);
        crearMazo(modelo);
        cargarJugadores();

        // Añadimos el fondo al final de todo para que esté en la capa inferior.
        GUIjuego.add(fondo);

        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    private void crearTablero(IModelo modelo) {
        if (tableroUI == null) {
            tableroUI = new TableroUI(modelo, control, this);
            tableroUI.setLocation(130, 130);
            // El tamaño ya está definido dentro de la clase TableroUI (660, 245)
            GUIjuego.add(tableroUI);
        }
    }

    private void crearManoUI() {
        if (manoUI == null) {
            manoUI = new ManoUI();
            manoUI.setLocation(160, 380);
            manoUI.setSize(580, 120);
            GUIjuego.add(manoUI);
        }
    }

    private void repintarMano(IModelo modelo) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = modelo.getMano();
        for (FichaJuegoDTO fichaDTO : fichasMano) {
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
        manoUI.revalidate();
        manoUI.repaint();
    }

    private void crearMazo(IModelo modelo) {
        if (mazoUI == null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI = new MazoUI(String.valueOf(fichasRestantes), control);
            mazoUI.setLocation(800, 150);
            mazoUI.setSize(70, 90);
            GUIjuego.add(mazoUI);
        }
    }

    private void repintarMazo(IModelo modelo) {
        if (mazoUI != null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI.setNumeroFichasRestantes(String.valueOf(fichasRestantes));
        }
    }
}
