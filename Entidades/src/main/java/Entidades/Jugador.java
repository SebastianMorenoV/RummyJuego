/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author moren
 */
public class Jugador {
    String nickname;
    String grupoColor;
    Mano manoJugador;
    
    public Jugador() {
    }

    public Jugador(String nickname, String grupoColor, Mano manoJugador) {
        this.nickname = nickname;
        this.grupoColor = grupoColor;
        this.manoJugador = manoJugador;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGrupoColor() {
        return grupoColor;
    }

    public Mano getManoJugador() {
        return manoJugador;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGrupoColor(String grupoColor) {
        this.grupoColor = grupoColor;
    }

    public void setManoJugador(Mano manoJugador) {
        this.manoJugador = manoJugador;
    }
    
    public boolean agregarFichaAJugador(Ficha ficha){
        return manoJugador.getFichasEnMano().add(ficha);
    }
    
    
}
