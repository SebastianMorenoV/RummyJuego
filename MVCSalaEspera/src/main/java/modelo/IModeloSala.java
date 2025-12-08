/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;

import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import java.util.List;
import java.util.Map;
import vista.ObservadorSala;
import vista.TipoEventoSala;

/**
 *
 * @author benja
 */
public interface IModeloSala {
    public void setDespachador(iDespachador despachador);
    void enviarSolicitudInicio();
    
    void iniciarConexionRed(); 

    String getMiId(); 

    void setEnsambladorCliente(iEnsambladorCliente ensambladorCliente);

    void setMiPuertoDeEscucha(int miPuertoDeEscucha);

    void setMiId(String miId);

}
