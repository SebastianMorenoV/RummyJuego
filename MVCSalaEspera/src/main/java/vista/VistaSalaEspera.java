/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import control.ControlSalaEspera;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import modelo.IModeloSalaEspera;
import modelo.ModeloSalaEspera;
import modelo.ObservadorSalaEspera;
import modelo.TipoEvento;

/**
 *
 * @author benja
 */
public class VistaSalaEspera extends javax.swing.JFrame implements ObservadorSalaEspera {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaSalaEspera.class.getName());
    private ControlSalaEspera control;

    /**
     * Creates new form VistaSalaEspera
     *
     * @param control
     */
    public VistaSalaEspera(ControlSalaEspera control) {
        this.control = control;
        initComponents();
        this.setLocationRelativeTo(null);

        // Enlazar el botón de listo al controlador.
        btnJugadorListoParaIniciarPartida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnJugadorListoParaIniciarPartida.isEnabled()) {
                    control.jugadorPulsaListo();
                }
            }
        });
    }

    // ******************************************************
    // Constructor para PRUEBAS LOCALES (main) ***
    // ******************************************************
    /**
     * Constructor auxiliar para el método main de pruebas. NO debe ser usado
     * por el flujo normal de la aplicación.
     */
    public VistaSalaEspera() {
        // Llama a inicializar componentes para que la UI se dibuje
        initComponents();
        this.setLocationRelativeTo(null);
        if (btnJugadorListoParaIniciarPartida != null) {
            btnJugadorListoParaIniciarPartida.setEnabled(false);
            btnJugadorListoParaIniciarPartida.setText("MODO PRUEBA");
        }
    }

    @Override
    public void actualiza(IModeloSalaEspera iModelo, TipoEvento evt) {
        switch (evt) {
            case SALA:
                ModeloSalaEspera modelo = (ModeloSalaEspera) iModelo;
                Map<String, Boolean> jugadoresListos = modelo.getJugadoresListos();
                String miId = modelo.getMiId(); // ID de la instancia actual (Ej: "Jugador1")

                // Mapeo de componentes de la UI a los slots fijos de la ventana
                JLabel[] nombres = {txtNombreJ1, txtNombreJ2, txtNombreJ3, txtNombreJ4};
                JLabel[] iconosListos = {Jugador1NoListo, Jugador2NoListo, Jugador3NoListo, Jugador4NoListo};
                String[] idsFijos = {"Jugador1", "Jugador2", "Jugador3", "Jugador4"};

                // Carga de íconos desde resources
                ImageIcon iconoListo = new ImageIcon(getClass().getResource("/check.png"));
                ImageIcon iconoNoListo = new ImageIcon(getClass().getResource("/loadingGIF.gif"));

                // Lógica de Repintado: Cargar nombres, identidad (TÚ) y estados (iconos)
                for (int i = 0; i < idsFijos.length; i++) {
                    String id = idsFijos[i]; // ID del slot actual (Ej: "Jugador1" en el primer ciclo)

                    // Ya que el modelo mockea J1-J4, esta condición siempre será verdadera
                    if (modelo.getIdsJugadoresEnSala().contains(id)) {

                        // 1. ASIGNAR NOMBRE E IDENTIDAD (TÚ)
                        // Si el ID del slot coincide con mi ID local, añado (TÚ)
                        String nombreCompleto = id + (id.equals(miId) ? " (TÚ)" : "");
                        nombres[i].setText(nombreCompleto);

                        // 2. ASIGNAR ICONO LISTO
                        boolean estaListo = jugadoresListos.getOrDefault(id, false);
                        iconosListos[i].setIcon(estaListo ? iconoListo : iconoNoListo);
                    } else {
                        // Estado por defecto si el slot no está ocupado (nunca debería ocurrir con este mock)
                        nombres[i].setText("?????????????????");
                        iconosListos[i].setIcon(null);
                    }
                }

                // Lógica del Botón y Título (CU "Iniciar Partida")
                boolean miEstado = jugadoresListos.getOrDefault(miId, false);

                if (modelo.isPartidaLista()) {
                    btnJugadorListoParaIniciarPartida.setText("Partida lista");
                    btnJugadorListoParaIniciarPartida.setEnabled(false);
                    this.setVisible(false); // Ocultar la sala de espera
                    control.iniciarPartidaFinal(); // Llamar al controlador para iniciar el CU de Juego
                    // FIN DE LA LÓGICA DE TRANSICIÓN
                } else if (miEstado) {
                    // El jugador actual está listo, esperando a los demás
                    this.setTitle("Sala de Espera - " + miId + " (¡LISTO!)");
                    btnJugadorListoParaIniciarPartida.setText("Esperando a otros...");
                    btnJugadorListoParaIniciarPartida.setEnabled(false);
                } else {
                    // El jugador actual no se ha declarado listo
                    this.setTitle("Sala de Espera - " + miId);
                    btnJugadorListoParaIniciarPartida.setText("Estoy Listo");
                    btnJugadorListoParaIniciarPartida.setEnabled(true);
                }

                // ----------------------------------------------------
                // LÍNEAS CRUCIALES: FORZAR REDIBUJADO DE LA INTERFAZ
                this.revalidate();
                this.repaint();
                // ----------------------------------------------------

                // Hace visible la ventana (o la mantiene visible si ya lo estaba)
                this.setVisible(true);

                break;

            case CERRAR_CU:
                this.setVisible(false);
                
                break;
        
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnJugadorListoParaIniciarPartida = new javax.swing.JButton();
        tituloVista = new javax.swing.JLabel();
        panelJugador1 = new javax.swing.JPanel();
        txtNombreJ1 = new javax.swing.JLabel();
        panelJugador2 = new javax.swing.JPanel();
        txtNombreJ2 = new javax.swing.JLabel();
        panelJugador3 = new javax.swing.JPanel();
        txtNombreJ3 = new javax.swing.JLabel();
        panelJugador4 = new javax.swing.JPanel();
        txtNombreJ4 = new javax.swing.JLabel();
        iconoJugador1 = new javax.swing.JLabel();
        iconoJugador2 = new javax.swing.JLabel();
        iconoJugador3 = new javax.swing.JLabel();
        iconoJugador4 = new javax.swing.JLabel();
        Jugador1NoListo = new javax.swing.JLabel();
        Jugador2NoListo = new javax.swing.JLabel();
        Jugador3NoListo = new javax.swing.JLabel();
        Jugador4NoListo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(900, 500));
        setMinimumSize(new java.awt.Dimension(900, 500));
        setPreferredSize(new java.awt.Dimension(900, 500));
        setResizable(false);
        setSize(new java.awt.Dimension(900, 500));
        getContentPane().setLayout(null);

        btnJugadorListoParaIniciarPartida.setBackground(new java.awt.Color(80, 118, 78));
        btnJugadorListoParaIniciarPartida.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnJugadorListoParaIniciarPartida.setForeground(new java.awt.Color(255, 255, 255));
        btnJugadorListoParaIniciarPartida.setText("Iniciar Partida");
        btnJugadorListoParaIniciarPartida.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        getContentPane().add(btnJugadorListoParaIniciarPartida);
        btnJugadorListoParaIniciarPartida.setBounds(370, 380, 180, 50);

        tituloVista.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        tituloVista.setForeground(new java.awt.Color(255, 235, 126));
        tituloVista.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tituloVista.setText("Sala de espera");
        getContentPane().add(tituloVista);
        tituloVista.setBounds(0, 30, 900, 90);

        panelJugador1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreJ1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNombreJ1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNombreJ1.setText("?????????????????");
        panelJugador1.add(txtNombreJ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 5, 340, 30));

        getContentPane().add(panelJugador1);
        panelJugador1.setBounds(300, 140, 348, 40);

        panelJugador2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreJ2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNombreJ2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNombreJ2.setText("?????????????????");
        panelJugador2.add(txtNombreJ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 350, 40));

        getContentPane().add(panelJugador2);
        panelJugador2.setBounds(300, 200, 350, 40);

        panelJugador3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreJ3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNombreJ3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNombreJ3.setText("?????????????????");
        panelJugador3.add(txtNombreJ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 5, 350, 30));

        getContentPane().add(panelJugador3);
        panelJugador3.setBounds(300, 260, 350, 40);

        panelJugador4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNombreJ4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNombreJ4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNombreJ4.setText("?????????????????");
        panelJugador4.add(txtNombreJ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 5, 350, 30));

        getContentPane().add(panelJugador4);
        panelJugador4.setBounds(300, 320, 350, 40);

        iconoJugador1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usuario.png"))); // NOI18N
        getContentPane().add(iconoJugador1);
        iconoJugador1.setBounds(250, 140, 40, 40);

        iconoJugador2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usuario.png"))); // NOI18N
        getContentPane().add(iconoJugador2);
        iconoJugador2.setBounds(250, 200, 40, 40);

        iconoJugador3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usuario.png"))); // NOI18N
        getContentPane().add(iconoJugador3);
        iconoJugador3.setBounds(250, 260, 40, 40);

        iconoJugador4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usuario.png"))); // NOI18N
        getContentPane().add(iconoJugador4);
        iconoJugador4.setBounds(250, 320, 40, 40);

        Jugador1NoListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loadingGIF.gif"))); // NOI18N
        getContentPane().add(Jugador1NoListo);
        Jugador1NoListo.setBounds(660, 140, 32, 40);

        Jugador2NoListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loadingGIF.gif"))); // NOI18N
        getContentPane().add(Jugador2NoListo);
        Jugador2NoListo.setBounds(660, 200, 32, 40);

        Jugador3NoListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loadingGIF.gif"))); // NOI18N
        getContentPane().add(Jugador3NoListo);
        Jugador3NoListo.setBounds(660, 260, 32, 40);

        Jugador4NoListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/loadingGIF.gif"))); // NOI18N
        getContentPane().add(Jugador4NoListo);
        Jugador4NoListo.setBounds(660, 320, 32, 40);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fondoRummy.jpg"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(-10, -10, 920, 520);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VistaSalaEspera().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jugador1NoListo;
    private javax.swing.JLabel Jugador2NoListo;
    private javax.swing.JLabel Jugador3NoListo;
    private javax.swing.JLabel Jugador4NoListo;
    private javax.swing.JButton btnJugadorListoParaIniciarPartida;
    private javax.swing.JLabel iconoJugador1;
    private javax.swing.JLabel iconoJugador2;
    private javax.swing.JLabel iconoJugador3;
    private javax.swing.JLabel iconoJugador4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelJugador1;
    private javax.swing.JPanel panelJugador2;
    private javax.swing.JPanel panelJugador3;
    private javax.swing.JPanel panelJugador4;
    private javax.swing.JLabel tituloVista;
    private javax.swing.JLabel txtNombreJ1;
    private javax.swing.JLabel txtNombreJ2;
    private javax.swing.JLabel txtNombreJ3;
    private javax.swing.JLabel txtNombreJ4;
    // End of variables declaration//GEN-END:variables
}
