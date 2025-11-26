// Archivo: ServerTCP.java (Reemplaza completamente el anterior)
package sockets;

import utils.PeticionCliente;
import contratos.iListener;
import contratos.iProcesador;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Servidor TCP basado en cola (productor–consumidor). Evita bloqueos y permite
 * procesar muchas peticiones concurrentes.
 *
 * @author chris
 */
public class ServerTCP implements iListener {

    private final iProcesador procesador;
    private final BlockingQueue<PeticionCliente> colaDeEntrada;
    private volatile boolean ejecutando = true;
    private ServerSocket serverSocket;

    public ServerTCP(iProcesador procesador) {
        if (procesador == null) {
            throw new IllegalArgumentException("El procesador no puede ser nulo");
        }
        this.procesador = procesador;
        this.colaDeEntrada = new LinkedBlockingQueue<>();

        new Thread(this::procesarCola).start();
    }

    /**
     * El hilo del "Mesero". Su único trabajo es aceptar conexiones, leer el
     * mensaje y ponerlo en la cola. Es súper rápido.
     */
    @Override
    public void iniciar(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("[Listener " + puerto + "] Hilo de escucha (mesero) iniciado.");

        while (ejecutando) {
            try {

                Socket socketCliente = serverSocket.accept();

                DataInputStream in = new DataInputStream(socketCliente.getInputStream());
                String msgRecibido = in.readUTF();

                colaDeEntrada.put(new PeticionCliente(socketCliente, msgRecibido));

            } catch (IOException e) {
                if (ejecutando) {
                    System.err.println("[Listener] Error al aceptar conexión: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * El hilo del "Cocinero". Trabaja en segundo plano. Toma peticiones de la
     * cola, las procesa y envía la respuesta.
     */
   private void procesarCola() {
        System.out.println("[Procesador de Cola] Hilo de procesamiento (cocinero) iniciado.");
        while (ejecutando) {
            PeticionCliente peticion = null;
            try {

                peticion = colaDeEntrada.take();
                System.out.println("[ServidorTCP] Procesando <- " + peticion.mensajeRecibido + " de **" + peticion.ipCliente + "**");

                this.procesador.procesar(peticion.ipCliente, peticion.mensajeRecibido);

            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("[ServidorTCP] Error al procesar petición: " + e.getMessage());
            } finally {
                if (peticion != null && peticion.socketCliente != null) {
                    try {
                        peticion.socketCliente.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

    /**
     * Detiene el servidor cerrando el socket de escucha y finalizando el hilo.
     *
     * @throws IOException
     */
    @Override
    public void detener() throws IOException {
        ejecutando = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
