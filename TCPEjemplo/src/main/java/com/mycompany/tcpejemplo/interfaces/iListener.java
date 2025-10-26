
package com.mycompany.tcpejemplo.interfaces;


import java.io.IOException;

/**
 * Contrato para cualquier clase que pueda "Escuchar" en un puerto.
 * Es el contrato del "Mesero".
 * No le importa si es TCP, UDP, WebSockets, etc.
 */
public interface iListener {
    /**
     * Inicia el bucle de escucha en el puerto especificado.
     * Este método es bloqueante (debe correr en su propio hilo).
     * @param puerto El puerto en el que escuchar.
     * @throws IOException Si no se puede enlazar al puerto.
     */
    void iniciar(int puerto) throws IOException;

    /**
     * Detiene el listener.
     */
    void detener() throws IOException;
}