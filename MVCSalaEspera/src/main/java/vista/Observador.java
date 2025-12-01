/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista;

import Dtos.ActualizacionDTO;
import modelo.IModelo;

/**
 *
 * @author benja
 */
public interface Observador {
    public void actualiza(IModelo modelo, ActualizacionDTO dto);
}
