/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import modelo.IModeloSalaDeEspera;
import contratos.iDespachador;
import contratos.controladoresMVC.iControlSalaEspera;
import tipoEventos.EventoSalaEspera;


/**
 *
 * @author benja
 */
public class ControlSalaDeEspera implements iControlSalaEspera {

    private IModeloSalaDeEspera modelo;

    /**
     * Constructor que recibe el modelo.
     *
     * @param modelo Instancia del modelo que maneja la lógica de negocio.
     */
    public ControlSalaDeEspera(IModeloSalaDeEspera modelo) {
        this.modelo = modelo;
    }

    /**
     * Método llamado por la vista cuando se presiona "Iniciar Partida". Delega
     * la acción al modelo para que envíe el voto al servidor.
     */
    public void enviarVoto() {
        if (modelo != null) {
            System.out.println("[ControlSalaDeEspera] Procesando clic en Iniciar Partida...");
            modelo.votarParaIniciar();
        } else {
            System.err.println("[ControlSalaDeEspera] Error: El modelo es nulo.");
        }
    }

    /**
     * Configura los datos de red en el modelo. Este método suele llamarse desde
     * el ensamblador o el controlador principal para inyectar las dependencias
     * necesarias.
     *
     * @param ipServidor IP del servidor Blackboard.
     * @param puertoServidor Puerto del servidor Blackboard.
     * @param idCliente ID único de este cliente.
     * @param despachador Componente encargado de enviar mensajes por la red.
     */
    public void setConfiguracionRed(String ipServidor, int puertoServidor, String idCliente, iDespachador despachador) {
        if (modelo != null) {
            modelo.setIpServidor(ipServidor);
            modelo.setPuertoServidor(puertoServidor);
            modelo.setIdCliente(idCliente);
            modelo.setDespachador(despachador);
        }
    }

    @Override
    public void cerrarCU() {
        if (modelo != null) {
            modelo.cerrarCU();
        }
    }

    public void iniciarCU() {
        if (modelo != null) {
            modelo.iniciarCU();
        }
    }

}
