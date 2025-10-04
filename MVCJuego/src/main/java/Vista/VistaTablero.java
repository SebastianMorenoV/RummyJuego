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
import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.JLabel;
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
    private final int idJugador;
    private boolean esMiTurno = false;

    /**
     * Constructor que recibe el control para poder ejecutar la logica hacia el
     * siguiente componente de MVC.
     *
     * @param control el control de el mvc.
     */
    public VistaTablero(Controlador control, int idJugador) {
        this.control = control;
        this.idJugador = idJugador;
        this.setSize(920, 550);
        this.setTitle("Rummy Juego");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        initComponents();
    }

    @Override
    public int getIdJugador() {
        return this.idJugador;
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GUIjuego = new javax.swing.JPanel();
        btnOrdenarMayorAMenor = new javax.swing.JLabel();
        btnOrdenarPorGrupos = new javax.swing.JLabel();
        btnFinalizarTurno = new javax.swing.JLabel();
        panelFichasArmadas = new javax.swing.JPanel();
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
        if (esMiTurno) {
            control.terminarTurno();
        }
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
                actualizarEstadoTurno(modelo);
                break;
            case TURNO_CAMBIADO:
                actualizarEstadoTurno(modelo); // Actualiza el título y los controles
                if (tableroUI != null) {
                    
                    tableroUI.repintarTablero(true);
                }
                repintarMano(modelo); // Repinta la mano 
                repintarMazo(modelo); // Actualiza el contador de fichas del mazo
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

        btnOrdenarMayorAMenor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                repintarManoOrdenadaPorNumero(modelo);
            }
        });

        btnOrdenarPorGrupos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                repintarManoOrdenadaPorGrupos(modelo);
            }
        });

    }

    /**
     * *
     * Comprobar el turno con un false o true. Devuelve el resultado.
     */
    public boolean esMiTurno() {
        return this.esMiTurno;
    }

    private void actualizarEstadoTurno(IModelo modelo) {
        JuegoDTO juegoDTO = modelo.getTablero();
        this.esMiTurno = ("Jugador " + (this.idJugador + 1)).equals(juegoDTO.getJugadorActual());

        // Dependiendo si es turno del jugador o no, se activa o desactiva el uso del mazo
        this.btnFinalizarTurno.setEnabled(this.esMiTurno);
        if (mazoUI != null) {
            this.mazoUI.setEnabled(this.esMiTurno);
        }

        // Igualmente con el tablero y mano
        if (manoUI != null) {
            this.manoUI.setEnabled(this.esMiTurno);
        }
        if (tableroUI != null) {
            this.tableroUI.setEnabled(this.esMiTurno);
        }

        if (this.esMiTurno) {
            this.setTitle("Rummy Juego - Tu turno");
        } else {
            this.setTitle("Rummy Juego - Esperando a " + juegoDTO.getJugadorActual());
        }
    }

    public TableroUI getPanelTablero() {
        return tableroUI;
    }

    public ManoUI getPanelMano() {
        return this.manoUI;
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
     * Metodo que pinta los componentes necesarios para la vista. Los crea con
     * su estructura base.
     *
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
            manoUI.setLocation(160, 380);
            manoUI.setSize(580, 120);
            GUIjuego.add(manoUI);
        }
    }

    /**
     * Metodo que repinta la mano colocandole las fichas necesarias para
     * mostrarse.
     *
     * @param modelo el modelo que pasa los datos a actualizar.
     */
    private void repintarMano(IModelo modelo) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = modelo.getMano();

        validacionesDeManoUI(fichasMano);
    }

    private void crearMazo(IModelo modelo) {
        if (mazoUI == null) {
            int fichasRestantes = modelo.getTablero().getFichasMazo();
            mazoUI = new MazoUI(String.valueOf(fichasRestantes), control);
            mazoUI.setLocation(807, 140);
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

    private void repintarManoOrdenadaPorGrupos(IModelo modelo) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = modelo.getMano();

        // Ordenamos la lista localmente
        fichasMano.sort(Comparator.comparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);

    }

    private void repintarManoOrdenadaPorNumero(IModelo modelo) {
        if (manoUI == null) {
            return;
        }
        manoUI.removeAll();

        List<FichaJuegoDTO> fichasMano = modelo.getMano();

        // CUIDADO: Ordenamos la lista SÓLO para esta vista.
        fichasMano.sort(Comparator.comparing((FichaJuegoDTO f) -> f.getColor().toString())
                .thenComparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);
    }

    public void validacionesDeManoUI(List<FichaJuegoDTO> fichasMano) {
        // APLICAMOS LA LÓGICA DE FILTRADO QUE TIENES
        Collection<FichaUI> fichasEnTablero = tableroUI.getFichasEnTablero().values();
        Collection<FichaUI> fichasValidasEnTablero = tableroUI.getFichasEnTableroValidas().values();

        for (FichaJuegoDTO fichaDTO : fichasMano) {
            boolean yaEstaEnTablero = false;
            for (FichaUI fichaTablero : fichasEnTablero) {
                if (fichaTablero.getIdFicha() == fichaDTO.getIdFicha()) {
                    yaEstaEnTablero = true;
                    break; // La encontramos, no hace falta seguir buscando en esta lista.
                }
            }

            if (!yaEstaEnTablero) { // Si aún no la hemos encontrado, buscamos en la otra lista.
                for (FichaUI fichaTablero : fichasValidasEnTablero) {
                    if (fichaTablero.getIdFicha() == fichaDTO.getIdFicha()) {
                        yaEstaEnTablero = true;
                        break; // La encontramos, salimos del bucle.
                    }
                }
            }

            // 4. Si después de buscar en ambas listas, la ficha NO está en el tablero, la agregamos a la mano.
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

}
