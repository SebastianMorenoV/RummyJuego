/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Util;

import java.awt.Color;

/**
 *
 * @author chris
 */
public class SesionUsuario {

    private static String nickname;
    private static String idCliente;
    private static String avatar;

    // Colores de los 4 sets del usuario
    private static Color colorSet1;
    private static Color colorSet2;
    private static Color colorSet3;
    private static Color colorSet4;

    private static boolean estaRegistrado = false;

    private SesionUsuario() {
    }

    /**
     * Guarda la sesión con los 4 colores definidos por el usuario.
     */
    public static void guardarSesion(String nick, String imgAvatar,
            Color c1, Color c2, Color c3, Color c4) {
        nickname = nick;
        avatar = imgAvatar;

        colorSet1 = c1;
        colorSet2 = c2;
        colorSet3 = c3;
        colorSet4 = c4;

        estaRegistrado = true;

        System.out.println("[SesionUsuario] Sesión guardada: " + nickname);
    }

    public static String getNickname() {
        return nickname;
    }

    public static String getAvatar() {
        return avatar;
    }

    public static Color getColorSet1() {
        return colorSet1;
    }

    public static Color getColorSet2() {
        return colorSet2;
    }

    public static Color getColorSet3() {
        return colorSet3;
    }

    public static Color getColorSet4() {
        return colorSet4;
    }

    public static boolean isEstaRegistrado() {
        return estaRegistrado;
    }

    public static String getIdCliente() {
        return idCliente;
    }

    public static void setIdCliente(String idCliente) {
        SesionUsuario.idCliente = idCliente;
    }
    
    
}
