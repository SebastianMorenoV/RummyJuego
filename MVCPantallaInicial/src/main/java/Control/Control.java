/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import Modelo.Modelo;
//import controlador.Controlador;

/**
 *
 * @author benja
 */
public class Control {

    Modelo modelo;
    //Controlador control;

    public Control(Modelo modelo) {
        this.modelo = modelo;
    }
    
    public void iniciarCreacionPartida(){
        modelo.iniciarCreacionPartida();
    }

    public void casoUsoConfigurarPartida(){
        //control CU
       //control.iniciarCU();
        
    }
    
}
