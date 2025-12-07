/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Vista;

import Dtos.ActualizacionDTO;
import Modelo.IModelo;

/**
 *
 *
 * @author moren
 */
public interface ObservadorJuego {

    public void actualiza(IModelo modelo, ActualizacionDTO dto);
}
