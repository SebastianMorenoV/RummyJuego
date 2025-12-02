/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;

import contratos.iDespachador;

/**
 *
 * @author benja
 */
public interface IModeloSala {
    public void setDespachador(iDespachador despachador);
    void enviarSolicitudInicio();
}
