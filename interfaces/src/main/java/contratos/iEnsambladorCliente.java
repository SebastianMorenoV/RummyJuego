/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos;

import java.beans.PropertyChangeListener;

/**
 *
 * @author benja
 */
public interface iEnsambladorCliente {
    
    iDespachador crearDespachador(String ipServidor, int puertoServidor);
    
    iListener crearListener(String miId, PropertyChangeListener oyente);
}
