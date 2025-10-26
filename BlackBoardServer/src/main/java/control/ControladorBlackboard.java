package control;


import contratos.iAgenteConocimiento;
import contratos.iControladorBlackboard;
import contratos.iResultadoComando;
import resultados.ResultadoComando; // Solo la usa internamente
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ¡Implementa la nueva interfaz!
public class ControladorBlackboard implements iControladorBlackboard {
    
    private final Map<String, iAgenteConocimiento> agentes;

    public ControladorBlackboard(List<iAgenteConocimiento> listaDeAgentes) {
        this.agentes = new HashMap<>();
        for (iAgenteConocimiento agente : listaDeAgentes) {
            this.agentes.put(agente.getComandoQueManeja(), agente);
        }
    }
    
    @Override
    public iResultadoComando procesarComando(String idCliente, String comando, String payload) {
        
        iAgenteConocimiento agente = agentes.get(comando);
        
        if (agente != null) {
            return agente.ejecutar(idCliente, payload);
        }
        
        // Si no hay agente, devuelve un resultado de error genérico
        return new ResultadoComando("ERROR: Comando desconocido");
    }
}