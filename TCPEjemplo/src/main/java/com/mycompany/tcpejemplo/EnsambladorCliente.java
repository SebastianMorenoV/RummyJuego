package com.mycompany.tcpejemplo;

import utils.ComponentesRedCliente;
import procesadores.ProcesadorCliente;
import sockets.ClienteTCP;
import sockets.ServerTCP;
import contratos.iDespachador;
import contratos.iListener;

// Importa el listener genérico de Java
import java.beans.PropertyChangeListener;

/**
 * (ACTUALIZADO) El "Puente" entre componentes. Conecta la Lógica (Procesador)
 * con el Transporte (Listener/Despachador). Es el ÚNICO que conecta el Modelo
 * (Oyente) con el ProcesadorCliente (Emisor).
 */
/**
 * (REFACTORIZADO) Clase de utilidad tipo "Fábrica" que construye y conecta los
 * componentes de red para el cliente o el servidor. No guarda ningún estado; su
 * único trabajo es ensamblar.
 */
public final class EnsambladorCliente {

    /**
     * Constructor privado para evitar que esta clase de utilidad sea
     * instanciada.
     */
    private EnsambladorCliente() {
    }

    /**
     * Ensambla todos los componentes de red necesarios para una instancia de
     * CLIENTE.
     *
     * @param miId El ID de este cliente.
     * @param ipServidor La IP del servidor al que se conectará el despachador.
     * @param puertoServidor El puerto del servidor.
     * @param oyente El Modelo del juego que escuchará los eventos de la red.
     * @return Un objeto ComponentesRedCliente con el despachador y el listener
     * listos.
     */
    public static ComponentesRedCliente ensamblarCliente(String miId, String ipServidor, int puertoServidor, PropertyChangeListener oyente) {
        System.out.println("[Ensamblador] Ensamblando componentes para CLIENTE (" + miId + ")...");

        // 1. Crear el "Repartidor" (Despachador) y configurarlo con el destino.
        //    (Asumiendo que ClienteTCP fue modificado para recibir esto en su constructor)
        iDespachador despachador = new ClienteTCP(ipServidor, puertoServidor);

        // 2. Crear la "Lógica de Cliente" que procesa mensajes entrantes.
        ProcesadorCliente logicaCliente = new ProcesadorCliente(miId);

        // 3. Conectar el Modelo (oyente) para que reciba eventos de la red.
        System.out.println("[Ensamblador] Conectando Oyente (" + oyente.getClass().getSimpleName() + ") -> ProcesadorCliente");
        logicaCliente.addPropertyChangeListener(oyente);

        // 4. Crear el "Mesero" (Listener) e inyectarle la lógica ya conectada.
        iListener listener = new ServerTCP(logicaCliente);

        System.out.println("[Ensamblador] Ensamblaje de CLIENTE finalizado.");

        // 5. Devolver el paquete con los componentes listos para usar.
        return new ComponentesRedCliente(despachador, listener);
    }

}
