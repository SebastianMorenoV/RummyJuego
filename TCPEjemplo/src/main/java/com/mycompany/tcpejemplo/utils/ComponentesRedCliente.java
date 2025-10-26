package com.mycompany.tcpejemplo.utils;

import com.mycompany.tcpejemplo.interfaces.iDespachador;
import com.mycompany.tcpejemplo.interfaces.iListener;

public class ComponentesRedCliente {
    public final iDespachador despachador;
    public final iListener listener;

    public ComponentesRedCliente(iDespachador despachador, iListener listener) {
        this.despachador = despachador;
        this.listener = listener;
    }
}