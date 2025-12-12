package Controlador;

import DTO.GrupoDTO;
import Modelo.Modelo;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlConfig;
import contratos.controladoresMVC.iControlEjercerTurno;
import contratos.controladoresMVC.iControlEjercerTurno;
import contratos.controladoresMVC.iControlSalaEspera;

import java.util.List;

/**
 * Controlador del sistema encargado de gestionar la comunicación entre la
 * interfaz de usuario (vista/cliente) y la lógica de negocio representada por
 * el Modelo.
 *
 * @author moren
 */
public class Controlador implements iControlEjercerTurno {

    Modelo modelo;

    iControlConfig controlConfiguracion;
    iControlCUPrincipal controlPantallaPrincipal;
    iControlSalaEspera controlSalaDeEspera;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }

    @Override
    public void solicitarTerminarPartida() {
        modelo.solicitarTerminarPartida();
    }

    @Override
    public void salirAlLobby() {

        if (controlPantallaPrincipal != null) {
            System.out.println("[Controlador] Regresando al Lobby Principal...");
            controlPantallaPrincipal.pantallaInicial();
        } else {
            System.err.println("[Controlador] Error: No hay referencia a PantallaPrincipal");
        }
    }

    @Override
    public void enviarVotoTerminar(boolean aceptaTerminar) {
        modelo.enviarVotoTerminar(aceptaTerminar);
    }

    /**
     * Metodo que le habla al modelo para iniciar el juego.
     */
    @Override
    public void iniciarJuego() {
        modelo.iniciarJuego();
    }

    /**
     * Le dice al Modelo que coloque la ficha (para la UI local). Serializa la
     * LISTA ENTERA de DTOs a un solo String. 3 Le dice al Ensamblador que envíe
     * ESE ÚNICO String.
     */
    @Override
    public void colocarFicha(List<GrupoDTO> grupos) {
        modelo.colocarFicha(grupos);
    }

    /**
     * Metodo que le habla al modelo para regresar una ficha desde el tablero a
     * la mano.
     *
     * @param idFicha id de la ficha a regresar.
     */
    @Override
    public void regresarFichaAMano(int idFicha) {
        modelo.regresarFichaAMano(idFicha);
    }

    public void mockGanarPartida() {
        modelo.mockGanarPartida();
    }

    /**
     * Metodo que le habla al modelo para pasar el Turno.
     */
    @Override
    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    /**
     * Metodo que le habla al modelo para terminar el turno de un jugador.
     */
    @Override
    public void terminarTurno() {
        modelo.terminarTurno();
    }

    @Override
    public void abrirCU() {
        modelo.abrirCU();
    }

    @Override
    public void setControlSalaEspera(iControlSalaEspera ControlSalaEspera) {
        this.controlSalaDeEspera = ControlSalaEspera;
    }

    @Override
    public void enviarMensajeChat(String mensaje) {
        modelo.enviarMensajeChat(mensaje);
    }

    @Override
    public String getNombreJugadorLocal() {
        return modelo.getMiNickname(); // Asumiendo que el modelo tiene esto
    }

    @Override
    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente, int puertoCliente) {
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setIpCliente(ipCliente);
        modelo.setPuertoCliente(puertoCliente);
    }

    @Override
    public void cerrarCUAnteriores() {
        controlConfiguracion.cerrarCU();
        controlPantallaPrincipal.cerrarCU();
    }

    @Override
    public void setControlConfiguracion(iControlConfig controlConfiguracion) {
        this.controlConfiguracion = controlConfiguracion;
    }

    @Override
    public void setControlPantallaPrincipal(iControlCUPrincipal controlPantallaPrincipal) {
        this.controlPantallaPrincipal = controlPantallaPrincipal;
    }

}
