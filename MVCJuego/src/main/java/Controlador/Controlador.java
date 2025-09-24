package Controlador;

import DTO.FichaJuegoDTO;
import DTO.JuegoDTO;
import Modelo.Modelo;
import Red.ClienteRummy;
import Red.Mensaje;
import Red.TipoMensaje;

/**
 *
 * @author moren
 */
public class Controlador {

    Modelo modelo;
    private ClienteRummy cliente;

    public Controlador(Modelo modelo) {
        this.modelo = modelo;
    }

    public void setCliente(ClienteRummy cliente) {
        this.cliente = cliente;
    }

    public void iniciarJuego() {
        modelo.iniciarJuego();
        modelo.iniciarTurno();
    }

    public void fichaSoltada(FichaJuegoDTO ficha, int x, int y) {
        modelo.colocarFicha(ficha, x, y);
    }

    public void pasarTurno() {
        modelo.tomarFichaMazo();
    }

    public boolean terminarTurno() {
        boolean exito = modelo.terminarTurno(); // ahora guardamos el boolean

        if (cliente != null && cliente.estaConectado()) {
            JuegoDTO estado = modelo.getTablero();
            cliente.enviarTurno(estado);
        }

        return exito; // retornamos el resultado
    }
}
