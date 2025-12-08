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
 * Implementa el patrón Productor-Consumidor usando una cola de bloqueo 
 * para desacoplar el proceso de solicitud de envío de la operación de red.
 *
 * @author Sebastian Moreno
 */
public class ClienteTCP implements iDespachador, Runnable {

    private final BlockingQueue<MensajeEncolado> colaDeSalida;
    private volatile boolean ejecutando = true;

    /**
     * Constructor. Inicializa la cola de mensajes y comienza un hilo de ejecución 
     * dedicado (hilo consumidor) para el procesamiento y envío asíncrono de datos.
     */
    public ClienteTCP() {
        this.colaDeSalida = new LinkedBlockingQueue<>();

        new Thread(this).start();
    }

    /**
     * Encola un mensaje para su procesamiento asíncrono.
     * Este método actúa como el productor, aceptando la solicitud de envío 
     * y depositándola rápidamente en la cola de salida para ser gestionada 
     * por el hilo de red.
     * @param host La dirección IP del destinatario.
     * @param puerto El puerto del destinatario.
     * @param mensaje El contenido de texto a enviar.
     * @throws IOException Si la operación de encolamiento es interrumpida.
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
     * Lógica de ejecución del hilo consumidor de mensajes.
     * El hilo se mantiene en un ciclo de escucha activo, esperando (`take()`) a que 
     * existan mensajes en la cola para extraerlos y ejecutarlos a través de la red.
     */
    @Override
    public void run() {
        System.out.println("[Despachador Asíncrono] Hilo de envío iniciado.");
        while (ejecutando) {
            try {
                MensajeEncolado msg = colaDeSalida.take();

                enviarDestinatario(msg.host, msg.puerto, msg.mensaje);

            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("[Despachador Asíncrono] Error al enviar mensaje: " + e.getMessage());
            }
        }
    }

    /**
     * Ejecuta la conexión y envío real del mensaje utilizando sockets TCP.
     * Esta operación es bloqueante (síncrona) y se ejecuta dentro del hilo "obrero" del despachador.
     * * @param host La dirección IP del destino.
     * @param puerto El puerto del destino.
     * @param mensaje El contenido del mensaje a enviar.
     * @throws IOException Si la conexión o la escritura fallan.
     */
    private void enviarDestinatario(String host, int puerto, String mensaje) throws IOException {
        System.out.println("[Despachador] Conectando a " + host + ":" + puerto + "...");

        try (Socket socket = new Socket(host, puerto); DataOutputStream out
                = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF(mensaje);
            System.out.println("[Despachador] Enviado -> " + mensaje);
        }
    }

    /**
     * Señaliza al hilo de envío para que termine su ejecución en el siguiente ciclo.
     */
    public void detener() {
        this.ejecutando = false;
    }
}
