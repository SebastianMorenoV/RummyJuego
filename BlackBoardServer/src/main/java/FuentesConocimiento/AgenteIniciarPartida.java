package FuentesConocimiento;

import Entidades.Ficha;
import Entidades.Tablero;
import DTO.FichaJuegoDTO; // Usamos DTOs para serializar
import contratos.iAgentePartida;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pizarra.EstadoJuegoPizarra;

/**
 * Fuente de Conocimiento virtual experta en "Iniciar Partida". Es el ÚNICO
 * componente en el servidor que conoce las Entidades.
 */
public class AgenteIniciarPartida implements iAgentePartida{
    
    private Tablero tableroDeJuego;
    EstadoJuegoPizarra pizarra;

    /**
     * Al ser creado, este agente instancia el dominio del juego y crea el mazo
     * completo.
     */
    public AgenteIniciarPartida(EstadoJuegoPizarra pizarra) {
        this.pizarra = pizarra;
        this.tableroDeJuego = new Tablero();
        this.tableroDeJuego.crearMazoCompleto();
    }

    /**
     * Reparte las manos para todos los jugadores IDs provistos.
     *
     * @param jugadorIds La lista de IDs de los jugadores en la partida.
     * @return Un mapa de [ID_Jugador -> ManoSerializada]
     */
    @Override
    public Map<String, String> repartirManos(List<String> jugadorIds) {
        Map<String, String> manosSerializadas = new HashMap<>();
        
        for (String id : jugadorIds) {
            // 1. Usa la lógica de Entidades para repartir 14 fichas
            List<Ficha> mano = new ArrayList<>();
            for (int i = 0; i < 14; i++) {
                Ficha ficha = this.tableroDeJuego.tomarFichaMazo(); //
                if (ficha != null) {
                    mano.add(ficha);
                }
            }

            // 2. Serializa la mano a un String (Fichas separadas por "|")
            String manoPayload = mano.stream()
                    .map(this::convertirFichaADTO) // Convierte Entidad a DTO
                    .map(FichaJuegoDTO::serializar) // Convierte DTO a String
                    .collect(Collectors.joining("|"));
            
            manosSerializadas.put(id, manoPayload);
            System.out.println("[AgenteIniciarPartida] Mano creada para " + id);
        }
        return manosSerializadas;
    }

    @Override
    public void setMazoSerializado(String mazoSerializado) {
        this.pizarra.setMazoSerializado(mazoSerializado);
    }

    /**
     * Obtiene el mazo restante, también serializado.
     *
     * @return Un string que representa el mazo (Fichas separadas por "|").
     */
    @Override
    public String getMazoSerializado() {
        return this.tableroDeJuego.getMazo().stream()
                .map(this::convertirFichaADTO)
                .map(FichaJuegoDTO::serializar)
                .collect(Collectors.joining("|"));
    }

    /**
     * Método de ayuda para convertir de Entidad a DTO. La Pizarra no sabe qué
     * es una "Ficha", pero el cliente sí sabe qué es una "FichaJuegoDTO".
     */
    private FichaJuegoDTO convertirFichaADTO(Ficha ficha) {
        // Asigna 0,0 a fila/columna, ya que no están en el tablero
        return new FichaJuegoDTO(
                ficha.getId(),
                ficha.getNumero(),
                ficha.getColor(),
                ficha.isComodin(),
                0, 0
        );
    }
}
