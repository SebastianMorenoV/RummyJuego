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
import Entidades.Tablero;
import Entidades.Mano;
import Vista.Observador;
import java.awt.Color;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Arrays;
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
    Mano mano;

    public Modelo() {
        observadores = new ArrayList<>();
        tablero = new Tablero();
        mano = new Mano();
    }

    //Definir que es lo que va a mandar el metodo , no puede darle entidades a la interfaz.
    //Deberia traducir a codigo para interfaz
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

//        // 2️⃣ Convertir jugadores
//        List<JugadorDTO> jugadoresDTO = new ArrayList<>();
//        for (Jugador jugador : tablero.getJugadores()) {
//            // Solo agregamos info relevante para la vista
//            JugadorDTO jDTO = new JugadorDTO(jugador.getNombre(), jugador.getFichas().size());
//            jugadoresDTO.add(jDTO);
//        }
//        juegoDTO.setJugadores(jugadoresDTO);
        // 3️⃣ Fichas restantes en mazo
        //  juegoDTO.setFichasMazo(tablero.getMazo() != null ? tablero.getMazo().size() : 0);
        // 4️⃣ Mensaje o jugador actual (opcional)
//        juegoDTO.setJugadorActual(tablero.getJugadorActual() != null
//                ? new JugadorDTO(tablero.getJugadorActual().getNombre(), tablero.getJugadorActual().getFichas().size())
//                : null);
//        juegoDTO.setMensaje("Estado del tablero actualizado");
        return juegoDTO;
    }

    //Probablemente este metodo tenga que traducir lo que la vista debe recibir, segun la informacion de los objetos actualizar la vista.
    @Override
    public List<FichaJuegoDTO> getMano() {
        List<Grupo> gruposMano = mano.getGruposMano();
        List<FichaJuegoDTO> fichasJuegoDTO = new ArrayList<>();
        for (Grupo grupo : gruposMano) {
            List<Ficha> fichas = grupo.getFichas();
            for (Ficha ficha : fichas) {
                Color color = ficha.getColor();
                int numero = ficha.getNumero();
                boolean comodin = ficha.isComodin();
                fichasJuegoDTO.add(new FichaJuegoDTO(numero, color, comodin));
            }

        }
        return fichasJuegoDTO;
    }

    public void iniciarJuego() {
        //Incializar mano en vista.
        crearGruposMano();
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

    public List<Grupo> crearGruposMano() {

        Grupo grupo1 = new Grupo();
        grupo1.setTipo("Escalera");
        grupo1.setNumFichas(13);
        grupo1.getFichas().clear();

        Color[] colores = {Color.RED, Color.BLUE, Color.BLACK, Color.ORANGE};
        Random random = new Random();
        for (int i = 1; i <= 13; i++) {
            Color color = colores[random.nextInt(colores.length)];
            int numero = random.nextInt(13);
            grupo1.getFichas().add(new Ficha(numero, color, false));
        }
        List<Grupo> grupos = new ArrayList<>();
        grupos.add(grupo1);
        mano.setGruposMano(grupos);
        mano.setFichasEnMano(grupos.size());

        return grupos;
    }

    public void colocarFicha(FichaJuegoDTO fichaDTO, int x, int y) {
        Ficha nuevaFicha = new Ficha(fichaDTO.getNumeroFicha(), fichaDTO.getColor(), fichaDTO.isComodin(), x, y);

        // 1️⃣ Intentar agregar a un grupo existente
        Grupo grupoCercano = encontrarGrupo(nuevaFicha);
        if (grupoCercano != null) {
            if (puedeAgregarAFichas(grupoCercano, nuevaFicha)) {
                grupoCercano.getFichas().add(nuevaFicha);
                grupoCercano.setNumFichas(grupoCercano.getFichas().size());
                System.out.println("Ficha agregada a grupo existente: " + grupoCercano);
                notificarObservadores();
            } else {
                System.out.println("La ficha no cumple las reglas del grupo.");
                deshacerGruposIncompletos();
            }
            return;
        }

        // 2️⃣ Lógica de fichaAnterior
        if (fichaAnterior == null) {
            fichaAnterior = nuevaFicha;
            System.out.println("Primera ficha colocada, esperando otra para formar grupo.");
        } else {
            // Si la nueva ficha está cerca de la referencia
            if (estaCerca(fichaAnterior, nuevaFicha.getX(), nuevaFicha.getY())) {
                String tipo = establecerTipoGrupo(fichaAnterior, nuevaFicha);
                if (!tipo.equals("no establecido")) {
                    List<Ficha> grupoFichas = new ArrayList<>(Arrays.asList(fichaAnterior, nuevaFicha));
                    Grupo nuevoGrupo = new Grupo(tipo, grupoFichas.size(), grupoFichas);
                    tablero.getFichasEnTablero().add(nuevoGrupo);
                    System.out.println("Se creó un nuevo grupo: " + nuevoGrupo);
                    fichaAnterior = null;
                    deshacerGruposIncompletos();
                    notificarObservadores();
                } else {
                    // Nueva referencia incompatible → deshacer referencia previa

                    fichaAnterior = nuevaFicha;
                    quitarFichaDeGrupos(fichaAnterior);
                    System.out.println("Fichas incompatibles, se reinicia referencia.");
                    deshacerGruposIncompletos();
                    notificarObservadores();
                }
            } else {
                // Nueva ficha lejos → deshacer referencia previa

                fichaAnterior = nuevaFicha;
                quitarFichaDeGrupos(fichaAnterior);
                System.out.println("La ficha no estaba cerca, se reinicia referencia.");
                deshacerGruposIncompletos();
                notificarObservadores();
            }
        }
    }

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

    private boolean esGrupoValido(Grupo grupo) {
        if (grupo.getTipo().equals("numero")) {
            int size = grupo.getFichas().size();
            return size >= 2 && size <= 4; // válido solo de 2 a 4
        }
        if (grupo.getTipo().equals("escalera")) {
            int size = grupo.getFichas().size();
            return size >= 2 && size <= 13; // válido solo de 3 a 13
        }
        return false;
    }
    // Método para quitar una ficha de cualquier grupo existente

    private void quitarFichaDeGrupos(Ficha ficha) {
        for (Grupo grupo : tablero.getFichasEnTablero()) {
            if (grupo.getFichas().remove(ficha)) {
                grupo.setNumFichas(grupo.getFichas().size());
                System.out.println("🔹 Se quitó la ficha del grupo: " + grupo);
            }
        }
    }

}
