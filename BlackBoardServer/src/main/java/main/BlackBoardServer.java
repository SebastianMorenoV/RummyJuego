/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package main;

import contratos.iListener;
import ensamblador.EnsambladorServidor;
import java.io.IOException;

/**
 *
 * @author benja
 */
public class BlackBoardServer {

    public static void main(String[] args) {
        final int PUERTO_DE_ESCUCHA = 5000;
        System.out.println("Iniciando Servidor de Rummy...");

        // 2. Llama al EnsambladorServidor correcto
        iListener listenerServidor = EnsambladorServidor.ensamblarServidor();

        // 3. Poner el servidor a escuchar
        try {
            System.out.println("[Servidor Main] Escuchando en el puerto " + PUERTO_DE_ESCUCHA);
            listenerServidor.iniciar(PUERTO_DE_ESCUCHA);
        } catch (IOException e) {
            System.err.println("[Servidor Main] ERROR FATAL: No se pudo iniciar el servidor.");
            e.printStackTrace();
        }
    }
}
