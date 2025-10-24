package Controlador;

import DTO.GrupoDTO;
import Modelo.Modelo;
import com.mycompany.tcpejemplo.Ensamblador;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moren
 */
public class Controlador {

    Ensamblador ensamblador = new Ensamblador();
    Modelo modelo;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }

    /**
     * Metodo que le habla al modelo para iniciar el juego.
     */
    public void iniciarJuego() {
        modelo.iniciarJuego();
    }

    /**
     * Metodo que le habla al modelo para pasar el Turno.
     */
    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    /**
     * (ACTUALIZADO) Metodo que le habla al modelo para colocar fichas y
     * notifica al servidor.
     *
     * @param grupos lista de grupos con las fichas a colocar.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        // 1. Actualiza el modelo local PRIMERO
        modelo.colocarFicha(grupos);

        // --- INICIO DE CAMBIOS ---
        // Datos de conexión (deberían ser variables, pero seguimos tu ejemplo)
        String miId = "lucianobarcelo";
        String ipServidor = "192.168.1.70";
        int puertoServidor = 8000;

        // 2. Itera sobre CADA grupo que el jugador colocó
        for (GrupoDTO grupo : grupos) {
            try {
                // 3. Serializa el grupo a un string plano
                String payload = grupo.serializarParaPayload();

                // 4. Construye el mensaje de red correcto
                //    Formato:  ID:COMANDO:PAYLOAD
                String mensaje = miId + ":MOVER:" + payload;

                // 5. Envía el mensaje al servidor (que lo reenviará a todos)
                System.out.println("[Controlador] Enviando MOVIMIENTO: " + mensaje);
                ensamblador.enviar(ipServidor, puertoServidor, mensaje);

            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // --- FIN DE CAMBIOS ---
    }

    /**
     * Metodo que le habla al modelo para terminar el turno de un jugador.
     */
    public void terminarTurno() {
        modelo.terminarTurno();
    }

    /**
     * Metodo que le habla al modelo para regresar una ficha desde el tablero a
     * la mano.
     *
     * @param idFicha id de la ficha a regresar.
     */
    public void regresarFichaAMano(int idFicha) {
        modelo.regresarFichaAMano(idFicha);
    }

    public void crearYUnirseAPartida() {
        ensamblador.configurarComoServidor();
        new Thread(() -> {
            try {
                ensamblador.iniciarListener(8000);
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        ensamblador.configurarComoCliente("lucianobarcelo", this.modelo);

        new Thread(() -> {
            try {
                ensamblador.iniciarListener(9002);
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        try {
            ensamblador.enviar("192.168.1.70", 8000, "lucianobarcelo:REGISTRAR:9001");
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
