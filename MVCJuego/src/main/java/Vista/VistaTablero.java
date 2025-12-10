package Vista;

import DTO.FichaJuegoDTO;
import DTO.JugadorDTO;
import Dtos.ActualizacionDTO;
import GestorDeSonidos.GestorSonidos;
import Modelo.IModelo;
import Vista.Objetos.FichaUI;
import Vista.Objetos.JugadorUI;
import Vista.Objetos.ManoUI;
import Vista.Objetos.MazoUI;
import Vista.Objetos.PanelInfoUI;
import Vista.Objetos.TableroUI;
import static Vista.TipoEvento.MOSTRAR_JUEGO;
import contratos.controladoresMVC.iControlEjercerTurno;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Esta clase representa la vistaGeneral de el tablero y todos sus elementos de
 * el juego.
 *
 * @author benja
 */
public class VistaTablero extends javax.swing.JFrame implements Observador {

    private iControlEjercerTurno control;
    private TableroUI tableroUI;
    private ManoUI manoUI;
    private MazoUI mazoUI;
    private boolean yaSeRepartio = false;
    private boolean animacionEnCurso = false; // Bandera para bloquear actualizaciones externas
    private java.util.List<JugadorUI> listaJugadoresUI = new java.util.ArrayList<>();
    private final Border BORDE_HOVER = new LineBorder(new Color(255, 215, 0), 3, true); // Amarillo, 3px, Redondeado
    private PanelInfoUI panelInfo; // <--- Nuevo atributo
    private javax.swing.JLabel lblBannerAlerta; // Nuevo Label
    private Timer timerAlerta; // Timer para ocultarlo
    private List<FichaJuegoDTO> manoCache;

    /**
     * Constructor que recibe el control para poder ejecutar la logica hacia el
     * siguiente componente de MVC.
     *
     * @param control el control de el mvc.
     */
    public VistaTablero(iControlEjercerTurno control) {
        this.control = control;
        this.setSize(920, 550);
        this.setTitle("Rummy Juego");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initComponents();

        configurarHovers();
        initBannerAlerta();
        configurarListenersOrdenamiento();
        configurarIconoVentana();
    }

    private void initBannerAlerta() {
        lblBannerAlerta = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
        lblBannerAlerta.setOpaque(true);
        lblBannerAlerta.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblBannerAlerta.setForeground(Color.WHITE);
        lblBannerAlerta.setVisible(false);

        // Lo agregamos al LayeredPane en una capa MUY alta (POPUP_LAYER)
        getLayeredPane().add(lblBannerAlerta, javax.swing.JLayeredPane.POPUP_LAYER);
    }

