/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Red;

import java.io.Serializable;
/**
 *
 * @author Admin
 */
public class Mensaje implements Serializable {

    private TipoMensaje tipo;
    private Object contenido;

    public Mensaje(TipoMensaje tipo, Object contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public Object getContenido() {
        return contenido;
    }
}
