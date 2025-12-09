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
    
    void solicitarRegistro();
    
    void setControladorRegistro(iControlRegistro controlRegistro);

    public void casoUsoConfigurarPartida();

    public void pantallaInicial();

    public void setControladorConfig(iControlConfig controladorConfig);

    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente);

    public void setControladorEjercerTurno(iControlEjercerTurno control);

    public void ejercerTurno();
    
    public void cerrarCU();
    
    public void iniciarCU();

    public void setControlSalaEspera(iControlSalaEspera controlSalaEspera);
    
    void entrarSalaEspera();
    
    void procesarNavegacionRegistrarJugador ();

}
