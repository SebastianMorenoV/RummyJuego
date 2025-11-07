package contratos;

/**
 * Esta interfaz define un ensamblador que CONECTA la lógica de red a los
 * componentes principales del juego.
 *
 * @author benja
 */
public interface iEnsambladorServidor {

    /**
     * Conecta el Procesador y el Listener a los componentes del Blackboard.
     *
     * @param pizarra La instancia de la pizarra ya creada.
     * @return Un iListener listo para iniciarse.
     */
    public iListener ensamblarRedServidor(
            iPizarraJuego pizarra
    );

}
