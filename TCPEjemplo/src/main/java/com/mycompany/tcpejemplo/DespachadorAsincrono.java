package com.mycompany.tcpejemplo;
// Archivo: DespachadorAsincrono.java


import com.mycompany.tcpejemplo.utils.MensajeEncolado;
import com.mycompany.tcpejemplo.sockets.ClienteTCP;
import com.mycompany.tcpejemplo.interfaces.iDespachador;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DespachadorAsincrono implements iDespachador, Runnable {
    
    private final BlockingQueue<MensajeEncolado> colaDeSalida;
    private final iDespachador transportista; // El ClienteTCP real
    private volatile boolean ejecutando = true;

    public DespachadorAsincrono() {
        this.colaDeSalida = new LinkedBlockingQueue<>();
        this.transportista = new ClienteTCP(); // El "obrero" que sabe cómo enviar de verdad
        
        // Inicia el hilo "obrero" que vigila la cola.
        new Thread(this).start();
    }

    /**
     * Este es el método que llamará el ProcesadorServidor.
     * Su trabajo es rápido: solo añade el mensaje a la cola y termina.
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
     * Este método es para el cliente, no se usa en el servidor.
     * Lo implementamos por la interfaz, pero lanzamos una excepción.
     */
    @Override
    public void enviar(String mensaje) throws IOException {
        throw new UnsupportedOperationException("Este despachador es para el servidor y requiere un destino.");
    }
    
    /**
     * Este es el trabajo del hilo "obrero".
     * Se queda esperando a que lleguen mensajes a la cola y los envía.
     */
    @Override
    public void run() {
        System.out.println("[Despachador Asíncrono] Hilo de envío iniciado.");
        while (ejecutando) {
            try {
                // El hilo se bloquea aquí hasta que haya un mensaje en la cola.
                MensajeEncolado msg = colaDeSalida.take();
                
                // Cuando hay un mensaje, usa el transportista real para enviarlo.
                transportista.enviar(msg.host, msg.puerto, msg.mensaje);
                
            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("[Despachador Asíncrono] Error al enviar mensaje: " + e.getMessage());
            }
        }
    }

    public void detener() {
        this.ejecutando = false;
    }
}