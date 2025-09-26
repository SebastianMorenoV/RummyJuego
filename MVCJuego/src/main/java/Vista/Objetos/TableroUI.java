package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.VistaTablero;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class TableroUI extends JPanel {

    private List<GrupoUI> gruposUI;
    // ¡NUEVO ATRIBUTO! Un mapa para recordar la posición de cada grupo.
    private java.util.Map<String, Point> posicionesDeGrupos = new java.util.HashMap<>();
    private IModelo modelo;
    private Controlador control;
    private VistaTablero vista;

    public TableroUI(IModelo modelo, Controlador control, VistaTablero vista) {
        // ... (inicialización básica se mantiene)
        this.vista = vista;
        this.modelo = modelo;
        this.control = control;
        setPreferredSize(new Dimension(880, 275));
        setBorder(new LineBorder(new Color(6, 71, 34), 3, true));

//                setPreferredSize(new Dimension(880, 275)); // tamaño base
//                setBorder(new LineBorder(new Color(6, 71, 34), 3, true));
        this.gruposUI = new java.util.ArrayList<>(); // Inicializar la lista
        setLayout(null); // Mantenemos el layout absoluto para posicionar los GRUPOS
        // ...
    }

    // El paintComponent ahora es mucho más simple. Solo dibuja el fondo.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(15, 89, 46));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        // Las líneas de la cuadrícula pueden ser opcionales ahora.
    }

    /**
     * Lógica principal para cuando se suelta una ficha sobre el tablero. Decide
     * si crear un nuevo grupo o añadir a uno existente.
     */
    // En la clase Vista.Objetos.TableroUI.java
    public void procesarFichaSoltada(FichaUI ficha, Point dropPoint) {
        GrupoUI grupoCercano = encontrarGrupoCercano(dropPoint);

        if (grupoCercano != null) {
            // AÑADIR A GRUPO EXISTENTE
            // Convertimos el punto de soltado a las coordenadas internas del grupoCercano
            Point puntoEnGrupo = javax.swing.SwingUtilities.convertPoint(this, dropPoint, grupoCercano);

            // Llamamos al nuevo método inteligente de agregarFicha
            grupoCercano.agregarFicha(ficha, puntoEnGrupo);

            grupoCercano.setSize(grupoCercano.getPreferredSize());
        } else {
            // CREAR GRUPO NUEVO (esta lógica no cambia)
            GrupoUI nuevoGrupo = new GrupoUI();
            nuevoGrupo.agregarFicha(ficha);
            nuevoGrupo.setSize(nuevoGrupo.getPreferredSize());
            nuevoGrupo.setLocation(dropPoint);
            this.gruposUI.add(nuevoGrupo);
            this.add(nuevoGrupo);
        }
        revalidate();
        repaint();
    }

    private GrupoUI encontrarGrupoCercano(Point punto) {
        for (GrupoUI grupo : gruposUI) {
            // Si el punto donde se soltó la ficha está dentro de los límites de un grupo existente...
            if (grupo.getBounds().contains(punto)) {
                return grupo;
            }
        }
        return null; // No se encontró un grupo cercano
    }

    // El método repintarTablero que hicimos antes, con la lógica de "firmas",
    // ahora es el encargado de sincronizar con el modelo.
    // En la clase TableroUI
    // (Añade el método auxiliar convertirAFichasUI que te di en la respuesta anterior)
    private List<FichaUI> convertirAFichasUI(List<FichaJuegoDTO> fichasDTO) {
        // ...
        return fichasDTO.stream()
                .map(dto -> new FichaUI(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin(), control, vista))
                .collect(Collectors.toList());
    }

    /**
     * Genera una lista de GrupoDTO a partir del estado actual de los GrupoUI en
     * el tablero. Esta lista se enviaría al controlador para que el modelo la
     * valide.
     */
    // Modifica generarListaDeGrupoDTOs para que guarde las posiciones
    public List<GrupoDTO> generarListaDeGrupoDTOs(List<GrupoUI> gruposUI) {
        // Limpiamos el mapa anterior antes de llenarlo de nuevo
        posicionesDeGrupos.clear();

        return gruposUI.stream()
                .map(grupoUI -> {
                    // ... (el código de conversión de fichas se queda igual)
                    List<FichaJuegoDTO> fichasDTO = convertirAFichasDTO(grupoUI.getFichas());

                    // ¡GUARDAMOS LA POSICIÓN!
                    // Usamos la firma del grupo como clave y su ubicación como valor.
                    String firma = generarFirmaDeFichasDTO(fichasDTO);
                    posicionesDeGrupos.put(firma, grupoUI.getLocation());

                    // ... (el resto del método se queda igual)
                    return new GrupoDTO("No establecido", fichasDTO.size(), fichasDTO);
                })
                .collect(Collectors.toList());
    }

    // En la clase TableroUI
    public void repintarTablero() {
        System.out.println("\n--- [DEBUG] Iniciando repintado completo del TableroUI ---");

        // 1. Limpiar el estado visual
        this.removeAll();
        this.gruposUI.clear();
        System.out.println("[DEBUG] Paneles y lista de gruposUI eliminados.");

        // 2. Obtener el estado real del modelo
        JuegoDTO juego = modelo.getTablero();
        List<GrupoDTO> gruposDelModelo = juego.getGruposEnTablero();
        System.out.println("[DEBUG] Obtenidos " + gruposDelModelo.size() + " grupos desde el Modelo.");
        System.out.println("[DEBUG] Grupos recibidos: " + gruposDelModelo);

        // Imprime el contenido del mapa de posiciones que se guardó en el turno.
        System.out.println("[DEBUG] Mapa de posiciones guardado: " + posicionesDeGrupos);

        // 3. Crear y añadir los GrupoUI basados en el modelo
        for (GrupoDTO grupoDTO : gruposDelModelo) {
            System.out.println("\n[DEBUG] Procesando grupo DTO: " + grupoDTO.getFichasGrupo());

            if (grupoDTO.getTipo().equals("Invalido")) {
                System.out.println("[DEBUG] >> Grupo marcado como 'Invalido'. Omitiendo.");
                continue;
            }

            GrupoUI grupoUI = new GrupoUI();
            List<FichaUI> fichas = convertirAFichasUI(grupoDTO.getFichasGrupo());
            grupoUI.setFichas(fichas);
            grupoUI.setSize(grupoUI.getPreferredSize());
            System.out.println("[DEBUG] Creado GrupoUI con " + fichas.size() + " fichas y tamaño " + grupoUI.getSize());

            // --- SECCIÓN DE DEPURACIÓN DE POSICIONES ---
            String firma = generarFirmaDeGrupoDTO(grupoDTO);
            System.out.println("[DEBUG] Generada firma para el grupo: '" + firma + "'");

            Point posicionOriginal = posicionesDeGrupos.get(firma);

            if (posicionOriginal != null) {
                System.out.println("[DEBUG] >> ¡Éxito! Posición encontrada en el mapa: " + posicionOriginal);
                grupoUI.setLocation(posicionOriginal);
            } else {
                System.err.println("[DEBUG] >> ¡ADVERTENCIA! No se encontró posición para la firma '" + firma + "'. Usando posición por defecto.");
                grupoUI.setLocation(10, 10);
            }
            // --- FIN DE SECCIÓN DE DEPURACIÓN ---

            this.add(grupoUI);
            this.gruposUI.add(grupoUI);
            System.out.println("[DEBUG] GrupoUI añadido al panel en la posición: " + grupoUI.getLocation());
        }

        System.out.println("\n[DEBUG] Repintado finalizado. Llamando a revalidate() y repaint().");
        revalidate();
        repaint();
        System.out.println("--- [DEBUG] Fin del ciclo de repintado ---\n");
    }

    // Necesitarás estos dos métodos auxiliares para generar las firmas consistentemente
    private String generarFirmaDeGrupoDTO(GrupoDTO grupo) {
        return grupo.getFichasGrupo().stream()
                .map(FichaJuegoDTO::getIdFicha).sorted()
                .map(String::valueOf).collect(Collectors.joining("-"));
    }

    private String generarFirmaDeFichasDTO(List<FichaJuegoDTO> fichas) {
        return fichas.stream()
                .map(FichaJuegoDTO::getIdFicha).sorted()
                .map(String::valueOf).collect(Collectors.joining("-"));
    }

    public void recogerFichaDeGrupo(FichaUI ficha) {
        GrupoUI grupoDeOrigen = null;
        for (GrupoUI grupo : gruposUI) {
            if (grupo.getFichas().stream().anyMatch(f -> f.getIdFicha() == ficha.getIdFicha())) {
                grupoDeOrigen = grupo;
                break;
            }
        }

        if (grupoDeOrigen != null) {
            grupoDeOrigen.removerFicha(ficha);

            if (grupoDeOrigen.getFichas().isEmpty()) {
                gruposUI.remove(grupoDeOrigen);
                this.remove(grupoDeOrigen);
            } else {
                grupoDeOrigen.setSize(grupoDeOrigen.getPreferredSize());
            }

            revalidate();
            repaint();
        }
    }

    /**
     * Método auxiliar para convertir una lista de FichaUI a FichaJuegoDTO. Es
     * el inverso del método que teníamos antes.
     */
    private List<FichaJuegoDTO> convertirAFichasDTO(List<FichaUI> fichasUI) {
        return fichasUI.stream()
                .map(fichaUI -> {
                    // Creamos un nuevo DTO con la información de la FichaUI
                    return new FichaJuegoDTO(
                            fichaUI.getIdFicha(),
                            fichaUI.getNumero(), // Asumiendo que FichaUI tiene getNumero()
                            fichaUI.getColor(), // Asumiendo que FichaUI tiene getColor()
                            fichaUI.isComodin() // Asumiendo que FichaUI tiene isComodin()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<GrupoUI> getGruposUI() {
        return gruposUI;
    }

    public void setGruposUI(List<GrupoUI> gruposUI) {
        this.gruposUI = gruposUI;
    }

}
