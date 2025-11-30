/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import modelo.ModeloSalaEspera;

/**
 *
 * @author benja
 */
public class ControlSalaEspera {
    private ModeloSalaEspera modelo;

    public ControlSalaEspera(ModeloSalaEspera modelo) {
        this.modelo = modelo;
    }
    
    public void enviarVoto(boolean aceptado){
        modelo.enviarVoto(aceptado);
    }
}
