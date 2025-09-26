package Vista;

/**
 * Clase enum para identificar tipo de jugada.
 *
 * @author Sebastian Moreno
 */
public enum TipoEvento {
    PUNTUACION_ACTUALIZADA,
    TURNO_CAMBIADO,
    FICHA_JUGADA,
    ACTUALIZAR_JUGADA,
    REPINTAR_MANO,
    INCIALIZAR_FICHAS,
    TOMO_FICHA,
    //ACTUALIZAR_TABLERO,
    // ACTUALIZAR_TABLERO, // Lo reemplazamos por los dos siguientes
    ACTUALIZAR_TABLERO_TEMPORAL, // Para el feedback mientras juegas
    JUGADA_VALIDA_FINALIZADA, // Para confirmar y guardar una jugada buena
    JUGADA_INVALIDA_REVERTIR
}
