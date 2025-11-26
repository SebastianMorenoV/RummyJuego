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
        this.esTemporal = true; 
    }

    public GrupoDTO(String tipo, int cantidad, List<FichaJuegoDTO> fichasGrupo, int fila, int columna, boolean esTemporal) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fichasGrupo = fichasGrupo;
        this.fila = fila;
        this.columna = columna;
        this.esTemporal = esTemporal;
    }

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
        sb.append(this.esTemporal); 

        if (this.fichasGrupo != null && !this.fichasGrupo.isEmpty()) {
            sb.append(";"); 

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
     * @param payload El string de payload 
     * @return una nueva instancia de GrupoDTO
     */
    public static GrupoDTO deserializar(String payload) {
        try {

            String[] partesGrupo = payload.split(";", 6);

            String tipo = partesGrupo[0];
            int cantidad = Integer.parseInt(partesGrupo[1]);
            int fila = Integer.parseInt(partesGrupo[2]);
            int columna = Integer.parseInt(partesGrupo[3]);
            boolean esTemporal = Boolean.parseBoolean(partesGrupo[4]);

            List<FichaJuegoDTO> fichas = new ArrayList<>();

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
            return null; 
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
