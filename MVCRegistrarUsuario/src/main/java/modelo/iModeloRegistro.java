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

    void setDespachador(iDespachador despachador);

    void registrarUsuario(String nickname, String avatar,
            int color1, int color2, int color3, int color4);

    void agregarObservador(ObservadorRegistro obs);

    void setIpServidor(String ipServidor);

    void setPuertoServidor(int puertoServidor);

    void setIpCliente(String ipCliente);

    public void iniciar();
}
