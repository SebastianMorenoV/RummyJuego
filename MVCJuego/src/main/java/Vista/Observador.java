/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Vista;

import DTO.ActualizacionDTO;
import Modelo.IModelo;

/**
 *
 *
 * @author moren
 * @author chris
 */
public interface Observador {

    public void actualiza(IModelo modelo, ActualizacionDTO dto);
}
