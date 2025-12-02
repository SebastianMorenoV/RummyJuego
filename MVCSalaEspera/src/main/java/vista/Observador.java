/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista;

import Dtos.ActualizacionSalaDTO;
import modelo.IModeloSala;

/**
 *
 * @author benja
 */
public interface Observador {
    public void actualiza(IModeloSala modelo, ActualizacionSalaDTO dto);
}
