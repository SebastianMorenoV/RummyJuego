/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import DTO.JuegoDTO;
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
    private JuegoDTO estadoGlobal;

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

        public void enviarJuego(JuegoDTO juego) {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(juego);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    JuegoDTO juego = (JuegoDTO) in.readObject();
                    estadoGlobal = juego;
                    for (ClienteHandler ch : clientes) {
                        if (ch != this) {
                            ch.enviarJuego(juego);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
