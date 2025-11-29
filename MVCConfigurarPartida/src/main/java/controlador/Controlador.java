/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Modelo;

/**
 *
 * @author moren
 */
public class Controlador {

    Modelo modelo;
    public Controlador(Modelo modelo) {
        this.modelo = new Modelo();
    }
    
   public void iniciarCU(){
       modelo.iniciarCU();
   } 
   
}
