/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import Modelo.Modelo;
import contratos.iNavegacion;

/**
 *
 * @author benja
 */
public class Control {

    Modelo modelo;
    iNavegacion navegacion;

    public Control(Modelo modelo, iNavegacion navegacion) {
        this.navegacion = navegacion;
        this.modelo = modelo;
    }

    public void iniciarCreacionPartida() {
        modelo.iniciarCreacionPartida();
    }
    public void SolicitarUnirseAPartida(){
        modelo.SolicitarUnirseApartida();
    }
    public void casoUsoConfigurarPartida() {
        if (this.navegacion != null) {
            this.navegacion.iniciarConfiguracionPartida();
        }
    }

}
