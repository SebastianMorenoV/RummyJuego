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
public interface IModeloSalaDeEspera {

    void votarParaIniciar();

    void setDespachador(iDespachador despachador);

    void setIpServidor(String ipServidor);

    void setPuertoServidor(int puertoServidor);

    void setIdCliente(String idCliente);

    void iniciarCU();

    void cerrarCU();

    void enviarVoto(boolean aceptado);
}
