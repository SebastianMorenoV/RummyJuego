package sockets;

import utils.MensajeEncolado;
import contratos.iDespachador;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Despachador asíncrono para envío de mensajes TCP.
 *
 * @author Sebastian Moreno
 */
public class ClienteTCP implements iDespachador, Runnable {

    private final BlockingQueue<MensajeEncolado> colaDeSalida;
    private volatile boolean ejecutando = true;

    public ClienteTCP() {
        this.colaDeSalida = new LinkedBlockingQueue<>();

        // Inicia el hilo "obrero" que vigila la cola
        new Thread(this).start();
    }

    /**
     * Este es el método que llamará el ProcesadorServidor Su trabajo es rápido:
     * solo añade el mensaje a la cola y termina
     */
    @Override
    public void enviar(String host, int puerto, String mensaje) throws IOException {
        try {
            colaDeSalida.put(new MensajeEncolado(host, puerto, mensaje));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Este es el trabajo del hilo "obrero". Se queda esperando a que lleguen
     * mensajes a la cola y los envía.
     */
    @Override
    public void run() {
        System.out.println("[Despachador Asíncrono] Hilo de envío iniciado.");
        while (ejecutando) {
            try {
                // El hilo se bloquea aquí hasta que haya un mensaje en la cola.
                MensajeEncolado msg = colaDeSalida.take();

                // Cuando hay un mensaje, utiliza el socket para enviarlo.
                enviarDestinatario(msg.host, msg.puerto, msg.mensaje);

            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("[Despachador Asíncrono] Error al enviar mensaje: " + e.getMessage());
            }
        }
    }

    private void enviarDestinatario(String host, int puerto, String mensaje) throws IOException {
        System.out.println("[Despachador] Conectando a " + host + ":" + puerto + "...");

        // Usa los parámetros 'host' y 'puerto', no las variables de la clase.
        try (Socket socket = new Socket(host, puerto); DataOutputStream out
                = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF(mensaje);
            System.out.println("[Despachador] Enviado -> " + mensaje);
        }
    }

    public void detener() {
        this.ejecutando = false;
    }
}
