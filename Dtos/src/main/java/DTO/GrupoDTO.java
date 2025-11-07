package DTO;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa un grupo de fichas en el tablero.
 *
 * @author chris
 */
public class GrupoDTO {

    private String tipo;
    private int cantidad;
    private List<FichaJuegoDTO> fichasGrupo;
    private int fila;
    private int columna;
    private boolean esTemporal;

    public GrupoDTO() {
        this.esTemporal = true; // Por defecto es temporal
    }

    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo, int fila, int columna, boolean esTemporal) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
        this.fila = fila;
        this.columna = columna;
        this.esTemporal = esTemporal;
    }

    // Getters y Setters
    public boolean isEsTemporal() {
        return esTemporal;
    }

    public void setEsTemporal(boolean esTemporal) {
        this.esTemporal = esTemporal;
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
     * Formato: tipo;cantidad;fila;columna;esTemporal;fichaData1|fichaData2
     *
     * @return
     */
    public String serializarParaPayload() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.tipo).append(";");
        sb.append(this.cantidad).append(";");
        sb.append(this.fila).append(";");
        sb.append(this.columna).append(";");
        sb.append(this.esTemporal); // --- ¡NUEVO CAMPO SERIALIZADO! ---

        if (this.fichasGrupo != null && !this.fichasGrupo.isEmpty()) {
            sb.append(";"); // Separador antes de la lista

            for (int i = 0; i < this.fichasGrupo.size(); i++) {
                FichaJuegoDTO ficha = this.fichasGrupo.get(i);
                sb.append(ficha.serializar());
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
     * @param payload El string de payload (ej:
     * "TERCIA;3;4;7;false;ficha1|ficha2")
     * @return una nueva instancia de GrupoDTO
     */
    public static GrupoDTO deserializar(String payload) {
        try {

            // Dividimos en 6 partes como MÁXIMO. La 6ta parte es "todo lo demás" (las fichas)
            String[] partesGrupo = payload.split(";", 6);

            String tipo = partesGrupo[0];
            int cantidad = Integer.parseInt(partesGrupo[1]);
            int fila = Integer.parseInt(partesGrupo[2]);
            int columna = Integer.parseInt(partesGrupo[3]);
            boolean esTemporal = Boolean.parseBoolean(partesGrupo[4]);

            List<FichaJuegoDTO> fichas = new ArrayList<>();

            // Revisar si la 6ta parte (índice 5) existe y no está vacía
            if (partesGrupo.length == 6 && !partesGrupo[5].isEmpty()) {
                String[] dataFichas = partesGrupo[5].split("\\|");

                for (String fichaData : dataFichas) {
                    FichaJuegoDTO ficha = FichaJuegoDTO.deserializar(fichaData);
                    if (ficha != null) {
                        fichas.add(ficha);
                    }
                }
            }

            return new GrupoDTO(tipo, cantidad, fichas, fila, columna, esTemporal);

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

        // Si el payload es nulo O ESTÁ VACÍO, no hay nada que
        // procesar. Devuelve la lista vacía inmediatamente.
        if (payloadLote == null || payloadLote.isEmpty()) {
            return listaGrupos;
        }

        String[] payloadsIndividuales = payloadLote.split("\\$");

        for (String payload : payloadsIndividuales) {
            if (payload != null && !payload.isEmpty()) {
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
        return "GrupoDTO{"
                + "tipo=" + tipo
                + ", cantidad=" + cantidad
                + ", fichasGrupo=" + fichasGrupo + '}';
    }
}
