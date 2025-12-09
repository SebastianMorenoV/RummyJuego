/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FuentesConocimiento;

import contratos.iAgenteRegistro;
import pizarra.EstadoJuegoPizarra;

/**
 *
 * @author benja
 */
public class AgenteRegistro implements iAgenteRegistro{
    private EstadoJuegoPizarra pizarra;

    public AgenteRegistro(EstadoJuegoPizarra pizarra) {
        this.pizarra = pizarra;
    }
    
    @Override
    public void registrarJugador(String id, String payload){
        pizarra.registrarJugador(id, payload);
    }
    
}
