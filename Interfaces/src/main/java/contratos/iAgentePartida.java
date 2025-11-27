/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos;

import java.util.List;
import java.util.Map;

/**
 * Contrato para la Fuente de Conocimiento (Agente Experto) responsable de la 
 * lógica de gestión de la partida, especialmente la creación del mazo, 
 * el reparto de fichas y el mantenimiento del estado serializado del mazo 
 * en el servidor.
 *
 * @author Sebastian Moreno
 */
public interface iAgentePartida {

    /**
     * Reparte las manos iniciales a un conjunto de jugadores.
     * Toma las fichas del mazo del juego y genera las cadenas serializadas 
     * correspondientes para cada jugador.
     *
     * @param jugadorIds La lista de identificadores únicos de los jugadores que recibirán una mano.
     * @return Un mapa donde la clave es el ID del jugador (String) y el valor 
     * es la mano serializada de fichas (String).
     */
    public Map<String, String> repartirManos(List<String> jugadorIds);

    /**
     * Almacena o actualiza la cadena serializada que representa el mazo restante 
     * del juego en la Pizarra.
     *
     * @param mazoSerializado La cadena de texto que contiene todas las fichas restantes del mazo.
     */
    public void setMazoSerializado(String mazoSerializado);

    /**
     * Obtiene la representación serializada actual del mazo restante.
     *
     * @return La cadena de texto que contiene las fichas del mazo.
     */
    public String getMazoSerializado();
}
