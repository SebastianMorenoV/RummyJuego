/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.itson.blackboardserver;

import com.mycompany.tcpejemplo.Ensamblador;
import com.mycompany.tcpejemplo.interfaces.iListener;
import java.io.IOException;

/**
 *
 * @author benja
 */
public class BlackBoardServer {

    public static void main(String[] args) {
        // 1. Configuración del Servidor
        final int PUERTO_DE_ESCUCHA = 5000;
        System.out.println("Iniciando Servidor de Rummy...");

        // 2. Ensamblaje de los componentes del servidor
        // (Necesitarás un método para esto en tu clase Ensamblador)
        iListener listenerServidor = Ensamblador.ensamblarServidor();

        // 3. Poner el servidor a escuchar
        // Esta llamada bloqueará el programa, manteniéndolo en un bucle infinito
        // para aceptar conexiones de jugadores.
        try {
            System.out.println("[Servidor Main] Escuchando en el puerto " + PUERTO_DE_ESCUCHA);
            listenerServidor.iniciar(PUERTO_DE_ESCUCHA);
        } catch (IOException e) {
            System.err.println("[Servidor Main] ERROR FATAL: No se pudo iniciar el servidor.");
            e.printStackTrace();
        }
    }
}
