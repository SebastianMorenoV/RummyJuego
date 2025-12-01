/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import modelo.Modelo;

/**
 *
 * @author benja
 */
public class Control {
    private Modelo modelo;

    public Control(Modelo modelo) {
        this.modelo = modelo;
    }
    
    public void notificarEstoyListo() {
        modelo.enviarSolicitudInicio();
    }
}
