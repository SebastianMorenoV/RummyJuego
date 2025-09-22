/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import DTO.FichaJuegoDTO;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Entidades.Jugador;
import Entidades.Tablero;
import Entidades.Mano;
import Vista.Observador;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author moren
 */
public class Modelo implements IModelo {

    Ficha fichaAnterior;
    List<Observador> observadores;
    Tablero tablero;
    Jugador jugador;

    public Modelo() {
        observadores = new ArrayList<>();
        tablero = new Tablero();
        jugador = new Jugador();
    }

    @Override
    public JuegoDTO getTablero() {
        JuegoDTO juegoDTO = new JuegoDTO();

        // 1️⃣ Convertir grupos del tablero
        List<GrupoDTO> gruposDTO = new ArrayList<>();
        for (Grupo grupo : tablero.getFichasEnTablero()) {
            List<FichaJuegoDTO> fichasDTO = grupo.getFichas().stream()
                    .map(ficha -> new FichaJuegoDTO(
                    ficha.getNumero(),
                    ficha.getColor(),
                    ficha.isComodin()))
                    .toList(); // versión moderna de stream
            GrupoDTO grupoDTO = new GrupoDTO(grupo.getTipo(), grupo.getFichas().size(), fichasDTO);
            gruposDTO.add(grupoDTO);
        }
        juegoDTO.setGruposEnTablero(gruposDTO);
        return juegoDTO;
    }

    /**
     * Metodo para obtener la mano desde la vista , utilizada por modelo.
     *
     * @return un arreglo de FichaJuegoDTO con los datos traducidos de las
     * fichas.
     */
    @Override
    public List<FichaJuegoDTO> getMano() {
        List<Grupo> gruposMano = jugador.getManoJugador().getGruposMano();
        List<FichaJuegoDTO> fichasJuegoDTO = new ArrayList<>();
        for (Grupo grupo : gruposMano) {
            List<Ficha> fichas = grupo.getFichas();
            for (Ficha ficha : fichas) {
                Color color = ficha.getColor();
                int numero = ficha.getNumero();
                boolean comodin = ficha.isComodin();
                int id = ficha.getId();
                fichasJuegoDTO.add(new FichaJuegoDTO(id, numero, color, comodin));
            }
        }
        return fichasJuegoDTO;
    }

    public void iniciarJuego() {
        //Incializar mano en vista.
        //crearGruposMano();
        this.jugador = new Jugador("Sebas", "B1", new Mano());
        crearMazoCompleto();
        repartirMano(jugador);
        notificarObservadores();
    }

    public void notificarObservadores() {
        for (Observador observer : observadores) {
            observer.actualiza(this);
        }
    }

    public void agregarObservador(Observador obs) {
        observadores.add(obs);
    }

    public void colocarFicha(FichaJuegoDTO fichaDTO, int x, int y) {
        Ficha fichaAColocar = fichaDTO.toFicha(x, y);

        // Eliminar la ficha de la mano del jugador
        eliminarFichaDeMano(fichaAColocar);

        // 1. Revisar si la ficha ya está en algún grupo (movimiento dentro del tablero)
        Grupo grupoOrigen = buscarGrupoPorFicha(fichaAColocar.getId());
        if (grupoOrigen != null) {
            // Quitar ficha del grupo actual
            grupoOrigen.getFichas().removeIf(fichaEnGrupo -> fichaEnGrupo.getId() == fichaAColocar.getId());
            grupoOrigen.setNumFichas(grupoOrigen.getFichas().size());

            // Eliminar grupo si queda incompleto
            if (!esGrupoValido(grupoOrigen)) {
                tablero.getFichasEnTablero().remove(grupoOrigen);
                System.out.println("Grupo eliminado por ser incompleto tras mover ficha: " + grupoOrigen);
            }
        }

        // 2. Buscar un grupo cercano donde se pueda agregar
        Grupo grupoCercano = encontrarGrupo(fichaAColocar);
        if (grupoCercano != null && puedeAgregarAFichas(grupoCercano, fichaAColocar)) {
            // Agregar ficha al grupo cercano
            grupoCercano.getFichas().add(fichaAColocar);
            grupoCercano.setNumFichas(grupoCercano.getFichas().size());

            // Limpiar grupos incompletos restantes
            deshacerGruposIncompletos();
            System.out.println("Ficha agregada a grupo existente: " + grupoCercano);
            notificarObservadores();
            return;
        }
        // 3. Si no hay grupo cercano válido, crear un nuevo grupo
        List<Ficha> fichasAGrupo = new ArrayList<>();
        fichasAGrupo.add(fichaAColocar);
        Grupo grupoNuevo = new Grupo("No establecido", fichasAGrupo.size(), fichasAGrupo);
        tablero.getFichasEnTablero().add(grupoNuevo);
        System.out.println("Creacion de grupo nuevo correctamente: " + grupoNuevo);
        notificarObservadores();
    }

    public void crearMazoCompleto() {
        List<Ficha> mazo = tablero.getMazo();
        Color[] colores = {Color.RED, Color.BLUE, Color.BLACK, Color.ORANGE};

        // IDs únicos del 1 al 108
        List<Integer> idsDisponibles = new ArrayList<>();
        for (int i = 1; i <= 108; i++) {
            idsDisponibles.add(i);
        }
        Collections.shuffle(idsDisponibles);

        Random random = new Random();

        // Crear 104 fichas normales (2 sets de 13 números por color)
        for (Color color : colores) {
            for (int set = 0; set < 2; set++) {
                for (int numero = 1; numero <= 13; numero++) {
                    int id = idsDisponibles.remove(0);
                    mazo.add(new Ficha(id, numero, color, false));
                }
            }
        }

        // Crear 4 comodines
        for (int i = 0; i < 4; i++) {
            int id = idsDisponibles.remove(0);
            mazo.add(new Ficha(id, 0, Color.GRAY, true)); // comodines
        }

        Collections.shuffle(mazo); // barajar
        tablero.setMazo(mazo);
    }