    private void mostrarNotificacionGlobal(String mensaje) {
        // Configurar color según urgencia
        if (mensaje.contains("1 ficha")) {
            lblBannerAlerta.setBackground(new Color(220, 53, 69)); // Rojo Alerta
            GestorSonidos.reproducir(GestorSonidos.SONIDO_ENOJO); // Sonido de tensión
        } else {
            lblBannerAlerta.setBackground(new Color(255, 193, 7)); // Amarillo/Naranja Precaución
            GestorSonidos.reproducir(GestorSonidos.SONIDO_EFECTO);
        }

        lblBannerAlerta.setText(mensaje);

        // Posicionar y dimensionar (Centrado arriba)
        int ancho = 400;
        int alto = 40;
        int x = (getWidth() - ancho) / 2;
        int y = 30; // Un poco bajado del borde superior

        lblBannerAlerta.setBounds(x, y, ancho, alto);
        // Borde redondeado y sombra (simple border factory)
        lblBannerAlerta.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE, 2));

        lblBannerAlerta.setVisible(true);

        // Timer para ocultar a los 4 segundos
        if (timerAlerta != null && timerAlerta.isRunning()) {
            timerAlerta.stop();
        }

        timerAlerta = new Timer(4000, e -> {
            lblBannerAlerta.setVisible(false);
        });
        timerAlerta.setRepeats(false);
        timerAlerta.start();
    }

    /**
     * Aplica la magia visual a los botones clave.
     */
    private void configurarHovers() {
        // Botones de ordenamiento (imágenes cuadradas/rectangulares)
        agregarEfectoHover(btnOrdenarMayorAMenor);
        agregarEfectoHover(btnOrdenarPorGrupos);

        // Botón grande de finalizar turno (imagen irregular, el borde será un recuadro)
        agregarEfectoHover(btnFinalizarTurno);

        // Botón Terminar Partida:
        // El trigger es el texto (btnTerminarPartida), pero el que se anima es el panel azul (panelRound1)
        agregarEfectoHover(btnTerminarPartida, panelRound1);
    }

    private void configurarListenersOrdenamiento() {
        btnOrdenarMayorAMenor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                repintarManoOrdenadaPorNumero();
            }
        });

        btnOrdenarPorGrupos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                repintarManoOrdenadaPorGrupos();
            }
        });
    }

    private void agregarEfectoHover(JComponent componente) {
        agregarEfectoHover(componente, componente);
    }

    private void repintarManoOrdenadaPorNumero() {
        if (manoUI == null || this.manoCache == null) {
            return;
        }
        manoUI.removeAll();

        // Usamos la lista cacheada
        List<FichaJuegoDTO> fichasMano = this.manoCache;

        fichasMano.sort(Comparator.comparing((FichaJuegoDTO f) -> f.getColor().toString())
                .thenComparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);

        GestorSonidos.reproducir(GestorSonidos.SONIDO_SOLTAR);
    }

    /**
     * Metodo que repinta la mano del jugador en la interfaz con un orden de
     * "Grupos". CORREGIDO: Usa la manoCache en lugar del DTO que puede venir
     * nulo
     */
    private void repintarManoOrdenadaPorGrupos() {
        if (manoUI == null || this.manoCache == null) {
            return;
        }
        manoUI.removeAll();

        // Usamos la lista cacheada
        List<FichaJuegoDTO> fichasMano = this.manoCache;

        fichasMano.sort(Comparator.comparingInt(FichaJuegoDTO::getNumeroFicha));
        validacionesDeManoUI(fichasMano);

        GestorSonidos.reproducir(GestorSonidos.SONIDO_SOLTAR);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GUIjuego = new javax.swing.JPanel();
        btnFinalizarTurno = new javax.swing.JLabel();
        btnOrdenarMayorAMenor = new javax.swing.JLabel();
        btnOrdenarPorGrupos = new javax.swing.JLabel();
        panelRound1 = new Vista.PanelRound();
        btnTerminarPartida = new javax.swing.JLabel();
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
        btnFinalizarTurno.setBounds(800, 320, 90, 50);

        btnOrdenarMayorAMenor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/button.png"))); // NOI18N
        btnOrdenarMayorAMenor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOrdenarMayorAMenor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrdenarMayorAMenorMouseClicked(evt);
            }
        });
        GUIjuego.add(btnOrdenarMayorAMenor);
        btnOrdenarMayorAMenor.setBounds(820, 280, 50, 30);

        btnOrdenarPorGrupos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/button (1).png"))); // NOI18N
        btnOrdenarPorGrupos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOrdenarPorGrupos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnOrdenarPorGruposMouseClicked(evt);
            }
        });
        GUIjuego.add(btnOrdenarPorGrupos);
        btnOrdenarPorGrupos.setBounds(820, 240, 50, 30);

        panelRound1.setBackground(new java.awt.Color(20, 86, 118));
        panelRound1.setRoundBottomLeft(30);
        panelRound1.setRoundBottomRight(30);
        panelRound1.setRoundTopLeft(30);
        panelRound1.setRoundTopRight(30);

        btnTerminarPartida.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnTerminarPartida.setForeground(new java.awt.Color(255, 255, 255));
        btnTerminarPartida.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnTerminarPartida.setText("Terminar Partida");
        btnTerminarPartida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTerminarPartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnTerminarPartidaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnTerminarPartida, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnTerminarPartida, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        GUIjuego.add(panelRound1);
        panelRound1.setBounds(10, 290, 100, 30);

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoR.png"))); // NOI18N
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
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);
        control.terminarTurno();
        //control.mockGanarPartida();
    }//GEN-LAST:event_btnFinalizarTurnoMouseClicked

    private void btnOrdenarPorGruposMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrdenarPorGruposMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_SOLTAR);
    }//GEN-LAST:event_btnOrdenarPorGruposMouseClicked

    private void btnOrdenarMayorAMenorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnOrdenarMayorAMenorMouseClicked
        GestorSonidos.reproducir(GestorSonidos.SONIDO_SOLTAR);

    }//GEN-LAST:event_btnOrdenarMayorAMenorMouseClicked

    private void btnTerminarPartidaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnTerminarPartidaMouseClicked
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Quieres proponer terminar la partida a votación?",
                "Terminar Partida", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            control.solicitarTerminarPartida();
        }
    }//GEN-LAST:event_btnTerminarPartidaMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GUIjuego;
    private javax.swing.JLabel btnFinalizarTurno;
    private javax.swing.JLabel btnOrdenarMayorAMenor;
    private javax.swing.JLabel btnOrdenarPorGrupos;
    private javax.swing.JLabel btnTerminarPartida;
    private javax.swing.JLabel fondo;
    private Vista.PanelRound panelRound1;
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

        if (dto.getManoDelJugador() != null && !dto.getManoDelJugador().isEmpty()) {
            this.manoCache = dto.getManoDelJugador();
        }
        if (dto.getTipoEvento() == TipoEvento.INCIALIZAR_FICHAS) {

            this.yaSeRepartio = false;

            if (this.yaSeRepartio) {
                return;
            }
            this.yaSeRepartio = true;
            iniciarComponentesDeJuego(modelo, dto);
            animarReparticionInicial(dto.getManoDelJugador());
            habilitarControles(dto.esMiTurno());
            return;
        }

        habilitarControles(dto.esMiTurno());

        DTO.JuegoDTO estadoJuego = modelo.getTablero();

        if (estadoJuego != null) {
            actualizarEstadoJugadores(estadoJuego);
            if (panelInfo != null) {
                String actual = "Desconocido";

                // 1. Obtener el nombre del jugador actual (ya lo hacías bien)
                List<DTO.JugadorDTO> jugadores = estadoJuego.getJugadores();
                if (jugadores != null) {
                    for (DTO.JugadorDTO j : jugadores) {
                        if (j.isEsTurno()) {
                            actual = j.getNombre();
                            break;
                        }
                    }
                }

                // 2. OBTENER EL SIGUIENTE DIRECTAMENTE DEL DTO
                // ¡Ya no calculamos nada aquí! El servidor nos dice la verdad.
                String siguiente = estadoJuego.getSiguienteJugador();
                if (siguiente == null || siguiente.isEmpty()) {
                    siguiente = "-";
                }

                panelInfo.actualizarTurnos(actual, siguiente);
            }
        }
        switch (dto.getTipoEvento()) {
            case MOSTRAR_ALERTA:
                String msg = dto.getMensaje();
                mostrarNotificacionGlobal(msg);
                break;
            case MOSTRAR_JUEGO:
                this.setVisible(true);
                control.cerrarCUAnteriores();
                break;
            case CAMBIO_DE_TURNO:
                if (dto.esMiTurno()) {
                    GestorSonidos.reproducir(GestorSonidos.SONIDO_CAMBIOTURNO);
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
                    GestorSonidos.reproducir(GestorSonidos.SONIDO_ERROR);

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
                if (estadoJuego != null) {
                    actualizarEstadoJugadores(estadoJuego);
                }
                break;

            case NO_ES_MI_TURNO:
                JOptionPane.showMessageDialog(this, "No es tu turno.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case TOMAR_FICHA_POR_FINALIZARTURNO:
                JOptionPane.showMessageDialog(this, "Turno finalizado, tomando ficha... ", "Turno finalizado", JOptionPane.INFORMATION_MESSAGE);
                control.pasarTurno();
                break;
            case PARTIDA_GANADA:

                JOptionPane.showMessageDialog(this,
                        "¡FELICIDADES!\nHas ganado la partida de Rummy.",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE);
                if (panelInfo != null) {
                    panelInfo.detenerCronometro();
                }
                break;

            case PARTIDA_PERDIDA:

                JOptionPane.showMessageDialog(this,
                        "El juego ha terminado.\nOtro jugador se ha quedado sin fichas.",
                        "Fin del Juego",
                        JOptionPane.WARNING_MESSAGE);
                if (panelInfo != null) {
                    panelInfo.detenerCronometro();
                }
                break;

            case NUEVO_MENSAJE_CHAT:
                String payload = dto.getMensaje();
                if (payload != null && payload.contains(":")) {
                    String[] partes = payload.split(":", 2);
                    String nombreEmisor = partes[0];
                    String textoMensaje = partes[1];

                    // --- LÓGICA DE SONIDOS INTELIGENTE ---
                    String mensajeLower = textoMensaje.toLowerCase();

                    if (mensajeLower.contains("jajaja") || mensajeLower.contains("jeje")) {
                        // Si se ríe
                        GestorSonidos.reproducir(GestorSonidos.SONIDO_RISA);

                    } else if (mensajeLower.contains("suerte") || mensajeLower.contains("apúrate")) {
                        // Si molesta o se queja (burla/tensión)
                        GestorSonidos.reproducir(GestorSonidos.SONIDO_RISA);

                    } else if (textoMensaje.contains("😭")) {
                        GestorSonidos.reproducir(GestorSonidos.SONIDO_LLORO);
                    } else if (textoMensaje.contains("😡")) {
                        GestorSonidos.reproducir(GestorSonidos.SONIDO_ENOJO);
                    } else {
                        // Mensaje normal (Hola, Buena jugada, etc.)
                        GestorSonidos.reproducir(GestorSonidos.SONIDO_EFECTO);
                    }

                    // Buscar el JugadorUI correspondiente y mostrar la burbuja
                    for (JugadorUI jUI : listaJugadoresUI) {
                        if (jUI.getNombre().equals(nombreEmisor)) {
                            jUI.mostrarMensaje(textoMensaje);
                            break;
                        }
                    }
                }
                break;
            case RESULTADOS_VOTACION:
                String tabla = modelo.getTablero().getMensaje();
                JOptionPane.showMessageDialog(this, tabla, "Resultados de la Partida", JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
                control.salirAlLobby();
                break;

            case SOLICITUD_VOTO_TERMINAR:
                String solicitante = modelo.getTablero().getMensaje();
                int resp = JOptionPane.showConfirmDialog(this,
                        "El jugador " + solicitante + " Se aburrio .\n¿Tu tambien te aburriste y quieres terminar la partida?",
                        "Votación", JOptionPane.YES_NO_OPTION);
                control.enviarVotoTerminar(resp == JOptionPane.YES_OPTION);
                break;

            case VOTACION_FALLIDA:
                JOptionPane.showMessageDialog(this, "Alguien votó que NO. ¡Seguimos!");
                break;
        }

    }

    /**
     * Metodo para cargar los jugadores aun no terminado (MOCK).
     *
     * @param jugadoresReales
     */
    public void cargarJugadores(List<DTO.JugadorDTO> jugadoresReales) {
        System.out.println("jugadores reales:::: " + jugadoresReales.toString());
        // 1. PRIMERO: Recorremos la lista ACTUAL para quitar los paneles viejos de la ventana
        // (No borres la lista todavía, o perderás la referencia a los objetos que quieres quitar)
        for (JugadorUI p : listaJugadoresUI) {
            GUIjuego.remove(p);
        }
        listaJugadoresUI.clear();
        GUIjuego.repaint();

        if (jugadoresReales == null) {
            return;
        }

        // ... (definicion de Point[] posiciones) ...
        Point[] posiciones = {new Point(-10, 380), new Point(-10, -10), new Point(780, -10), new Point(780, 380)};

        int indexPos = 0;

        // OBTENEMOS MI NOMBRE DEL CONTROLADOR PARA DEPURAR
        String miNombreLocal = control.getNombreJugadorLocal();
        System.out.println("[VISTA DEBUG] Soy: " + miNombreLocal);

        for (DTO.JugadorDTO dto : jugadoresReales) {
            if (indexPos >= 4) {
                break;
            }

            byte[] avatarBytes = cargarImagenPorIndice(dto.getIdAvatar());
            JugadorUI panelJugador = new JugadorUI(dto.getNombre(), dto.getFichasRestantes(), avatarBytes);
            panelJugador.setSize(130, 130);
            panelJugador.setLocation(posiciones[indexPos]);

            // --- CORRECCION DE IDENTIFICACION ---
            // Imprimimos qué estamos comparando para ver por qué falla
            System.out.println("   -> Comparando DTO(" + dto.getNombre() + ") vs LOCAL(" + miNombreLocal + ")");

            // INTENTO DE MATCH: Comparamos directamente Strings
            boolean esYo = dto.getNombre().equals(miNombreLocal);

            // Si falla y miNombreLocal parece un ID (empieza con Jugador_), tal vez el DTO tiene un campo ID que no estamos viendo,
            // pero por ahora confiaremos en el nombre.
            panelJugador.setEsMiJugador(esYo, e -> {
                String mensaje = e.getActionCommand();
                control.enviarMensajeChat(mensaje);
            });

            GUIjuego.add(panelJugador);
            listaJugadoresUI.add(panelJugador);
            indexPos++;
        }
        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    /**
     *
     * @param indice
     * @return
     */
    private byte[] cargarImagenPorIndice(int indice) {
        String rutaRecurso = "/avatares/avatar" + indice + ".png";
        try (java.io.InputStream is = getClass().getResourceAsStream(rutaRecurso)) {
            if (is != null) {
                return is.readAllBytes();
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar avatar: " + rutaRecurso);
        }
        return null;
    }

    /**
     * Metodo que pinta los componentes necesarios para la vista. Los crea con
     * su estructura base.
     *
     * @param modelo el modelo de donde se sacan los datos para crearse.
     */
    private void iniciarComponentesDeJuego(IModelo modelo, ActualizacionDTO dto) {

        crearTablero(modelo);
        if (tableroUI != null) {
            tableroUI.limpiarTablero();
        }
        // --- AGREGAR ESTO AQUÍ ---
        crearPanelInfo();
        panelInfo.iniciarCronometro(); // Arrancar el tiempo al iniciar
        // -------------------------
        crearManoUI();

//        repintarMano(modelo, dto);
        crearMazo(modelo);

        // 1. Recuperamos el estado actual del juego desde el modelo
        DTO.JuegoDTO estadoJuego = modelo.getTablero();

        // 2. Extraemos la lista de jugadores reales (con Avatares y Nombres del BB)
        List<DTO.JugadorDTO> listaJugadores = (estadoJuego != null) ? estadoJuego.getJugadores() : null;

        // 3. Llamamos a cargarJugadores pasándole la lista
        cargarJugadores(listaJugadores);

        if (estadoJuego != null) {
            actualizarEstadoJugadores(estadoJuego);
        }

        btnFinalizarTurno.setVisible(false);

        if (fondo.getParent() == GUIjuego) {
            GUIjuego.setComponentZOrder(fondo, GUIjuego.getComponentCount() - 1);
        }

        GUIjuego.revalidate();
        GUIjuego.repaint();
    }

    // --- NUEVO MÉTODO DE CREACIÓN ---
    private void crearPanelInfo() {
        if (panelInfo == null) {
            panelInfo = new PanelInfoUI();
            // Posición: Izquierda (x=10), un poco abajo del tope (y=20)
            panelInfo.setBounds(5, 150, 110, 125);
            GUIjuego.add(panelInfo);
            GUIjuego.setComponentZOrder(panelInfo, 0); // Traer al frente
        }
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
            manoUI.setSize(580, 120);

            JScrollPane scrollPane = new JScrollPane(
                    manoUI,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );

            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

            verticalBar.setUI(new BasicScrollBarUI() {

                @Override
                protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
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

                private JButton createZeroButton() {
                    JButton button = new JButton();
                    Dimension zeroDim = new Dimension(0, 0);
                    button.setPreferredSize(zeroDim);
                    button.setMinimumSize(zeroDim);
                    button.setMaximumSize(zeroDim);
                    return button;
                }
            });

            verticalBar.setOpaque(false);

            verticalBar.setPreferredSize(new Dimension(10, 0));

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
        if (this.animacionEnCurso) {
            return;
        }

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

    /**
     * Método auxiliar para actualizar bordes y contadores de TODOS los
     * jugadores
     */
    private void actualizarEstadoJugadores(DTO.JuegoDTO estadoJuego) {
        String nombreJugadorEnTurno = estadoJuego.getJugadorActual();
        List<DTO.JugadorDTO> listaDatos = estadoJuego.getJugadores();

        if (listaDatos == null || listaDatos.isEmpty()) {
            return;
        }

        // El Modelo SIEMPRE debe mandar la lista ordenada: [YO, Rival1, Rival2, Rival3]
        for (int i = 0; i < listaDatos.size(); i++) {
            if (i >= listaJugadoresUI.size()) {
                break;
            }

            JugadorUI uiPanel = listaJugadoresUI.get(i);
            DTO.JugadorDTO datosNuevos = listaDatos.get(i);

            // Actualizar informacion dinámica
            uiPanel.setFichasRestantes(datosNuevos.getFichasRestantes());

            boolean esSuTurno = datosNuevos.isEsTurno();

            uiPanel.setEsTuTurno(esSuTurno);
        }
        GUIjuego.repaint();
    }

    /**
     * Obtiene el panel de interfaz de usuario (UI) que representa visualmente
     * el tablero de juego. Este panel contiene los grupos de fichas colocados
     * por los jugadores.
     *
     * @return El objeto TableroUI que es el componente visual del tablero.
     */
    public TableroUI getPanelTablero() {
        return tableroUI;
    }

    /**
     * Obtiene el panel de interfaz de usuario (UI) que representa visualmente
     * la mano o área de fichas del jugador actual.
     *
     * @return El objeto ManoUI que es el componente visual de la mano del
     * jugador.
     */
    public ManoUI getPanelMano() {
        return this.manoUI;
    }

    /**
     * --- ANIMACIÓN DE REPARTO (MODO PRO) --- Crea fichas voladoras desde el
     * mazo hacia la mano.
     */
    private void animarReparticionInicial(List<FichaJuegoDTO> fichasMano) {
        if (fichasMano == null || fichasMano.isEmpty()) {
            return;
        }
        if (mazoUI == null || manoUI == null) {
            return;
        }

        // 1. Bloqueamos actualizaciones externas y limpiamos
        this.animacionEnCurso = true;
        manoUI.removeAll();
        manoUI.revalidate();
        manoUI.repaint();

        Point mazoPos = SwingUtilities.convertPoint(mazoUI, 0, 0, getLayeredPane());

        Timer dealTimer = new Timer(200, null);
        final int[] index = {0};

        dealTimer.addActionListener(e -> {
            // VERIFICACIÓN DE FIN DE ANIMACIÓN
            if (index[0] >= fichasMano.size()) {
                dealTimer.stop();

                // [CORRECCIÓN] Liberamos el bloqueo para permitir actualizaciones futuras
                this.animacionEnCurso = false;

                manoUI.revalidate();
                manoUI.repaint();
                return;
            }

            FichaJuegoDTO dto = fichasMano.get(index[0]);
            Point destinoLocal = manoUI.calcularPosicionIndice(index[0]);
            Point destinoGlobal = SwingUtilities.convertPoint(manoUI, destinoLocal, getLayeredPane());

            lanzarFichaVoladora(dto, mazoPos, destinoGlobal);

            index[0]++;
        });
        dealTimer.start();

    }

    /**
     * Crea una ficha temporal en el aire y la mueve hacia el destino.
     */
    private void lanzarFichaVoladora(FichaJuegoDTO dto, Point start, Point end) {
        // Crear Ficha visual
        FichaUI fichaVoladora = new FichaUI(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin(), control, this);
        fichaVoladora.setSize(28, 45); // Tamaño standar
        fichaVoladora.setLocation(start);

        // Agregar al LayeredPane en capa DRAG (muy arriba)
        getLayeredPane().add(fichaVoladora, javax.swing.JLayeredPane.DRAG_LAYER);
        getLayeredPane().repaint(); // Forzar pintado inmediato

        // Sonido de reparto "swish"
        GestorSonidos.reproducir(GestorSonidos.SONIDO_CLICK);

        // Timer de Animación (Interpolación)
        // Se mueve cada 10ms, tarda 400ms en llegar
        final long startTime = System.currentTimeMillis();
        final int duration = 400;

        Timer animTimer = new Timer(10, null);
        animTimer.addActionListener(e -> {
            long now = System.currentTimeMillis();
            float progress = (float) (now - startTime) / duration;

            if (progress >= 1.0f) {
                // FIN DEL VIAJE
                animTimer.stop();
                getLayeredPane().remove(fichaVoladora); // Borrar la voladora
                getLayeredPane().repaint();

                // AGREGAR LA REAL a la mano
                // Creamos una nueva instancia para la mano (o reusamos, pero mejor nueva limpia)
                FichaUI fichaFinal = new FichaUI(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin(), control, this);
                fichaFinal.setOrigen(FichaUI.Origen.MANO);
                manoUI.agregarFicha(fichaFinal); // Se añade y ManoUI la acomoda
            } else {
                // Easing (suavizado de movimiento)
                // Usamos una función cúbica para que frene al llegar
                double ease = 1 - Math.pow(1 - progress, 3);

                int newX = (int) (start.x + (end.x - start.x) * ease);
                int newY = (int) (start.y + (end.y - start.y) * ease);
                fichaVoladora.setLocation(newX, newY);
            }
        });
        animTimer.start();
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
