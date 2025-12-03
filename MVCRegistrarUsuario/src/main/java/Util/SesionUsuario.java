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
    private static String avatar;
    private static Color color;
    private static boolean estaRegistrado = false;

    private SesionUsuario() {
    }

    public static void guardarSesion(String nick, String imgAvatar, Color colorUsuario) {
        nickname = nick;
        avatar = imgAvatar;
        color = colorUsuario;
        estaRegistrado = true;
        System.out.println("[SesionUsuario] Datos guardados: " + nickname);
    }

    public static String getNickname() {
        return nickname;
    }

    public static String getAvatar() {
        return avatar;
    }

    public static Color getColor() {
        return color;
    }

    public static boolean isEstaRegistrado() {
        return estaRegistrado;
    }
}
