package contratos;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


import contratos.iPizarraJuego;

/**
 *
 * @author Sebastian Moreno
 */
public interface iObservador {
    void actualiza(iPizarraJuego pizarra, String evento);
}
