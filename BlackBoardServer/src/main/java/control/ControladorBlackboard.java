package control;

import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iDirectorio;
import contratos.iPizarraJuego;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import observer.iObservador;
import pizarra.EstadoJuegoPizarra;

// ¡Implementa la nueva interfaz!
public class ControladorBlackboard implements iControladorBlackboard, iObservador {

    private final Map<String, iAgenteConocimiento> agentes;
    private final iDirectorio directorio; // ¡Ya no se inicializa aquí!

    // Modifica el constructor
    public ControladorBlackboard(List<iAgenteConocimiento> listaDeAgentes, iDirectorio directorio) {
        this.agentes = new HashMap<>();
        this.directorio = directorio; // ¡Recíbelo!

        for (iAgenteConocimiento agente : listaDeAgentes) {
            this.agentes.put(agente.getComandoQueManeja(), agente);
        }
    }

    @Override
    public void actualiza(iPizarraJuego pizarra, String evento) {
        switch (evento) {
            case "REGISTRAR":
                System.out.println("[Controlador] Pizarra notificó REGISTRO");
                // Aquí podrías iniciar la partida si ya están todos
                // if (pizarra.iniciarPartidaSiCorresponde()) {
                //     notificarObservadores(pizarra, "PARTIDA_INICIADA");
                // }
                break;

            case "MOVIMIENTO":
                // ¡La pizarra notificó un movimiento! Ahora hacemos el broadcast.

                // Obtenemos el ID del jugador que se movió
                String jugadorQueMovio = pizarra.getJugador();

                // Obtenemos el payload crudo que la pizarra acaba de procesar
                // Hacemos un "cast" para acceder al método específico de tu implementación
                String payloadCrudo = ((EstadoJuegoPizarra) pizarra).getUltimoPayloadMovimiento();

                // Armamos el mensaje de broadcast
                String mensajeBroadcast = "MOVIMIENTO_RECIBIDO:" + jugadorQueMovio + ":" + payloadCrudo;

                System.out.println("[Controlador] Reenviando broadcast: " + mensajeBroadcast);

                // Usamos el directorio para enviarlo a TODOS MENOS al que lo originó
                directorio.enviarATurnosInactivos(jugadorQueMovio, mensajeBroadcast);
                break;

            default:
                throw new AssertionError();
        }
    }
}
