package FuentesConocimiento;

import Entidades.Ficha;
import Entidades.Tablero;
import DTO.FichaJuegoDTO;
import contratos.iAgentePartida;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pizarra.EstadoJuegoPizarra;

/**
 * Fuente de Conocimiento virtual experta en "Iniciar Partida". Este agente es
 * responsable de inicializar el estado del juego (crear mazo, repartir fichas)
 * cuando se recibe el comando de inicio de partida.
 *
 * @author benja
 */
public class AgenteIniciarPartida implements iAgentePartida {

    private Tablero tableroDeJuego;
    EstadoJuegoPizarra pizarra;

    /**
     * Al ser creado, este agente instancia el dominio del juego y crea el mazo
     * completo.
     *
     * @param pizarra
     */
    public AgenteIniciarPartida(EstadoJuegoPizarra pizarra) {
        this.pizarra = pizarra;
        this.tableroDeJuego = new Tablero();

    }

    /**
     * Reparte las manos para todos los jugadores IDs provistos.
     *
     * @param jugadorIds La lista de IDs de los jugadores en la partida.
     * @param numFichas
     * @param numComodines
     * @return Un mapa de [ID_Jugador -> ManoSerializada]
     */
    @Override
    public Map<String, String> repartirManos(List<String> jugadorIds, int numFichas, int numComodines) {

        this.tableroDeJuego.crearMazoCompleto(numComodines);
        Map<String, String> manosSerializadas = new HashMap<>();

        for (String id : jugadorIds) {
            List<Ficha> mano = new ArrayList<>();
            for (int i = 0; i < numFichas; i++) {
                Ficha ficha = this.tableroDeJuego.tomarFichaMazo();
                if (ficha != null) {
                    mano.add(ficha);
                }
                pizarra.setFichasJugador(id, numFichas); //MODIFICAR AQUI DEPENDIENDO DE LO QUE YA HAY EN BLACKBOARD
            }

            String manoPayload = mano.stream()
                    .map(this::convertirFichaADTO)
                    .map(FichaJuegoDTO::serializar)
                    .collect(Collectors.joining("|"));

            manosSerializadas.put(id, manoPayload);
            System.out.println("[AgenteIniciarPartida] Mano creada para " + id);
        }
        return manosSerializadas;
    }

    /**
     * Establece la representación serializada del mazo en la Pizarra
     * (Blackboard), permitiendo que otros agentes y procesos accedan al estado
     * del mazo restante.
     *
     * @param mazoSerializado Una cadena que representa el mazo restante (Fichas
     * separadas por "|").
     */
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
        return new FichaJuegoDTO(
                ficha.getId(),
                ficha.getNumero(),
                ficha.getColor(),
                ficha.isComodin(),
                0, 0
        );
    }
}
