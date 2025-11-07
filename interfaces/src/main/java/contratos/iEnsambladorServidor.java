package contratos;

// (Asegúrate de importar las otras interfaces)
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
     * @param despachador El despachador ya creado.
     * @param directorio El directorio ya creado.
     * @return Un iListener listo para iniciarse.
     */
    public iListener ensamblarRedServidor(
            iPizarraJuego pizarra
    );

}
