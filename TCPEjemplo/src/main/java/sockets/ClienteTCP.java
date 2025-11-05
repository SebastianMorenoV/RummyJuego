package sockets;

import contratos.iDespachador;
import java.io.*;
import java.net.Socket;

/**
 * Implementación del TRANSPORTE DE ENVÍO (El "Repartidor"). Sabe cómo
 * conectarse, enviar un mensaje y recibir una respuesta.
 *
 * @author Sebastian Moreno
 */
public class ClienteTCP implements iDespachador {

    private String hostFijo;
    private int puertoFijo;

    // Constructor para el CLIENTE (destino fijo)
    public ClienteTCP(String host, int puerto) {
        this.hostFijo = host;
        this.puertoFijo = puerto;
    }

    // Constructor para el SERVIDOR (sin destino fijo)
    public ClienteTCP() {
    }

    // MÉTODO CLIENTE
    @Override
    public void enviar(String mensaje) throws IOException {
        if (this.hostFijo == null) {
            throw new IllegalStateException("Este despachador no tiene un destino fijo. Use enviar(host, puerto, mensaje).");
        }
        // Llama al otro método usando los datos guardados.
        this.enviar(this.hostFijo, this.puertoFijo, mensaje);
    }

    // MÉTODO SERVIDOR
    @Override
    public void enviar(String host, int puerto, String mensaje) throws IOException {
        System.out.println("[Despachador] Conectando a " + host + ":" + puerto + "...");

        // Usa los parámetros 'host' y 'puerto', no las variables de la clase.
        try (Socket socket = new Socket(host, puerto); DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            out.writeUTF(mensaje);
            System.out.println("[Despachador] Enviado -> " + mensaje);
        }
    }
}
