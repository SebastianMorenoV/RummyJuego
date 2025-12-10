package GestorDeSonidos;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class GestorSonidos {

    // Nombres de archivos sugeridos (guárdalos en src/main/resources/sonidos/)
    public static final String SONIDO__SELECCIONAR = "seleccionar.wav";
    public static final String SONIDO_ENOJO = "enojo.wav";
    public static final String SONIDO_LLORO = "lloriqueo.wav";
    public static final String SONIDO_RISA = "risa.wav";
    public static final String SONIDO_CLICK = "carta2.wav";
    public static final String SONIDO_SOLTAR = "click.wav";     // Ficha colocada bien
    public static final String SONIDO_ERROR = "error.wav";     // Jugada inválida
    public static final String SONIDO_TOMAR = "carta1.wav";      // Tomar del mazo
    public static final String SONIDO_CAMBIOTURNO = "turno.wav";
    public static final String SONIDO_EFECTO = "efecto.wav";
    public static final String SONIDO_BARAJACARTAS = "barajaCartas.wav";

    public static void reproducir(String nombreArchivo) {
        new Thread(() -> { // Ejecutar en hilo separado para no congelar la UI
            try {
                // Busca en la carpeta de recursos
                URL url = GestorSonidos.class.getResource("/sonidos/" + nombreArchivo);

                if (url == null) {
                    System.err.println("⚠ Audio no encontrado: " + nombreArchivo);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
