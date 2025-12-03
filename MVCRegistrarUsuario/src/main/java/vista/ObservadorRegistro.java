/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista;

import TipoEventos.EventoRegistro;

/**
 *
 * @author chris
 */
public interface ObservadorRegistro {

    void actualiza(EventoRegistro evento, String mensajeDetalle);
}
