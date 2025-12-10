package DTO;

/**
 * DTO que representa a un jugador en la partida.
 *
 * @author benja
 */
public class JugadorDTO {

    private String nombre;
    private int fichasRestantes;
    private ManoDTO manoDTO;
    private boolean esTurno;
    
    //CURegistrarUsuario
    private int idAvatar;
    private int[] colores;
    
    public JugadorDTO() {
    }

    public JugadorDTO(String nombre, int fichasRestantes, boolean esTurno) {
        this.nombre = nombre;
        this.fichasRestantes = fichasRestantes;
        this.esTurno = esTurno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFichasRestantes() {
        return fichasRestantes;
    }

    public void setFichasRestantes(int fichasRestantes) {
        this.fichasRestantes = fichasRestantes;
    }

    public boolean isEsTurno() {
        return esTurno;
    }

    public void setEsTurno(boolean esTurno) {
        this.esTurno = esTurno;
    }

    public ManoDTO getManoDTO() {
        return manoDTO;
    }

    public void setManoDTO(ManoDTO manoDTO) {
        this.manoDTO = manoDTO;
    }

    public int getIdAvatar() {
        return idAvatar;
    }

    public void setIdAvatar(int idAvatar) {
        this.idAvatar = idAvatar;
    }

    
    public int[] getColores() {
        return colores;
    }

    public void setColores(int[] colores) {
        this.colores = colores;
    }

    @Override
    public String toString() {
        return "JugadorDTO{" + "nombre=" + nombre + ", fichasRestantes=" + fichasRestantes + ", manoDTO=" + manoDTO + ", esTurno=" + esTurno + ", idAvatar=" + idAvatar + ", colores=" + colores + '}';
    }

    
    
}
