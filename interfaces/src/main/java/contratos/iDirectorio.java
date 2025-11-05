/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos;

/**
 *
 * @author Sebastian Moreno
 */
public interface iDirectorio {

    public void addJugador(String idJugador, String ip, int puerto);

    public void removeJugador(String idJugador);

    public void enviarATurnosInactivos(String jugadorQueEnvio, String mensaje);

    void enviarATodos(String mensaje);
}
