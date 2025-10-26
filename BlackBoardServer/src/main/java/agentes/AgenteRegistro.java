package agentes;

import contratos.iAgenteConocimiento;
import contratos.iPizarraJuego;
import contratos.iResultadoComando;
import resultados.ResultadoComando; 

/**
 * Agente Experto 🕵️ Sabe cómo manejar el comando "REGISTRAR". Depende solo de
 * interfaces.
 */
public class AgenteRegistro implements iAgenteConocimiento {

    private final iPizarraJuego pizarra;

    // Recibe la Pizarra (como interfaz) al ser construido
    public AgenteRegistro(iPizarraJuego pizarra) {
        this.pizarra = pizarra;
    }

    @Override
    public String getComandoQueManeja() {
        return "REGISTRAR";
    }

    @Override
    public iResultadoComando ejecutar(String idCliente, String payload) {
        // El 'payload' que recibe este agente es el que 
        // el ProcesadorServidor decidió pasarle (la mano serializada, o "").

        // 1. Ejecutar lógica de estado
        pizarra.registrarJugador(idCliente, payload);

        boolean juegoIniciado = pizarra.iniciarPartidaSiCorresponde();

        // 2. Crear el objeto de resultado
        ResultadoComando resultado = new ResultadoComando("REGISTRADO_OK");

        // 3. Añadir instrucciones si es necesario
        if (juegoIniciado) {
            // Si el juego inició, crea un mensaje para que 
            // el ProcesadorServidor lo envíe a todos.
            resultado.agregarBroadcast("PARTIDA_INICIADA");
        }

        return resultado; // Devuelve la interfaz
    }
}
