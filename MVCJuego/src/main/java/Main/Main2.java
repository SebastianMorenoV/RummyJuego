package Main;

import Controlador.Controlador;
import Modelo.Modelo;
import Vista.VistaTablero;
// Importa las interfaces y la nueva clase
import contratos.iDespachador;
import contratos.iListener;
import contratos.iEnsambladorCliente;
import Ensambladores.EnsambladorCliente;
import java.io.IOException;

public class Main2 {

    public static void main(String[] args) {
        // --- 1. Creación de Componentes MVC ---
        Modelo modelo = new Modelo();
        Controlador controlador = new Controlador(modelo);
        VistaTablero vistaJugador1 = new VistaTablero(controlador);
        modelo.agregarObservador(vistaJugador1);

        // --- 2. Configuración de Red ---
        String miId = "BenjaminSC"; 
        String ipServidor = "192.168.100.98"; 
        int puertoServidor = 5000;
        int miPuertoDeEscucha = 9004; 

        // --- 3. Ensamblaje de Componentes de Red (REFACTORIZADO) ---
        System.out.println("[Main] Iniciando ensamblaje de red...");
        
        // Instanciamos el nuevo ensamblador
        iEnsambladorCliente ensamblador = new EnsambladorCliente();
        
        // Creamos el despachador y el listener por separado
        iDespachador despachador = ensamblador.crearDespachador(ipServidor, puertoServidor);
        iListener listener = ensamblador.crearListener(miId, modelo); // El modelo es el 'oyente'

        // --- 4. Inyección de Dependencias ---
        // Se le da al Modelo la capacidad de enviar mensajes
        modelo.setDespachador(despachador);
        modelo.setMiId(miId);

        // --- 5. Iniciar Escucha en Segundo Plano ---
        // Usamos la variable 'listener'
        new Thread(() -> {
            try {
                System.out.println("[Main] Iniciando listener en el puerto " + miPuertoDeEscucha);
                listener.iniciar(miPuertoDeEscucha); // <--- Variable separada
            } catch (IOException e) {
                System.err.println("[Main] Error fatal al iniciar el listener: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // --- 6. Registrarse en el Servidor e Iniciar el Juego ---
        try {
            String mensajeRegistro = miId + ":REGISTRAR:" + miPuertoDeEscucha;
            despachador.enviar(mensajeRegistro); // <--- Variable separada
        } catch (IOException ex) {
            System.err.println("[Main] No se pudo conectar con el servidor para registrarse: " + ex.getMessage());
        }

        // Finalmente, se muestra la ventana del juego.
        vistaJugador1.setVisible(true);
        controlador.iniciarJuego();
    }
}