/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

/**
 *
 * @author gael_
 */
public interface iControlSolicitarInicio {
    public void notificarEstoyListo();
    public void setControladorCUPrincipal(iControlCUPrincipal controladorCUPrincipal);
    
    public void mostrarVista();
    public void unirseASala();
}
