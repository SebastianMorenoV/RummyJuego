package Vista;

/**
 *
 * Clase enum para identificar tipo de jugada del juego.
 * 
 * @author Sebastian Moreno
 */
public enum TipoEvento {
    CAMBIO_DE_TURNO,
    TURNO_CAMBIADO,
    REPINTAR_MANO,
    INCIALIZAR_FICHAS,
    TOMO_FICHA,
    ACTUALIZAR_TABLERO_TEMPORAL,
    JUGADA_VALIDA_FINALIZADA,
    JUGADA_INVALIDA_REVERTIR,
}
