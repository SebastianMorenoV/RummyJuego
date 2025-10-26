    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
     */
    package Main;

    import Controlador.Controlador;
    import Modelo.Modelo;
    import Vista.VistaTablero;
    import com.mycompany.tcpejemplo.utils.ComponentesRedCliente;
    import com.mycompany.tcpejemplo.Ensamblador;
    import java.io.IOException;

    /**
     *
     * @author benja
     */
    public class Main {

        /**
         *
         * @param args the command line arguments
         */
        public static void main(String[] args) {
            // --- 1. Creación de Componentes MVC ---
            // Se crean los objetos principales de la lógica del juego.
            Modelo modelo = new Modelo();
            Controlador controlador = new Controlador(modelo);
            VistaTablero vistaJugador1 = new VistaTablero(controlador); // Asumiendo que esta es tu clase de UI

            // El Modelo necesita saber a quién notificar (la Vista).
            modelo.agregarObservador(vistaJugador1);

            // --- 2. Configuración de Red ---
            // Se definen los datos de conexión en un solo lugar.
            String miId = "SebastianM"; // O el ID del jugador actual
            String ipServidor = "192.168.100.3"; // La IP del servidor central
            int puertoServidor = 5000;
            int miPuertoDeEscucha = 9004; // El puerto donde ESTE cliente escuchará

            // --- 3. Ensamblaje de Componentes de Red ---
            // Se le pide a la "fábrica" (Ensamblador) que construya y conecte
            // los objetos de red.
            System.out.println("[Main] Iniciando ensamblaje de red...");
            ComponentesRedCliente misComponentesDeRed = Ensamblador.ensamblarCliente(
                    miId,
                    ipServidor,
                    puertoServidor,
                    modelo // El Modelo es el que escucha los eventos que llegan de la red
            );

            // --- 4. Inyección de Dependencias ---
            // Se le da al Modelo la capacidad de enviar mensajes (el "despachador"),
            // sin que el Modelo sepa cómo o a dónde se envían.
            // (Asegúrate de tener un método setDespachador en tu clase Modelo).
            modelo.setDespachador(misComponentesDeRed.despachador);
            modelo.setMiId(miId);

            // --- 5. Iniciar Escucha en Segundo Plano ---
            // El listener (el "mesero") se ejecuta en su propio hilo para no congelar
            // la interfaz gráfica del juego.
            new Thread(() -> {
                try {
                    System.out.println("[Main] Iniciando listener en el puerto " + miPuertoDeEscucha);
                    misComponentesDeRed.listener.iniciar(miPuertoDeEscucha);
                } catch (IOException e) {
                    System.err.println("[Main] Error fatal al iniciar el listener: " + e.getMessage());
                    // Aquí podrías mostrar una ventana de error al usuario.
                    e.printStackTrace();
                }
            }).start();

            // --- 6. Registrarse en el Servidor e Iniciar el Juego ---
            // Ahora que la red está lista, el cliente se registra y la UI se hace visible.
            try {
                // El cliente le informa al servidor que existe y en qué puerto escucha.
                String mensajeRegistro = miId + ":REGISTRAR:" + miPuertoDeEscucha;
                misComponentesDeRed.despachador.enviar(mensajeRegistro);
            } catch (IOException ex) {
                System.err.println("[Main] No se pudo conectar con el servidor para registrarse: " + ex.getMessage());
                // Aquí podrías mostrar un error y cerrar la aplicación.
            }

            // Finalmente, se muestra la ventana del juego.
            vistaJugador1.setVisible(true);
            // NOTA: El inicio real del juego (repartir fichas, etc.) probablemente
            // debería ser activado por un mensaje del servidor, no llamando a
            // controlador.iniciarJuego() directamente aquí. Pero para empezar, está bien.
            controlador.iniciarJuego();
        }
    }
