package Vista.Objetos;

import Controlador.Controlador;
import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Modelo.IModelo;
import Vista.VistaTablero;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class TableroUI extends JPanel {

    // --- Atributos de la Cuadrícula ---
    private final int FILAS = 5;
    private final int COLUMNAS = 22;
    // **NUEVA CONSTANTE:** Define qué tan lejos (en celdas) puede estar una ficha para unirse a un grupo.
    private final int RANGO_DE_ATRACCION_DE_CELDA = 3;

    // --- Estado de la UI ---
    private List<GrupoUI> gruposUI;
    private Point celdaResaltada = null;
    private Map<String, Point> celdasDeGrupos = new HashMap<>();

    // --- Referencias MVC ---
    private final IModelo modelo;
    private final Controlador control;
    private final VistaTablero vista;

    public TableroUI(IModelo modelo, Controlador control, VistaTablero vista) {
        this.vista = vista;
        this.modelo = modelo;
        this.control = control;
        this.gruposUI = new ArrayList<>();
        setLayout(null);
    }

    // <editor-fold defaultstate="collapsed" desc="Métodos que ya funcionan bien (sin cambios)">
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(15, 89, 46));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        int anchoCelda = getWidth() / COLUMNAS;
        int altoCelda = getHeight() / FILAS;
        if (celdaResaltada != null) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(celdaResaltada.x * anchoCelda, celdaResaltada.y * altoCelda, anchoCelda, altoCelda);
        }
        g.setColor(new Color(0, 0, 0, 40));
        for (int r = 0; r <= FILAS; r++) {
            g.drawLine(0, r * altoCelda, getWidth(), r * altoCelda);
        }
        for (int c = 0; c <= COLUMNAS; c++) {
            g.drawLine(c * anchoCelda, 0, c * anchoCelda, getHeight());
        }
    }

    public void procesarFichaSoltada(FichaUI ficha, Component dropTarget, Point dropPoint) {
        Point celda = calcularCeldaParaPunto(dropPoint);
        GrupoUI grupoAdyacente = encontrarGrupoEnCeldaAdyacente(celda);
        if (grupoAdyacente != null) {
            Point celdaDelGrupo = calcularCeldaParaPunto(grupoAdyacente.getLocation());
            Point puntoEnGrupo;
            if (celda.x < celdaDelGrupo.x) {
                puntoEnGrupo = new Point(0, 0);
                int colDestino = celdaDelGrupo.x - 1;
                grupoAdyacente.setLocation(calcularPosicionCentradaDeCelda(new Point(colDestino, celda.y)));
            } else {
                puntoEnGrupo = new Point(Integer.MAX_VALUE, 0);
            }
            grupoAdyacente.agregarFicha(ficha, puntoEnGrupo);
            grupoAdyacente.setSize(grupoAdyacente.getPreferredSize());
        } else {
            Point posicionCentrada = calcularPosicionCentradaDeCelda(celda);
            GrupoUI nuevoGrupo = new GrupoUI();
            nuevoGrupo.agregarFicha(ficha);
            nuevoGrupo.setSize(nuevoGrupo.getPreferredSize());
            nuevoGrupo.setLocation(posicionCentrada);
            this.gruposUI.add(nuevoGrupo);
            this.add(nuevoGrupo);
        }
        revalidate();
        repaint();
    }

    // En la clase Vista.Objetos.TableroUI.java
    public void recogerFichaDeGrupo(FichaUI ficha) {
        GrupoUI grupoDeOrigen = null;
        for (GrupoUI grupo : gruposUI) {
            if (grupo.getFichas().stream().anyMatch(f -> f.getIdFicha() == ficha.getIdFicha())) {
                grupoDeOrigen = grupo;
                break;
            }
        }

        if (grupoDeOrigen != null) {
            int indice = grupoDeOrigen.getIndiceDeFicha(ficha);
            if (indice == -1) {
                return; // La ficha no se encontró, no hacer nada.
            }
            // Obtenemos la lista completa de fichas ANTES de hacer cualquier cambio.
            List<FichaUI> fichasOriginales = new ArrayList<>(grupoDeOrigen.getFichas());
            Point posicionOriginal = grupoDeOrigen.getLocation();

            // **INICIO DE LA NUEVA LÓGICA DE DIVISIÓN**
            // 1. Quita el grupo original del tablero. Lo vamos a reemplazar por completo.
            gruposUI.remove(grupoDeOrigen);
            this.remove(grupoDeOrigen);

            // 2. Crea las listas de las fichas que quedaron a la izquierda y a la derecha.
            List<FichaUI> fichasIzquierda = new ArrayList<>(fichasOriginales.subList(0, indice));
            List<FichaUI> fichasDerecha = new ArrayList<>(fichasOriginales.subList(indice + 1, fichasOriginales.size()));

            // 3. Si quedaron fichas a la izquierda, crea un grupo para ellas.
            if (!fichasIzquierda.isEmpty()) {
                GrupoUI grupoIzquierdo = new GrupoUI();
                grupoIzquierdo.setFichas(fichasIzquierda);
                grupoIzquierdo.setSize(grupoIzquierdo.getPreferredSize());
                grupoIzquierdo.setLocation(posicionOriginal); // Mantiene la posición original del grupo.

                gruposUI.add(grupoIzquierdo);
                this.add(grupoIzquierdo);
            }

            // 4. Si quedaron fichas a la derecha, crea un grupo para ellas.
            if (!fichasDerecha.isEmpty()) {
                GrupoUI grupoDerecho = new GrupoUI();
                grupoDerecho.setFichas(fichasDerecha);
                grupoDerecho.setSize(grupoDerecho.getPreferredSize());

                // Calculamos la posición del nuevo grupo de la derecha.
                Point celdaOriginal = calcularCeldaParaPunto(posicionOriginal);
                // La nueva columna es donde empezaba el grupo + el tamaño del grupo izquierdo + 1 (por la ficha que quitamos).
                int nuevaColumna = celdaOriginal.x + fichasIzquierda.size() + 1;
                Point nuevaCelda = new Point(nuevaColumna, celdaOriginal.y);

                grupoDerecho.setLocation(calcularPosicionCentradaDeCelda(nuevaCelda));

                gruposUI.add(grupoDerecho);
                this.add(grupoDerecho);
            }

            // 5. Finalmente, refrescamos el tablero para que todos los cambios se hagan visibles.
            revalidate();
            repaint();
        }
    }

    public void resaltarCeldaEn(Point puntoEnTablero) {
        Point nuevaCelda = calcularCeldaParaPunto(puntoEnTablero);
        if (!nuevaCelda.equals(this.celdaResaltada)) {
            this.celdaResaltada = nuevaCelda;
            repaint();
        }
    }

    public void limpiarResaltado() {
        if (this.celdaResaltada != null) {
            this.celdaResaltada = null;
            repaint();
        }
    }

    public void repintarTablero() {
        this.removeAll();
        this.gruposUI.clear();
        JuegoDTO juego = modelo.getTablero();
        List<GrupoDTO> gruposDelModelo = juego.getGruposEnTablero();
        for (GrupoDTO grupoDTO : gruposDelModelo) {
            if ("Invalido".equals(grupoDTO.getTipo())) {
                continue;
            }
            GrupoUI grupoUI = new GrupoUI();
            List<FichaUI> fichas = convertirAFichasUI(grupoDTO.getFichasGrupo());
            grupoUI.setFichas(fichas);
            grupoUI.setSize(grupoUI.getPreferredSize());
            String firma = generarFirmaDeGrupoDTO(grupoDTO);
            Point celdaGuardada = celdasDeGrupos.get(firma);
            if (celdaGuardada != null) {
                grupoUI.setLocation(calcularPosicionCentradaDeCelda(celdaGuardada));
            } else {
                grupoUI.setLocation(calcularPosicionCentradaDeCelda(new Point(1, gruposUI.size())));
            }
            this.add(grupoUI);
            this.gruposUI.add(grupoUI);
        }
        revalidate();
        repaint();
    }

    public List<GrupoDTO> generarListaDeGrupoDTOs() {
        celdasDeGrupos.clear();
        return gruposUI.stream()
                .map(grupoUI -> {
                    List<FichaJuegoDTO> fichasDTO = convertirAFichasDTO(grupoUI.getFichas());
                    String firma = generarFirmaDeFichasDTO(fichasDTO);
                    Point celdaDelGrupo = calcularCeldaParaPunto(grupoUI.getLocation());
                    celdasDeGrupos.put(firma, celdaDelGrupo);
                    return new GrupoDTO("No establecido", fichasDTO.size(), fichasDTO);
                })
                .collect(Collectors.toList());
    }

    private Point calcularCeldaParaPunto(Point puntoEnPixeles) {
        int anchoCelda = getWidth() / COLUMNAS;
        int altoCelda = getHeight() / FILAS;
        int col = Math.max(0, Math.min(puntoEnPixeles.x / anchoCelda, COLUMNAS - 1));
        int fila = Math.max(0, Math.min(puntoEnPixeles.y / altoCelda, FILAS - 1));
        return new Point(col, fila);
    }

    private Point calcularPosicionCentradaDeCelda(Point celda) {
        int anchoCelda = getWidth() / COLUMNAS;
        int altoCelda = getHeight() / FILAS;
        final int FICHA_ANCHO = 30;
        final int FICHA_ALTO = 45;
        int xOffset = (anchoCelda - FICHA_ANCHO) / 2;
        int yOffset = (altoCelda - FICHA_ALTO) / 2;
        int x = (celda.x * anchoCelda) + xOffset;
        int y = (celda.y * altoCelda) + yOffset;
        return new Point(x, y);
    }
    // </editor-fold>

    /**
     * **MÉTODO MODIFICADO:** Ahora busca grupos en un rango más amplio.
     */
    private GrupoUI encontrarGrupoEnCeldaAdyacente(Point celdaObjetivo) {
        for (GrupoUI grupo : gruposUI) {
            Point celdaGrupo = calcularCeldaParaPunto(grupo.getLocation());

            // Solo buscamos en la misma fila
            if (celdaGrupo.y == celdaObjetivo.y) {
                int colInicioGrupo = celdaGrupo.x;
                int colFinGrupo = colInicioGrupo + grupo.getFichas().size() - 1;

                // Comprobar si la celda objetivo está a la DERECHA del grupo, dentro del rango
                if (celdaObjetivo.x > colFinGrupo && celdaObjetivo.x - colFinGrupo <= RANGO_DE_ATRACCION_DE_CELDA) {
                    return grupo;
                }
                // Comprobar si la celda objetivo está a la IZQUIERDA del grupo, dentro del rango
                if (celdaObjetivo.x < colInicioGrupo && colInicioGrupo - celdaObjetivo.x <= RANGO_DE_ATRACCION_DE_CELDA) {
                    return grupo;
                }
            }
        }
        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="Métodos de conversión de DTOs (sin cambios)">
    private List<FichaUI> convertirAFichasUI(List<FichaJuegoDTO> fichasDTO) {
        return fichasDTO.stream()
                .map(dto -> new FichaUI(dto.getIdFicha(), dto.getNumeroFicha(), dto.getColor(), dto.isComodin(), control, vista))
                .collect(Collectors.toList());
    }

    private List<FichaJuegoDTO> convertirAFichasDTO(List<FichaUI> fichasUI) {
        return fichasUI.stream()
                .map(fichaUI -> new FichaJuegoDTO(
                fichaUI.getIdFicha(),
                fichaUI.getNumero(),
                fichaUI.getColor(),
                fichaUI.isComodin()
        ))
                .collect(Collectors.toList());
    }

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

    public List<GrupoUI> getGruposUI() {
        return gruposUI;
    }
    // </editor-fold>
}
