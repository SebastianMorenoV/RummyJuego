/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

/**
 *
 * @author Sebastian Moreno
 */
public interface iControlCUPrincipal {

    public void SolicitarUnirseAPartida();
    
    public void setControladorSalaEspera(iControlSalaEspera controlSalaEspera);
    
    public void procesarNavegacionSalaEspera();
}
