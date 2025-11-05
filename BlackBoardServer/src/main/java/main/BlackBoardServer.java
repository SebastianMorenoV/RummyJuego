package main;

import control.ControladorBlackboard;
import directorio.Directorio;
import pizarra.EstadoJuegoPizarra;
import com.mycompany.tcpejemplo.DespachadorAsincrono; // Necesaria para el Directorio
import Ensambladores.EnsambladorServidor; // Importa el Ensamblador y las Interfaces
import contratos.iEnsambladorServidor;
import contratos.iControladorBlackboard;
import contratos.iDespachador;
import contratos.iDirectorio;
import contratos.iListener;
import contratos.iObservador; // Necesaria para conectar Pizarra y Controlador
import contratos.iAgenteConocimiento; // Para la lista de agentes
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian Moreno
 */
public class BlackBoardServer {

    public static void main(String[] args) {
        final int PUERTO_DE_ESCUCHA = 5000;
        System.out.println("Iniciando Servidor de Rummy...");

        // 1. CREACIÓN DE COMPONENTES (Lógica movida desde el ensamblador)
        
        // 1.A. Crear Despachador (El que envía)
        iDespachador despachador = new DespachadorAsincrono();

        // 1.B. Crear Directorio (El que sabe "dónde viven")
        iDirectorio directorio = new Directorio(despachador);

        // 1.C. Crear Pizarra (El estado)
        EstadoJuegoPizarra pizarra = new EstadoJuegoPizarra(); 

        // 1.D. Crear Controlador y Agentes (La inteligencia)
        List<iAgenteConocimiento> agentes = new ArrayList<>();
        
        // Agregar agentes: new AgenteMovimiento(pizarra)
        iControladorBlackboard controladorBlackboard = new ControladorBlackboard(agentes, directorio);

        // 1.E. Conectar Pizarra -> Controlador (Observer)
        pizarra.addObservador((iObservador) controladorBlackboard);

        // 2. ENSAMBLAJE DE RED (Usando el módulo externo)
        
        // 2.A. Instanciar el ensamblador
        iEnsambladorServidor ensamblador = new EnsambladorServidor();
        
        // 2.B. Llamar al método e INYECTAR los componentes ya creados
        iListener listenerServidor = ensamblador.ensamblarRedServidor(
                pizarra, 
                despachador, 
                directorio
        );

        // 3. Iniciar el Servidor
        try {
            System.out.println("[Servidor Main] Escuchando en el puerto " + PUERTO_DE_ESCUCHA);
            listenerServidor.iniciar(PUERTO_DE_ESCUCHA);
        } catch (IOException e) {
            System.err.println("[Servidor Main] ERROR FATAL: No se pudo iniciar el servidor.");
            e.printStackTrace();
        }
    }
}