/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import Red.ServidorRummy;

/**
 *
 * @author Admin
 */
public class MainServidor {

    public static void main(String[] args) {
        ServidorRummy servidor = new ServidorRummy(5000);
        servidor.iniciar(); // solo inicia el servidor
    }
}
