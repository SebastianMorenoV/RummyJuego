/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.ModeloConfig;
import modelo.iModeloConfig;

/**
 *
 * @author moren
 */
public class Controlador {

    iModeloConfig modelo;

    public Controlador(iModeloConfig modelo) {
        this.modelo = modelo;
    }

    public void iniciarCU() {

    }
    
    public void configurarPartida(int comodines,int fichas){
        System.out.println("Aqui le hablaremos al modelo y despues al blackboard.");
    }

}
