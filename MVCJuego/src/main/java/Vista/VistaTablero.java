package Vista;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import Modelo.IModelo;
import Vista.Objetos.FichaUI;
import Vista.Objetos.JugadorUI;
import Vista.Objetos.ManoUI;
import Vista.Objetos.MazoUI;
import Vista.Objetos.TableroUI;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

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
        btnFinalizarTurno = new javax.swing.JLabel();
        panelFichasArmadas = new javax.swing.JPanel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        GUIjuego.setBackground(new java.awt.Color(0, 0, 0));
        GUIjuego.setLayout(null);

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
        btnFinalizarTurno.setBounds(800, 290, 90, 50);

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
    /**
     * Metodo implementado por la interfaz observer
     *
     * @param modelo el modelo enteró con sus datos actualizados.
     * @param tipoEvento el tipo de evento que se solicito , desde modelo.
     */
    @Override
    public void actualiza(IModelo modelo, TipoEvento tipoEvento) {

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

    }

    public TableroUI getPanelTablero() {
        return tableroUI;
    }
    
    public ManoUI getPanelMano(){
        return manoUI;
    }

    /**
     * Metodo para cargar las imagenes de jugadores aun no terminado.
     */
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
    /**
     * Metodo que pinta los componentes necesarios para la vista.
     * Los crea con su estructura base.
     * @param modelo el modelo de donde se sacan los datos para crearse.
     */
    private void iniciarComponentesDeJuego(IModelo modelo) {
        crearTablero(modelo);
        crearManoUI();
        repintarMano(modelo);
        crearMazo(modelo);
        cargarJugadores();
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

    private javax.swing.JScrollPane scrollPaneMano;
    
    private void crearManoUI() {
        if (manoUI == null) {
        manoUI = new ManoUI();
        manoUI.setSize(580, 120);
        
        // Crear el JScrollPane que contiene la mano
        scrollPaneMano = new JScrollPane(manoUI);
        scrollPaneMano.setLocation(160, 380);
        scrollPaneMano.setSize(580, 120);
        
        // Configurar scroll horizontal y vertical a la IZQUIERDA
        scrollPaneMano.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneMano.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        // IMPORTANTE: Poner el scrollbar vertical a la IZQUIERDA
        scrollPaneMano.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        scrollPaneMano.getHorizontalScrollBar().setUnitIncrement(16); // Scroll más suave
        scrollPaneMano.getVerticalScrollBar().setUnitIncrement(16);
        scrollPaneMano.setBorder(null); // Sin borde
        scrollPaneMano.setOpaque(false);
        scrollPaneMano.getViewport().setOpaque(false);
        
        // Personalizar ancho de la barra de scroll vertical
        scrollPaneMano.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(10, 0));
        
        // Agregar el scrollPane al panel principal
        GUIjuego.add(scrollPaneMano);
    }
    }
    
    /**
     * Metodo que repinta la mano colocandole las fichas necesarias para mostrarse.
     * @param modelo el modelo que pasa los datos a actualizar.
     */
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
            mazoUI.setCursor(new Cursor(HAND_CURSOR) {
            });
            GUIjuego.add(mazoUI);
        }
    }

    private void repintarMazo(IModelo modelo) {
        if (mazoUI != null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI.setNumeroFichasRestantes(String.valueOf(fichasRestantes));
            mazoUI.setCursor(new Cursor(HAND_CURSOR) {
            });
        }
    }
}
