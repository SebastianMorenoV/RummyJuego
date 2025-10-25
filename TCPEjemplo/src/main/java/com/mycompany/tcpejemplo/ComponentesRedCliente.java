package com.mycompany.tcpejemplo;
public class ComponentesRedCliente {
    public final iDespachador despachador;
    public final iListener listener;

    public ComponentesRedCliente(iDespachador despachador, iListener listener) {
        this.despachador = despachador;
        this.listener = listener;
    }
}