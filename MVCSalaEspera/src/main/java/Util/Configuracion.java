/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

/**
 *
 * @author gael_
 */
public class Configuracion {
    private static final String ipServidor = "192.168.100.3";
    private static final int puerto = 5000;

    public Configuracion() {
    }

    /**
     * Obtiene la dirección IP estática del servidor central del juego.
     * * @return La IP del servidor como una cadena de texto (String).
     */
    public static String getIpServidor() {
        return ipServidor;
    }

    /**
     * Obtiene el número de puerto estático que usa el servidor para escuchar 
     * las conexiones de los clientes.
     * * @return El número de puerto como un entero (int).
     */
    public static int getPuerto() {
        return puerto;
    }
}
