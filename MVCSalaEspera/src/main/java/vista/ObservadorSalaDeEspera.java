/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista;

import modelo.IModeloSalaDeEspera;
import tipoEventos.EventoSalaEspera;

/**
 *
 * @author benja
 */
public interface ObservadorSalaDeEspera {

    void actualiza(IModeloSalaDeEspera modelo, EventoSalaEspera evento, Object datos);
}
