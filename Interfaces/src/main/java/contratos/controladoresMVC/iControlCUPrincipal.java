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

    public void iniciarCreacionPartida();

    public void SolicitarUnirseAPartida();

    public void casoUsoConfigurarPartida();

    public void pantallaInicial();

    public void setControladorConfig(iControlConfig controladorConfig);
    
    public void setControladorSalaEspera(iControlSolicitarInicio controladorSala);
}
