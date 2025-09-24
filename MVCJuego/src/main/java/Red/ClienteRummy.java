/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import DTO.JuegoDTO;
import Modelo.IModelo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class ClienteRummy {

    private Socket socket;
    private IModelo modelo;

    public ClienteRummy(IModelo modelo) {
        this.modelo = modelo;
    }

    public void conectar(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            escucharServidor();
            System.out.println("Conectado al servidor en " + ip + ":" + puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean estaConectado() {
        return socket != null && socket.isConnected();
    }

    public void enviarTurno(JuegoDTO juego) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(juego);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void escucharServidor() {
        new Thread(() -> {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    JuegoDTO juego = (JuegoDTO) in.readObject();
                    SwingUtilities.invokeLater(() -> {
                        modelo.actualizaDesdeRed(juego);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
    }
}
