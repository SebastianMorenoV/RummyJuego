package agentes;


import contratos.iAgenteConocimiento;
import contratos.iPizarraJuego;
import contratos.iResultadoComando;
import resultados.ResultadoComando; // La usa para crear el objeto

/**
 * Agente Experto 🕵️
 * Sabe cómo manejar el comando "MOVER".
 * Valida el turno y actualiza el estado del juego.
 */
public class AgenteMovimiento implements iAgenteConocimiento {
    
    private final iPizarraJuego pizarra;

    public AgenteMovimiento(iPizarraJuego pizarra) {
        this.pizarra = pizarra;
    }
    
    @Override
    public String getComandoQueManeja() {
        return "MOVER";
    }

    @Override
    public iResultadoComando ejecutar(String idCliente, String payload) {
        
        // 1. Validar regla de negocio (leer de la pizarra)
        if (!pizarra.esTurnoDe(idCliente)) {
            // Si no es su turno, devuelve un resultado de error.
            return new ResultadoComando("ERROR: No es tu turno");
        }
        
        // 2. Actualizar estado (escribir en la pizarra)
        // Asumimos que el "payload" del movimiento es la nueva mano serializada
        pizarra.actualizarMano(idCliente, payload); 
        
        // 3. Avanzar estado del juego (escribir en la pizarra)
        pizarra.avanzarTurno();
        
        // 4. Preparar resultado para el remitente
        ResultadoComando resultado = new ResultadoComando("MOVIMIENTO_RECIBIDO_OK");
        
        // 5. Preparar instrucción de broadcast para los demás
        String msgUpdate = "MOVIMIENTO_RECIBIDO:" + idCliente + ":" + payload;
        resultado.agregarBroadcast(msgUpdate);
        
        return resultado; // Devuelve la interfaz
    }
}