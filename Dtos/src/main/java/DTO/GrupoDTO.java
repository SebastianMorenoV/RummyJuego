package DTO;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris
 */
public class GrupoDTO {

    private String tipo;
    private int cantidad;
    private List<FichaJuegoDTO> fichasGrupo;
    private int fila;
    private int columna;

    public GrupoDTO() {
    }

    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo, int fila, int columna) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<FichaJuegoDTO> getFichasGrupo() {
        return fichasGrupo;
    }

    public void setFichasGrupo(List<FichaJuegoDTO> fichasGrupo) {
        this.fichasGrupo = fichasGrupo;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Serializa los datos del grupo en un string plano para el payload.
     *
     * Formato: tipo;cantidad;fila;columna;fichaData1|fichaData2|fichaData3
     * Donde 'fichaData' es el string de FichaJuegoDTO.serializar()
     *
     * @return
     */
    public String serializarParaPayload() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.tipo).append(";");
        sb.append(this.cantidad).append(";");
        sb.append(this.fila).append(";");
        sb.append(this.columna);

        if (this.fichasGrupo != null && !this.fichasGrupo.isEmpty()) {
            sb.append(";"); // Separador antes de la lista

            for (int i = 0; i < this.fichasGrupo.size(); i++) {
                FichaJuegoDTO ficha = this.fichasGrupo.get(i);

                // Ya no usamos ficha.toString(), usamos ficha.serializar()
                sb.append(ficha.serializar());

                // Añadir el separador de lista '|' si no es la última ficha
                if (i < this.fichasGrupo.size() - 1) {
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Método estático (factory) para crear un GrupoDTO completo desde el
     * payload recibido por el servidor.
     *
     * @param payload El string de payload (ej: "TERCIA;3;4;7;ficha1|ficha2")
     * @return una nueva instancia de GrupoDTO
     */
    public static GrupoDTO deserializar(String payload) {
        try {

            // Dividimos en 5 partes como MÁXIMO. La 5ta parte es "todo lo demás" (las fichas)
            String[] partesGrupo = payload.split(";", 5);

            String tipo = partesGrupo[0];
            int cantidad = Integer.parseInt(partesGrupo[1]);
            int fila = Integer.parseInt(partesGrupo[2]);
            int columna = Integer.parseInt(partesGrupo[3]);

            List<FichaJuegoDTO> fichas = new ArrayList<>();

            // Revisar si la 5ta parte (índice 4) existe y no está vacía
            if (partesGrupo.length == 5 && !partesGrupo[4].isEmpty()) {

                // split() usa regex. El pipe '|' es un carácter especial, por eso debemos "escaparlo" con doble backslash: \\|
                String[] dataFichas = partesGrupo[4].split("\\|");

                for (String fichaData : dataFichas) {
                    FichaJuegoDTO ficha = FichaJuegoDTO.deserializar(fichaData);
                    if (ficha != null) {
                        fichas.add(ficha);
                    }
                }
            }

            return new GrupoDTO(tipo, cantidad, fichas, fila, columna);

        } catch (Exception e) {
            System.err.println("ERROR al deserializar GrupoDTO: " + payload);
            e.printStackTrace();
            return null; // Devuelve null si el formato es incorrecto
        }
    }

    /**
     * Método estático para deserializar un payload que contiene MÚLTIPLES
     * grupos separados por '$'.
     *
     * @param payloadLote El string de payload (ej:
     * "grupoPayload1$grupoPayload2")
     * @return una LISTA de GrupoDTO
     */
    public static List<GrupoDTO> deserializarLista(String payloadLote) {
        List<GrupoDTO> listaGrupos = new ArrayList<>();

        // 1. Divide el lote en payloads de grupos individuales
        // Usamos "\\$" porque '$' es un carácter especial en regex
        String[] payloadsIndividuales = payloadLote.split("\\$");

        // 2. Deserializa cada payload individual
        for (String payload : payloadsIndividuales) {
            System.out.println(payload);
            if (payload != null && !payload.isEmpty()) {

                // Llama al método deserializar()
                GrupoDTO grupo = GrupoDTO.deserializar(payload);
                if (grupo != null) {
                    listaGrupos.add(grupo);
                }
            }
        }
        return listaGrupos;
    }

    @Override
    public String toString() {
        return "GrupoDTO{" + 
                "tipo=" + tipo + 
                ", cantidad=" + cantidad + 
                ", fichasGrupo=" + fichasGrupo + '}';
    }
}
