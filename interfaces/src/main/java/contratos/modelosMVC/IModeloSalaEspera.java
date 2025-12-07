/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package contratos.modelosMVC;

import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.vistasMVC.ObservadorSalaEspera;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

/**
 * Contrato del modelo de Sala de Espera. Define las operaciones que el
 * Controlador y el Ensamblador pueden realizar, y extiende
 * PropertyChangeListener para recibir eventos de red.
 */
public interface IModeloSalaEspera extends PropertyChangeListener {

    // --- MÉTODOS DE LÓGICA Y ESTADO ---
    /**
     * Inicia el Caso de Uso (CU) localmente.
     */
    void iniciarCU(); // Llamado por ControlSalaEspera

    /**
     * Simula la entrada de jugadores (o se usa para inicialización real).
     */
    void simularEntradaDeJugadores(); // Llamado por ControlSalaEspera

    /**
     * Lógica de la acción "Pulsar Listo".
     */
    void jugadorPulsaListo(); // Llamado por ControlSalaEspera

    /**
     * Verifica si la condición de inicio (min. jugadores y todos listos) se
     * cumple.
     */
    boolean isPartidaLista(); // Llamado por VistaSalaEspera

    // --- MÉTODOS DE RED Y CONEXIÓN (Llamados por Ensambladores) ---
    /**
     * Inicia el hilo de escucha y se registra en el servidor.
     */
    void iniciarConexionRed(); // Llamado por EnsambladoresMVC

    // --- MÉTODOS OBSERVABLE (PATRÓN OBSERVER) ---
    /**
     * Notifica a las vistas de un cambio de estado en la sala.
     */
    void notificarObservadores(); // Usado internamente

    /**
     * Registra una vista (Observador) para recibir notificaciones.
     */
    void agregarObservador(ObservadorSalaEspera obs); // Llamado por EnsambladoresMVC

    // --- GETTERS (Datos para la Vista) ---
    /**
     * Obtiene el mapa con el estado de listos de todos los jugadores (ID ->
     * Boolean).
     */
    Map<String, Boolean> getJugadoresListos(); // Llamado por VistaSalaEspera

    /**
     * Obtiene la lista de IDs de todos los jugadores en la sala.
     */
    List<String> getIdsJugadoresEnSala(); // Llamado por VistaSalaEspera

    /**
     * Obtiene el ID único de este cliente.
     */
    String getMiId(); // Llamado por VistaSalaEspera

    // --- SETTERS (Llamados para inyección por EnsambladoresMVC) ---
    /**
     * Inyecta el Ensamblador Cliente para crear la red.
     */
    void setEnsambladorCliente(iEnsambladorCliente ensambladorCliente);

    /**
     * Inyecta el puerto de escucha local.
     */
    void setMiPuertoDeEscucha(int miPuertoDeEscucha);

    /**
     * Inyecta el despachador de red.
     */
    void setDespachador(iDespachador despachador);

    /**
     * Inyecta el ID único de este cliente.
     */
    void setMiId(String miId);
    

    // NOTA: El método propertyChange(evt) se incluye porque la interfaz
    // extiende PropertyChangeListener, resolviendo el error del Modelo de Juego.
}
