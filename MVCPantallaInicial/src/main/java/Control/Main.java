/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Control;

import Modelo.Modelo;
import Vista.VistaLobby;
import java.io.IOException;
import java.net.Socket;
import main.BlackBoardServer;

/**
 *
 * @author moren
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static final int PUERTO_SERVER = 5000;

    public static void main(String[] args) {

        Modelo modelo = new Modelo();
        Control control = new Control(modelo);
        VistaLobby vista = new VistaLobby(control);
        
        modelo.añadirObservador(vista);

        iniciarServidorSiNoExiste();

    }

    private static void iniciarServidorSiNoExiste() {
        if (estaCorriendoElServidor(PUERTO_SERVER)) {
            System.out.println(">> El servidor ya esta corriendo. No se iniciará una nueva instancia.");
        } else {
            System.out.println(">> Servidor no detectado. Iniciando BlackBoardServer...");
            new Thread(() -> {
                BlackBoardServer.main(new String[]{});
            }).start();
        }
    }

    private static boolean estaCorriendoElServidor(int puerto) {
        try (Socket s = new Socket("localhost", puerto)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
