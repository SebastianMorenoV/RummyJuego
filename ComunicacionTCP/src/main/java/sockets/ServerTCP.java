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
 * Servidor TCP basado en cola (patrón Productor–Consumidor). 
 * Este diseño evita bloqueos en la recepción de conexiones y permite procesar 
 * las peticiones de los clientes de forma asíncrona y concurrente.
 *
 * @author chris
 */
public class ServerTCP implements iListener {

    private final iProcesador procesador;
    private final BlockingQueue<PeticionCliente> colaDeEntrada;
    private volatile boolean ejecutando = true;
    private ServerSocket serverSocket;

    /**
     * Constructor. Inicializa el servidor y comienza el hilo dedicado al 
     * procesamiento de la cola de mensajes.
     *
     * @param procesador La instancia de la lógica de procesamiento (Servidor o Cliente) 
     * a la que se delegarán los mensajes recibidos.
     */
    public ServerTCP(iProcesador procesador) {
        if (procesador == null) {
            throw new IllegalArgumentException("El procesador no puede ser nulo");
        }
        this.procesador = procesador;
        this.colaDeEntrada = new LinkedBlockingQueue<>();

        new Thread(this::procesarCola).start();
    }

    /**
     * Inicia la operación de escucha del servidor en el puerto especificado.
     * Este método se ejecuta en el hilo productor (el hilo principal de escucha).
     * Su función es aceptar conexiones entrantes, leer el mensaje y encolarlo 
     * para su procesamiento.
     *
     * @param puerto El puerto de red en el que el servidor escuchará las conexiones entrantes.
     * @throws IOException Si ocurre un error al abrir el ServerSocket o aceptar conexiones.
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
     * Ejecuta la lógica del hilo consumidor. 
     * Este hilo se mantiene bloqueado (`take()`) esperando peticiones en la cola. 
     * Una vez disponible, extrae el mensaje y llama al procesador de lógica delegado.
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
