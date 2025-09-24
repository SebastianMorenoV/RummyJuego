/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import DTO.FichaJuegoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.Objetos.TableroUI;
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
    private ObjectOutputStream out;
    private TableroUI tableroUI;

    public ClienteRummy(IModelo modelo) {
        this.modelo = modelo;
    }

    public void conectar(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            escucharServidor();
            System.out.println("Conectado al servidor en " + ip + ":" + puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean estaConectado() {
        return socket != null && socket.isConnected();
    }

    // Enviar estado completo del juego (al terminar turno)
    public void enviarTurno(JuegoDTO juego) {
        try {
            Mensaje mensaje = new Mensaje(juego);
            out.writeObject(mensaje);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enviar movimiento en tiempo real
    public void enviarMovimiento(FichaJuegoDTO ficha, int x, int y) {
        try {
            Mensaje mensaje = new Mensaje(TipoMensaje.MOVER_FICHA, ficha, x, y);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(mensaje);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void escucharServidor() {
        new Thread(() -> {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    JuegoDTO juego = (JuegoDTO) in.readObject();
                    Mensaje mensaje = (Mensaje) in.readObject();
                    SwingUtilities.invokeLater(() -> {
                        procesarMensaje(mensaje);
                        modelo.actualizaDesdeRed(juego);
                        tableroUI.actualiza(juego);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void procesarMensaje(Mensaje mensaje) {
        if (mensaje.getTipo() == TipoMensaje.ESTADO_COMPLETO) {
            modelo.actualizaDesdeRed(mensaje.getJuego());
        } else if (mensaje.getTipo() == TipoMensaje.MOVER_FICHA) {
            modelo.moverFichaDesdeRed(
                    mensaje.getFicha(),
                    mensaje.getX(),
                    mensaje.getY()
            );
        }
    }
}
