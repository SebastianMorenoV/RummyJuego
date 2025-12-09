/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

import contratos.iDespachador;

/**
 *
 * @author chris
 */
public interface iControlSalaEspera {
    void cerrarCU();
    void setConfiguracionRed(String ipServidor, int puertoServidor, String idCliente, iDespachador despachador);
    void iniciarCU();
}
