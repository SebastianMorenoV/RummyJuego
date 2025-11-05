package utils;

import contratos.iDespachador;
import contratos.iListener;

/**
 * 
 * @author chris
 */
public class ComponentesRedCliente {

    public final iDespachador despachador;
    public final iListener listener;

    public ComponentesRedCliente(iDespachador despachador, iListener listener) {
        this.despachador = despachador;
        this.listener = listener;
    }
}
