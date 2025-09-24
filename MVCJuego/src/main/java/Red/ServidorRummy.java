/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ServidorRummy {

    private int puerto;
    private List<ClienteHandler> clientes = new ArrayList<>();

    public ServidorRummy(int puerto) {
        this.puerto = puerto;
    }

    public void iniciar() {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(puerto)) {
                System.out.println("Servidor iniciado en puerto " + puerto);
                while (true) {
                    Socket clienteSocket = server.accept();
                    ClienteHandler handler = new ClienteHandler(clienteSocket);
                    clientes.add(handler);
                    handler.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class ClienteHandler extends Thread {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        public void enviarMensaje(Mensaje mensaje) {
            try {
                out.writeObject(mensaje);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    Mensaje mensaje = (Mensaje) in.readObject();

                    if (mensaje.getTipo() == TipoMensaje.MOVER_FICHA) {
                        // Reenviar el mensaje a todos los demás clientes
                        for (ClienteHandler ch : clientes) {
                            if (ch != this) {
                                try {
                                    ObjectOutputStream oos = new ObjectOutputStream(ch.socket.getOutputStream());
                                    oos.writeObject(mensaje);
                                    oos.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
