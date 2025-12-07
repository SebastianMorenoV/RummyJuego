/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modelo;

import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

/**
 * Contrato del modelo de Sala de Espera. Define las operaciones que el
 * Controlador y el Ensamblador pueden realizar, y extiende
 * PropertyChangeListener para recibir eventos de red.
 * 
 * @author luciano
 */
public interface IModeloSalaEspera extends PropertyChangeListener {

    void iniciarCU(); 

    void simularEntradaDeJugadores(); 

    void jugadorPulsaListo(); 

    boolean isPartidaLista(); 

    void iniciarConexionRed(); 

    void notificarObservadores(TipoEvento evt);

    void agregarObservador(ObservadorSalaEspera obs); 

    Map<String, Boolean> getJugadoresListos(); 

    List<String> getIdsJugadoresEnSala(); 

    String getMiId(); 

    void setEnsambladorCliente(iEnsambladorCliente ensambladorCliente);

    void setMiPuertoDeEscucha(int miPuertoDeEscucha);

    void setDespachador(iDespachador despachador);

    void setMiId(String miId);
    
    public void cerrarCU();
}
