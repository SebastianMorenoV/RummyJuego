/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;

import contratos.iDespachador;
import vista.ObservadorRegistro;

/**
 *
 * @author chris
 */
public interface iModeloRegistro {

    public void setIpServidor(String ipServidor);

    public void setPuertoServidor(int puertoServidor);

    public void setIpCliente(String ipCliente);
    
    public void setIdCliente(String idCliente);
    
    void setDespachador(iDespachador despachador);
    
    void registrarUsuario(String nickname, int idAvatar, int[] colores);

    void agregarObservador(ObservadorRegistro obs);

    public void iniciar();
}
