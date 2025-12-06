/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista;

import modelo.IModeloSalaEspera;

/**
 *
 * @author benja
 */
public interface ObservadorSalaEspera {
    void actualiza(IModeloSalaEspera modelo, String evento, Object datos);
}
