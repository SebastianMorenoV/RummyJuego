/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

import DTO.GrupoDTO;
import java.util.List;

/**
 *
 * @author moren
 *
 */
public interface iControlEjercerTurno {

    public void iniciarJuego();

    /**
     *
     * Le dice al Modelo que coloque la ficha (para la UI local). Serializa la
     * LISTA ENTERA de DTOs a un solo String. 3 Le dice al Ensamblador que envíe
     * ESE ÚNICO String.
     */
    void mockGanarPartida();

    public void colocarFicha(List<GrupoDTO> grupos);

    public void regresarFichaAMano(int idFicha);

    public void pasarTurno();

    public void terminarTurno();

    public void abrirCU();

    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente);

    public void cerrarCUAnteriores();

    public void setControlConfiguracion(iControlConfig controlConfiguracion);

    public void setControlPantallaPrincipal(iControlCUPrincipal controlPantallaPrincipal);

    void setControlSalaEspera(iControlSalaEspera controlSalaEspera);

}
