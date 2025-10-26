package com.mycompany.tcpejemplo.utils;

import contratos.iDespachador;
import contratos.iListener;

public class ComponentesRedCliente {

    public final iDespachador despachador;
    public final iListener listener;

    public ComponentesRedCliente(iDespachador despachador, iListener listener) {
        this.despachador = despachador;
        this.listener = listener;
    }
}
