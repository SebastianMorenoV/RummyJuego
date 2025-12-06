/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

/**
 *
 * @author Sebastian Moreno
 */
public interface iControlConfig {

    public void configurarPartida(int comodines, int fichas);

    public void iniciarConfiguracion();

    public void setControladorCUPrincipal(iControlCUPrincipal controladorCUPrincipal);

    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente);

    public void cerrarCU();
}