    public void repartirMano(Jugador jugador) {
        List<Ficha> mazo = tablero.getMazo();
        List<Ficha> fichasMano = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            fichasMano.add(mazo.remove(0)); // quita del mazo
        }

        // Crear grupo de mano
        Grupo grupo = new Grupo();
        grupo.setTipo("Escalera");
        grupo.setFichas(fichasMano);

        List<Grupo> gruposMano = new ArrayList<>();
        gruposMano.add(grupo);

        Mano mano = jugador.getManoJugador();
        mano.setGruposMano(gruposMano);
        mano.setFichasEnMano(fichasMano.size());
    }

    public void tomarFichaMazo() {
        List<Ficha> mazo = tablero.getMazo();
        if (mazo.isEmpty()) {
            return; // si no hay fichas, nada que hacer
        }
        Ficha ficha = mazo.remove(0); // tomar la primera ficha del mazo (ya está barajada)

        // Agregarla al primer grupo de la mano (o puedes crear lógica para otro grupo)
        Mano manoJugador = jugador.getManoJugador();
        List<Grupo> gruposMano = manoJugador.getGruposMano();
        if (!gruposMano.isEmpty()) {
            gruposMano.get(0).getFichas().add(ficha);
            manoJugador.setFichasEnMano(manoJugador.getFichasEnMano() + 1);
            System.out.println("Se añadio nueva ficha a la mano.");
        }
    }

    ////////////////////////////////METODOS FUERTES/////////////////////////////////////////////////////
    private Grupo encontrarGrupo(Ficha nuevaFicha) {
        return tablero.getFichasEnTablero().stream()
                //encuentra el grupo de la ficha que le pasas como parametro
                .filter(grupo -> grupo.getFichas().stream().anyMatch(f -> estaCerca(f, nuevaFicha.getX(), nuevaFicha.getY())))
                .findFirst()
                .orElse(null);
    }

    private boolean estaCerca(Ficha f, int x, int y) {
        int distancia = 50;
        return Math.abs(f.getX() - x) <= distancia && Math.abs(f.getY() - y) <= distancia;
    }

    private String establecerTipoGrupo(Ficha f1, Ficha f2) {
        if (f1.getNumero() == f2.getNumero() && !f1.getColor().equals(f2.getColor())) {
            return "numero"; // mismo número, distinto color
        }
        if (f1.getColor().equals(f2.getColor())
                && (f1.getNumero() + 1 == f2.getNumero() || f1.getNumero() - 1 == f2.getNumero())) {
            return "escalera"; // consecutivos, mismo color
        }
        return "no establecido";
    }

    private boolean puedeAgregarAFichas(Grupo grupo, Ficha nuevaFicha) {
        List<Ficha> fichas = grupo.getFichas();
        if (grupo.getTipo().equals("No establecido")) {
            Ficha primerFichaGrupo = grupo.getFichas().get(0);
            String tipo = establecerTipoGrupo(primerFichaGrupo, nuevaFicha);
            grupo.setTipo(tipo);
        }

        if (grupo.getTipo().equals("numero")) {
            // Máx 4 fichas, mismo número, colores diferentes
            if (fichas.size() >= 4) {
                return false;
            }
            if (fichas.stream().anyMatch(f -> f.getColor().equals(nuevaFicha.getColor()))) {
                return false;
            }
            return fichas.get(0).getNumero() == nuevaFicha.getNumero();
        }

        if (grupo.getTipo().equals("escalera")) {
            // Máx 13 fichas, misma color, números secuenciales
            if (fichas.size() >= 13) {
                return false;
            }
            if (!fichas.get(0).getColor().equals(nuevaFicha.getColor())) {
                return false;
            }

            List<Integer> numeros = fichas.stream().map(Ficha::getNumero).sorted().toList();
            int min = numeros.get(0), max = numeros.get(numeros.size() - 1);

            return nuevaFicha.getNumero() == min - 1 || nuevaFicha.getNumero() == max + 1;
        }

        return false;
    }

    private void deshacerGruposIncompletos() {
        Iterator<Grupo> it = tablero.getFichasEnTablero().iterator();
        while (it.hasNext()) {
            Grupo grupo = it.next();
            if (!esGrupoValido(grupo)) {
                it.remove();
                System.out.println("❌ Grupo eliminado por ser incompleto: " + grupo);
            }
        }
        notificarObservadores();
    }

    private Grupo buscarGrupoPorFicha(int idFicha) {
        for (Grupo g : tablero.getFichasEnTablero()) {
            for (Ficha f : g.getFichas()) {
                if (f.getId() == idFicha) {
                    return g;
                }
            }
        }
        return null;
    }

    private boolean esGrupoValido(Grupo grupo) {
        int size = grupo.getFichas().size();
        switch (grupo.getTipo()) {
            case "No establecido":
                return size >= 1;  // grupo en construcción
            case "numero":
            case "escalera":
                return size >= 2;  // mínimo 2 fichas para ser válido
            default:
                return false;
        }
    }

    private void eliminarFichaDeMano(Ficha ficha) {
        for (Grupo grupo : jugador.getManoJugador().getGruposMano()) {
            boolean removida = grupo.getFichas().removeIf(f -> f.getId() == ficha.getId());
            if (removida) {
                grupo.setNumFichas(grupo.getFichas().size());
                System.out.println("Ficha eliminada de la mano: " + ficha);
                break; // ya se eliminó, no seguir buscando
            }
        }
    }
}
