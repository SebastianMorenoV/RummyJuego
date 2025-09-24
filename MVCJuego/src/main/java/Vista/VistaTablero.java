package Vista;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.Objetos.FichaUI;
import Vista.Objetos.JugadorUI;
import Vista.Objetos.ManoUI;
import Vista.Objetos.MazoUI;
import Vista.Objetos.TableroUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

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
        btnFinalizarTurno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        control.terminarTurno();
    }//GEN-LAST:event_btnFinalizarTurnoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GUIjuego;
    private javax.swing.JLabel btnFinalizarTurno;
    private javax.swing.JLabel fondo;
    private javax.swing.JPanel panelFichasArmadas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actualiza(IModelo modelo, TipoEvento tipoEvento) {

        switch (tipoEvento) {
            case INCIALIZAR_FICHAS:
                iniciarJuego(modelo);
                break;
            case REPINTAR_MANO:
                repintarMano(modelo, control, manoUI);
                break;
            case ACTUALIZAR_TABLERO:
                pintarJugadaTablero(modelo, control);
                break;
            case TOMO_FICHA:
                repintarMazo(modelo, control);
                break;
            case DEVOLVER_FICHAS_TABLERO:
                tableroUI.repintarTablero();
        }
    }

    public void pintarJugadaTablero(IModelo modelo, Controlador controlador) {
        JuegoDTO juego = modelo.getTablero();
        List<GrupoDTO> grupos = juego.getGruposEnTablero();
        panelFichasArmadas.removeAll();
        panelFichasArmadas.setBackground(new Color(23, 57, 134));
        panelFichasArmadas.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFichasArmadas.add(new JScrollBar());

        // Para cada grupo
        for (GrupoDTO grupoDTO : grupos) {
            // Ignorar grupos no establecidos
            if (grupoDTO.getTipo().equals("No establecido")) {
                continue; // salta a la siguiente iteración
            }

            // Crear un panel para el grupo
            JPanel panelGrupo = new JPanel();
            panelGrupo.setBorder(BorderFactory.createTitledBorder(grupoDTO.getTipo()));
            panelGrupo.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

            // Recorrer las fichas del grupo
            for (FichaJuegoDTO fichaDTO : grupoDTO.getFichasGrupo()) {
                // Crear un FichaUI en lugar de JButton
                FichaUI fichaUI = new FichaUI(
                        fichaDTO.getIdFicha(),
                        fichaDTO.getNumeroFicha(),
                        fichaDTO.getColor(),
                        fichaDTO.isComodin(),
                        controlador, this
                );
                for (MouseListener ml : fichaUI.getMouseListeners()) {
                    fichaUI.removeMouseListener(ml);
                }
                for (MouseMotionListener mml : fichaUI.getMouseMotionListeners()) {
                    fichaUI.removeMouseMotionListener(mml);
                }
                fichaUI.setPreferredSize(new Dimension(20, 50));
                panelGrupo.add(fichaUI);
            }
            // Agregar el panel del grupo al tablero
            panelFichasArmadas.add(panelGrupo);
        }
        panelFichasArmadas.revalidate();
        panelFichasArmadas.repaint();
    }

    /**
     * Es utilizado para iniciar el juego (Pinta las fichas dentro de la mano,
     * crea un tablero, crea el mazo,carga los jugadores.)
     *
     * @param modelo El modelo que se encarga de enviar datos.
     */
    public void iniciarJuego(IModelo modelo) {
        List<FichaJuegoDTO> fichasMano = modelo.getMano();

        // Eliminar fichas antiguas , si es que existe.
        for (Component c : GUIjuego.getComponents()) {
            if (c instanceof FichaUI && ((FichaUI) c).getOrigen() == FichaUI.Origen.MANO) {
                GUIjuego.remove(c);
            }
        }
        cargarJugadores();
        crearMano(modelo);
        crearTablero(modelo);
        crearMazo(modelo);
        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    /**
     * Metodo con logica de presentacion para borrar y repintar la mano.
     *
     * @param modelo Clase que se lleva la logica de el programa.
     * @param controlador Controlador que llama a modelo.
     * @param manoUI objeto presentacion que representa una mano.
     */
    public void repintarMano(IModelo modelo, Controlador controlador, ManoUI manoUI) {
        // Limpiar todo el panel mano para sobreponer fichas.
        manoUI.removeAll();
        List<FichaJuegoDTO> fichasMano = modelo.getMano();

        // Posicion inicial de las fichas dentro de ManoUI.
        int xPos = 10;
        int yPos = 10;

        // Dibujar fichas
        for (FichaJuegoDTO fichaDTO : fichasMano) {
            FichaUI fichaUI = new FichaUI(
                    fichaDTO.getIdFicha(),
                    fichaDTO.getNumeroFicha(),
                    fichaDTO.getColor(),
                    fichaDTO.isComodin(),
                    controlador, this
            );
            fichaUI.setOrigen(FichaUI.Origen.MANO);
            fichaUI.setSize(25, 45);
            fichaUI.setLocation(xPos, yPos);
            fichaUI.setOpaque(false);

            manoUI.add(fichaUI);
            manoUI.setComponentZOrder(fichaUI, 0);

            xPos += 40; // Espacio entre fichas.
        }
        manoUI.revalidate();
        manoUI.repaint();
    }

    /**
     * Este metodo repinta el mazo , funciona para actualizar el numero de
     * fichas que quedan.
     *
     * @param modelo el modelo pasado como parametro por la vista.
     * @param control el control pasado por la vista.
     */
    public void repintarMazo(IModelo modelo, Controlador control) {
        // 1. Obtén el número de fichas restantes
        int fichasRestantes = modelo.getTablero().getFichasMazo();
        if (this.mazoUI != null) {
            GUIjuego.remove(this.mazoUI);
        }
        // 3. Creas una nueva instancia del MazoUI con el número actualizado
        this.mazoUI = new MazoUI(String.valueOf(fichasRestantes), control);
        this.mazoUI.setLocation(800, 150);
        this.mazoUI.setSize(70, 90);
        this.mazoUI.setOpaque(false);
        GUIjuego.add(this.mazoUI);
        GUIjuego.setComponentZOrder(this.mazoUI, GUIjuego.getComponentCount() - 2);
        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    public void cargarJugadores() {
        String rutaImagen = "src/main/resources/avatares/avatar.png";

        try {
            // Convierte la cadena de texto de la ruta en un objeto Path
            Path path = new File(rutaImagen).toPath();

            // Lee todos los bytes del archivo
            byte[] imagenAvatarBytes = Files.readAllBytes(path);
            System.out.println("Imagen cargada exitosamente. Tamaño: " + imagenAvatarBytes.length + " bytes.");

            // 2. Crea los objetos JugadorUI, pasándoles la misma imagen en bytes ya cargada
            JugadorUI jugador1 = new JugadorUI("Sebastian", 7, imagenAvatarBytes);
            jugador1.setSize(150, 150);
            jugador1.setLocation(-10, -10);
            GUIjuego.add(jugador1);
            GUIjuego.setComponentZOrder(jugador1, GUIjuego.getComponentCount() - 2);

            JugadorUI jugador2 = new JugadorUI("Benjamin", 15, imagenAvatarBytes);
            jugador2.setSize(150, 150);
            jugador2.setLocation(-10, 360);
            GUIjuego.add(jugador2);
            GUIjuego.setComponentZOrder(jugador2, GUIjuego.getComponentCount() - 2);

            JugadorUI jugador3 = new JugadorUI("Luciano", 10, imagenAvatarBytes);
            jugador3.setSize(150, 150);
            jugador3.setLocation(760, -10);
            GUIjuego.add(jugador3);
            GUIjuego.setComponentZOrder(jugador3, GUIjuego.getComponentCount() - 2);

            JugadorUI jugador4 = new JugadorUI("Mr.Fitch", 5, imagenAvatarBytes);
            jugador4.setSize(150, 150);
            jugador4.setLocation(760, 360);
            GUIjuego.add(jugador4);
            GUIjuego.setComponentZOrder(jugador4, GUIjuego.getComponentCount() - 2);

        } catch (IOException e) {
            System.err.println("Error: No se pudo encontrar o leer el archivo de imagen en la ruta: " + rutaImagen);
            e.printStackTrace();
        }
    }

    public void crearMano(IModelo modelo) {
        //Crear Mano
        manoUI = new ManoUI();
        manoUI.setLocation(160, 380);
        manoUI.setSize(580, 120);
        GUIjuego.add(manoUI);
        GUIjuego.setComponentZOrder(manoUI, GUIjuego.getComponentCount() - 2);
        repintarMano(modelo, control, manoUI);
    }

    public void crearTablero(IModelo modelo) {
        //Crear Tablero
        tableroUI = new TableroUI(modelo, control, this);
        tableroUI.setLocation(130, 130);
        tableroUI.setSize(640, 242);
        tableroUI.setOpaque(false);
        GUIjuego.add(tableroUI);
        GUIjuego.setComponentZOrder(tableroUI, GUIjuego.getComponentCount() - 2);
    }

    public void crearMazo(IModelo modelo) {
        // Obtener las dimensiones de el panel mano.
        int fichasRestantes = modelo.getTablero().getFichasMazo();
        System.out.println("Fichas restantes :" + fichasRestantes);
        mazoUI = new MazoUI(String.valueOf(fichasRestantes), control);
        mazoUI.setLocation(800, 150);
        mazoUI.setSize(70, 90);
        mazoUI.setOpaque(false);

        GUIjuego.add(mazoUI);
        GUIjuego.setComponentZOrder(mazoUI, GUIjuego.getComponentCount() - 2);
        repintarMazo(modelo, control);
    }

    //Gets and sets
    public TableroUI getPanelTablero() {
        return tableroUI;
    }
}
