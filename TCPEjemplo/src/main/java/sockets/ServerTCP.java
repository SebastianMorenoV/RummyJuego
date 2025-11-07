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

        // Inicia el hilo "cocinero" que procesará la cola
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

                // 1. Acepta la conexión del cliente
                Socket socketCliente = serverSocket.accept();

                // 2. Lee el mensaje (la orden)
                DataInputStream in = new DataInputStream(socketCliente.getInputStream());
                String msgRecibido = in.readUTF();

                // 3. Pone la orden en la cola para que el "cocinero" la tome
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

                // 1. Espera a que haya una orden en la cola.
                peticion = colaDeEntrada.take();
                System.out.println("[Cocinero] Procesando <- " + peticion.mensajeRecibido + " de **" + peticion.ipCliente + "**");

                // 2. Procesa la orden usando la lógica del servidor. Esta es la parte "lenta".
                String respuesta = this.procesador.procesar(peticion.ipCliente, peticion.mensajeRecibido);

                // 3. Envía la respuesta al cliente.
                DataOutputStream out = new DataOutputStream(peticion.socketCliente.getOutputStream());
                out.writeUTF(respuesta);
                System.out.println("[Cocinero] Enviado a **" + peticion.ipCliente + "** -> " + respuesta);

            } catch (InterruptedException e) {
                ejecutando = false;
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("[Cocinero] Error al procesar petición: " + e.getMessage());
            } finally {
                // 4. Cierra la conexión con el cliente. Es muy importante.
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
