/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.controladoresMVC;

import java.awt.Color;

/**
 *
 * @author chris
 */
public interface iControlRegistro {

    void intentarRegistrar(String nickname, String avatar, Color set1, Color set2, Color set3, Color set4);

    void setNavegacion(iControlCUPrincipal controlLobby);

    void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente);

    void iniciarRegistro();

    void entrarSalaEspera();
}
