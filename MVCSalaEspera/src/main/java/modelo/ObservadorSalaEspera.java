/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;


/**
 *
 * @author benja
 */
public interface ObservadorSalaEspera {
    
    // Método para que el Modelo notifique a la Vista.
    void actualiza(IModeloSalaEspera modelo, TipoEvento evt);
    
}
