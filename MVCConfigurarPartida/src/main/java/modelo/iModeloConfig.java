/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;

import contratos.iDespachador;
import vista.ObservadorConfig;

/**
 *
 * @author benja
 */
public interface iModeloConfig {
    public void setDespachador(iDespachador despachador);
    public void iniciarCU();
    public void añadirObservador(ObservadorConfig obs);
    public void configurarPartida(int comodines, int fichas);
}
