package vista;

import TipoEventos.EventoConfig;
import controlador.ControladorConfig;
import javax.swing.JOptionPane;
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
        fondoRummy = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(900, 500));
        getContentPane().setLayout(null);

        txtTitulo.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        txtTitulo.setForeground(new java.awt.Color(255, 255, 255));
        txtTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTitulo.setText("Configurar Partida");
        getContentPane().add(txtTitulo);
        txtTitulo.setBounds(0, 20, 900, 90);

        spinnerComidines.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));
        spinnerComidines.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spinnerComidinesPropertyChange(evt);
            }
        });
        getContentPane().add(spinnerComidines);
        spinnerComidines.setBounds(450, 142, 260, 30);

        btn13Fichas.setBackground(new java.awt.Color(20, 86, 128));
        btn13Fichas.setRoundBottomLeft(30);
        btn13Fichas.setRoundBottomRight(30);
        btn13Fichas.setRoundTopLeft(30);
        btn13Fichas.setRoundTopRight(30);
        btn13Fichas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt13Fichas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txt13Fichas.setForeground(new java.awt.Color(232, 241, 251));
        txt13Fichas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt13Fichas.setText("13 Fichas");
        txt13Fichas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txt13Fichas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt13FichasMouseClicked(evt);
            }
        });
        btn13Fichas.add(txt13Fichas, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 2, 230, 60));

        getContentPane().add(btn13Fichas);
        btn13Fichas.setBounds(470, 210, 240, 60);

        btnCrearPartida.setBackground(new java.awt.Color(25, 84, 160));
        btnCrearPartida.setRoundBottomLeft(30);
        btnCrearPartida.setRoundBottomRight(30);
        btnCrearPartida.setRoundTopLeft(30);
        btnCrearPartida.setRoundTopRight(30);
        btnCrearPartida.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtCrearPartida.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtCrearPartida.setForeground(new java.awt.Color(232, 241, 251));
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

        btn10Fichas.setBackground(new java.awt.Color(20, 86, 128));
        btn10Fichas.setRoundBottomLeft(30);
        btn10Fichas.setRoundBottomRight(30);
        btn10Fichas.setRoundTopLeft(30);
        btn10Fichas.setRoundTopRight(30);
        btn10Fichas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt10Fichas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txt10Fichas.setForeground(new java.awt.Color(232, 241, 251));
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

        btnRegresar.setBackground(new java.awt.Color(232, 108, 108));
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

        txtNumComodines2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtNumComodines2.setForeground(new java.awt.Color(255, 255, 255));
        txtNumComodines2.setText("Comodines: 1");
        getContentPane().add(txtNumComodines2);
        txtNumComodines2.setBounds(190, 102, 200, 30);

        txtFichasCont.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtFichasCont.setForeground(new java.awt.Color(255, 255, 255));
        txtFichasCont.setText("Fichas: ");
        getContentPane().add(txtFichasCont);
        txtFichasCont.setBounds(450, 100, 250, 30);

        fondoRummy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoRummy.jpg"))); // NOI18N
        fondoRummy.setMaximumSize(new java.awt.Dimension(900, 500));
        fondoRummy.setMinimumSize(new java.awt.Dimension(900, 500));
        fondoRummy.setName(""); // NOI18N
        fondoRummy.setPreferredSize(new java.awt.Dimension(900, 500));
        getContentPane().add(fondoRummy);
        fondoRummy.setBounds(-4, -4, 910, 520);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCrearPartidaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCrearPartidaMouseClicked
        int fichasSeleccionadas = this.numFichas;
        int comodinesSeleccionados = (Integer) spinnerComidines.getValue();

        if (fichasSeleccionadas > 0 && comodinesSeleccionados >= 0) {
            control.configurarPartida(comodinesSeleccionados, fichasSeleccionadas);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor selecciona el modo de juego (10 o 13 fichas).", "Faltan datos", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_txtCrearPartidaMouseClicked

    private void txt10FichasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt10FichasMouseClicked
        btn10Fichas.setEnabled(false);
        txt10Fichas.setEnabled(false);
        btn13Fichas.setEnabled(true);
        txt13Fichas.setEnabled(true);

        numFichas = 10;
        txtFichasCont.setText("Fichas: 10Fichas");

    }//GEN-LAST:event_txt10FichasMouseClicked

    private void txt13FichasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt13FichasMouseClicked
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
        this.setVisible(false);
        control.regresarPantallaPrincipal();
    }//GEN-LAST:event_txtNumComodines1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vista.PanelRound btn10Fichas;
    private vista.PanelRound btn13Fichas;
    private vista.PanelRound btnCrearPartida;
    private vista.PanelRound btnRegresar;
    private javax.swing.JLabel fondoRummy;
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
                numComodines = (Integer) spinnerComidines.getValue();

                txtNumComodines2.setText("Comodines: " + numComodines);
            }
        });
    }
}
