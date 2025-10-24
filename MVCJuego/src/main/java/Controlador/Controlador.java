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
     * (ACTUALIZADO) 1. Le dice al Modelo que coloque la ficha (para la UI
     * local). 2. Serializa la LISTA ENTERA de DTOs a un solo String. 3. Le dice
     * al Ensamblador que envíe ESE ÚNICO String.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        // 1. Actualiza el modelo local PRIMERO
        modelo.colocarFicha(grupos);

        // --- INICIO DE CAMBIOS ---
        if (grupos == null || grupos.isEmpty()) {
            return; // No hay nada que enviar
        }

        // 2. Serializa CADA grupo y únelos con un delimitador (ej: '$')
        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$"); // Delimitador ENTRE grupos
            }
        }
        String payloadCompleto = payloadBuilder.toString();

        // 3. Envía UN SOLO mensaje con el payload completo
        try {
            // (Estos datos deberían ser variables de instancia)
            String miId = "lucianobarcelo";
            String ipServidor = "192.168.1.70";
            int puertoServidor = 8000;

            String mensaje = miId + ":MOVER:" + payloadCompleto;

            System.out.println("[Controlador] Enviando MOVIMIENTO (lote): " + mensaje);
            ensamblador.enviar(ipServidor, puertoServidor, mensaje);

        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
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
            ensamblador.enviar("192.168.1.70", 8000, "lucianobarcelo:REGISTRAR:9002");
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
