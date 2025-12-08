/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

import DTO.GrupoDTO;
import java.util.List;

/**
 *
 * @author chris
 */
public interface iControlEjercerTurno {

    public void iniciarJuego();

    public void colocarFicha(List<GrupoDTO> grupos);

    public void regresarFichaAMano(int idFicha);

    public void pasarTurno();

    public void terminarTurno();

    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente);

    public void setControlPantallaPrincipal(iControlCUPrincipal ccup);

    public void abrirCU();

    public void cerrarCUAnteriores();
    
    void setControlSalaEspera(iControlSalaEspera controlSalaEspera);

}
