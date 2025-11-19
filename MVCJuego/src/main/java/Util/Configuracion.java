/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

/**
 *
 * @author Sebastian Moreno
 */
public class Configuracion {

    private static final String ipServidor = "192.168.100.3";
    private static final int puerto = 5000;

    public Configuracion() {
    }

    public static String getIpServidor() {
        return ipServidor;
    }

    public static int getPuerto() {
        return puerto;
    }
    
}
