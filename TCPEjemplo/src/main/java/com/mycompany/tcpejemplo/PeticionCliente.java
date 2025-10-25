package com.mycompany.tcpejemplo;
// Archivo: PeticionCliente.java


import java.net.Socket;

// Contenedor para pasar la petición del "mesero" al "cocinero".
public class PeticionCliente {
    public final Socket socketCliente;
    public final String mensajeRecibido;
    public final String ipCliente;

    public PeticionCliente(Socket socketCliente, String mensajeRecibido) {
        this.socketCliente = socketCliente;
        this.mensajeRecibido = mensajeRecibido;
        this.ipCliente = socketCliente.getInetAddress().getHostAddress();
    }
}