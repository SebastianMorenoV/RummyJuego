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
import Vista.TipoEvento;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author moren
 */
public class Modelo implements IModelo {

    Ficha fichaAnterior;
    List<Observador> observadores;
    Tablero tablero;
    Jugador jugador;
    Tablero tableroAnterior; // copia del tablero al inicio del turno
    List<Ficha> fichasJugadorAlInicioTurno; // para rastrear solo las fichas del jugador

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
                    ficha.getId(),
                    ficha.getNumero(),
                    ficha.getColor(),
                    ficha.isComodin(),
                    ficha.getX(), ficha.getY()))
                    .toList(); // versión moderna de stream
            GrupoDTO grupoDTO = new GrupoDTO(grupo.getTipo(), grupo.getFichas().size(), fichasDTO);
            gruposDTO.add(grupoDTO);
        }
        juegoDTO.setGruposEnTablero(gruposDTO);
        juegoDTO.setFichasMazo(tablero.getMazo().size());
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
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : observadores) {
            observer.actualiza(this, tipoEvento);
        }
    }

    public void agregarObservador(Observador obs) {
        observadores.add(obs);
    }

    public void colocarFicha(FichaJuegoDTO fichaDTO, int x, int y) {
        // 1. Crear la ficha a colocar
        Ficha fichaAColocar = fichaDTO.toFicha(x, y);

        // 2. Eliminar la ficha de la mano
        eliminarFichaDeMano(fichaAColocar);

        // 3. Quitar la ficha de cualquier grupo donde ya esté
        for (Grupo g : tablero.getFichasEnTablero()) {
            g.getFichas().removeIf(f -> f.getId() == fichaAColocar.getId());
        }

        // 4. Agregar la ficha directamente a la lista de fichas del tablero
        List<Ficha> todasFichas = new ArrayList<>();
        for (Grupo g : tablero.getFichasEnTablero()) {
            todasFichas.addAll(g.getFichas());
        }
        todasFichas.add(fichaAColocar);

        // 5. Limpiar grupos actuales y reorganizar con TODAS las fichas
        tablero.getFichasEnTablero().clear();
        reorganizarGruposTablero(todasFichas);

        // 6. Notificar a los observadores
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);

        System.out.println("Ficha colocada y grupos reorganizados correctamente: " + fichaAColocar);
    }

    public void reorganizarGruposTablero(List<Ficha> todasFichas) {
        List<Ficha> fichasOrdenadas = new ArrayList<>(todasFichas);
        fichasOrdenadas.sort(Comparator.comparingInt(Ficha::getY).thenComparingInt(Ficha::getX));

        List<Grupo> gruposFinales = new ArrayList<>();
        boolean[] usadas = new boolean[fichasOrdenadas.size()];

        for (int i = 0; i < fichasOrdenadas.size(); i++) {
            if (usadas[i]) {
                continue;
            }
            Ficha base = fichasOrdenadas.get(i);

            // --- ESCALERA ---
            List<Ficha> escalera = new ArrayList<>();
            escalera.add(base);
            for (int j = i + 1; j < fichasOrdenadas.size(); j++) {
                if (usadas[j]) {
                    continue;
                }
                Ficha siguiente = fichasOrdenadas.get(j);
                Ficha ultima = escalera.get(escalera.size() - 1);

                boolean mismaFila = Math.abs(siguiente.getY() - ultima.getY()) <= 5;
                boolean mismoColor = siguiente.getColor().equals(ultima.getColor());
                boolean consecutivo = siguiente.getNumero() == ultima.getNumero() + 1;
                boolean xCorrecto = siguiente.getX() - ultima.getX() == 29;

                if (mismaFila && mismoColor && consecutivo && xCorrecto) {
                    escalera.add(siguiente);
                }
            }

            if (escalera.size() >= 3) {
                for (Ficha f : escalera) {
                    usadas[fichasOrdenadas.indexOf(f)] = true;
                }
                gruposFinales.add(new Grupo("escalera", escalera.size(), new ArrayList<>(escalera)));
                continue;
            }

            // --- MISMO NÚMERO (LÓGICA CORREGIDA) ---
            List<Ficha> candidatos = new ArrayList<>();
            // Agrega la ficha base y busca otras con el mismo número en la misma fila
            candidatos.add(base);
            for (int j = i + 1; j < fichasOrdenadas.size(); j++) {
                if (usadas[j]) continue;

                Ficha potencial = fichasOrdenadas.get(j);
                boolean mismoNumero = potencial.getNumero() == base.getNumero();
                boolean mismaFila = Math.abs(potencial.getY() - base.getY()) <= 5;

                if (mismoNumero && mismaFila) {
                    candidatos.add(potencial);
                }
            }

            // Si hay suficientes candidatos para formar un grupo, búscalos
            if (candidatos.size() >= 3) {
                // Ordena los candidatos por su posición X para verificar adyacencia
                candidatos.sort(Comparator.comparingInt(Ficha::getX));
                
                List<Ficha> grupoActual = new ArrayList<>();

                for (Ficha fichaCandidata : candidatos) {
                    // Si el grupo está vacío o la ficha actual está pegada a la anterior
                    if (grupoActual.isEmpty() || (fichaCandidata.getX() - grupoActual.get(grupoActual.size() - 1).getX() < 44)) { // 44 = ~1.5 * ancho de ficha
                        // Y además no tiene un color repetido
                        if (grupoActual.stream().noneMatch(f -> f.getColor().equals(fichaCandidata.getColor()))) {
                            grupoActual.add(fichaCandidata);
                        }
                    } else {
                        // Si la ficha está muy lejos, el grupo anterior se termina.
                        // Verificamos si el grupo que acabamos de formar es válido
                        if (grupoActual.size() >= 3) {
                            gruposFinales.add(new Grupo("numero", grupoActual.size(), new ArrayList<>(grupoActual)));
                            for (Ficha f : grupoActual) {
                                usadas[fichasOrdenadas.indexOf(f)] = true;
                            }
                        }
                        // Empezamos un nuevo grupo con la ficha actual (que estaba separada)
                        grupoActual.clear();
                        // Comprobamos que no se repita el color antes de añadir
                        if (grupoActual.stream().noneMatch(f -> f.getColor().equals(fichaCandidata.getColor()))){
                             grupoActual.add(fichaCandidata);
                        }
                    }
                }

                // Al final del bucle, revisa el último grupo que se estaba formando
                if (grupoActual.size() >= 3) {
                    gruposFinales.add(new Grupo("numero", grupoActual.size(), new ArrayList<>(grupoActual)));
                    for (Ficha f : grupoActual) {
                        usadas[fichasOrdenadas.indexOf(f)] = true;
                    }
                }
            }
             // Si después de todo, la ficha 'base' no se usó en ningún grupo de "mismo número" válido, trátala como no establecida.
            if (!usadas[i]) {
                gruposFinales.add(new Grupo("No establecido", 1, new ArrayList<>(List.of(base))));
                usadas[i] = true;
            }
        }

        tablero.setFichasEnTablero(gruposFinales);
        System.out.println("Grupos reorganizados correctamente: " + gruposFinales);
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
        for (int i = 0; i < 40; i++) {
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
        System.out.println("Tamaño de mazo: " + mazo.size());

        // Agregarla al primer grupo de la mano (o puedes crear lógica para otro grupo)
        Mano manoJugador = jugador.getManoJugador();
        List<Grupo> gruposMano = manoJugador.getGruposMano();
        if (!gruposMano.isEmpty()) {
            gruposMano.get(0).getFichas().add(ficha);
            manoJugador.setFichasEnMano(manoJugador.getFichasEnMano() + 1);
            System.out.println("Se añadio nueva ficha a la mano.");
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.TOMO_FICHA);
        }
    }

    ////////////////////////////////METODOS FUERTES/////////////////////////////////////////////////////
    private boolean estaCerca(Ficha f, int x, int y) {
        int distancia = 29;
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

    public Tablero copiaTablero(Tablero original) {
        System.out.println("[DEBUG] Iniciando copia profunda del tablero...");
        Tablero copia = new Tablero();
        List<Grupo> gruposCopia = new ArrayList<>();

        for (Grupo g : original.getFichasEnTablero()) {
            System.out.println("[DEBUG] Copiando grupo: " + g.getTipo() + " con " + g.getFichas().size() + " fichas.");
            List<Ficha> fichasCopia = new ArrayList<>();
            for (Ficha f : g.getFichas()) {
                Ficha fCopia = new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin(), f.getX(), f.getY());
                fichasCopia.add(fCopia);
                System.out.println("[DEBUG]   Copiada ficha ID=" + f.getId() + " Num=" + f.getNumero());
            }
            Grupo gCopia = new Grupo(g.getTipo(), fichasCopia.size(), fichasCopia);
            gruposCopia.add(gCopia);
        }

        copia.setFichasEnTablero(gruposCopia);
        copia.setMazo(tablero.getMazo());
        System.out.println("[DEBUG] Copia de tablero finalizada.");
        return copia;
    }

    public void iniciarTurno() {
        System.out.println("[DEBUG] Iniciando turno...");
        tableroAnterior = copiaTablero(tablero); // Esto ya hace una copia profunda, ¡bien!

        // Asegúrate de hacer una copia profunda de las fichas en la mano también
        fichasJugadorAlInicioTurno = new ArrayList<>();
        for (Grupo g : jugador.getManoJugador().getGruposMano()) {
            for (Ficha f : g.getFichas()) {
                // Crea un nuevo objeto Ficha para la copia
                fichasJugadorAlInicioTurno.add(new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()));
            }
        }
        System.out.println("[DEBUG] Se guardaron " + fichasJugadorAlInicioTurno.size() + " fichas del jugador al inicio del turno.");
    }

    public boolean terminarTurno() {
        System.out.println("[DEBUG] Intentando terminar turno...");

        // 1. Validar el estado final del tablero
        boolean tableroValido = true;
        for (Grupo g : tablero.getFichasEnTablero()) {
            // Un grupo es inválido si no está bien formado O tiene menos de 3 fichas.
            if ("No establecido".equals(g.getTipo()) || g.getFichas().size() < 3) {
                tableroValido = false;
                System.out.println("[DEBUG] Movimiento inválido detectado. Grupo: " + g.getTipo() + ", Tamaño: " + g.getFichas().size());
                break; // Si uno es inválido, todo el tablero lo es.
            }
        }

        // 2. Actuar según la validación
        if (tableroValido) {
            // --- CASO DE ÉXITO ---
            System.out.println("[DEBUG] Todos los grupos son válidos. Turno finalizado correctamente.");
            // Se consolida el estado actual como el nuevo "estado anterior" para el próximo turno.
            iniciarTurno();
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
            return true;

        } else {
            // --- CASO DE FALLO (AQUÍ ESTÁ LA CORRECCIÓN) ---
            System.out.println("[DEBUG] No se puede terminar el turno, revirtiendo cambios.");

            // a. Restaurar el tablero a su estado inicial
            tablero = copiaTablero(tableroAnterior);
            System.out.println("[DEBUG] Tablero restaurado.");

            // b. Restaurar la mano del jugador COMPLETAMENTE
            Mano manoJugador = jugador.getManoJugador();
            manoJugador.getGruposMano().clear(); // Limpia la mano actual

            // Vuelve a crear la mano usando la copia guardada
            List<Ficha> manoRestauradaFichas = new ArrayList<>();
            for (Ficha f : fichasJugadorAlInicioTurno) {
                manoRestauradaFichas.add(new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()));
            }

            Grupo grupoManoRestaurado = new Grupo("mano", manoRestauradaFichas.size(), manoRestauradaFichas);
            manoJugador.getGruposMano().add(grupoManoRestaurado);
            manoJugador.setFichasEnMano(manoRestauradaFichas.size());
            System.out.println("[DEBUG] Mano del jugador restaurada.");

            // c. Notificar a la Vista para que se repinte todo
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.DEVOLVER_FICHAS_TABLERO); // Este evento debería forzar el repintado del tablero
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
            return false;
        }
    }

    private void devolverFichasAMano(List<Ficha> fichas) {
        Mano manoJugador = jugador.getManoJugador();
        List<Grupo> gruposMano = manoJugador.getGruposMano();

        for (Ficha ficha : fichas) {
            if (!gruposMano.isEmpty()) {
                gruposMano.get(0).getFichas().add(ficha);
                System.out.println("[DEBUG] Ficha devuelta al primer grupo de la mano: ID=" + ficha.getId());
            } else {
                List<Ficha> nuevasFichas = new ArrayList<>();
                nuevasFichas.add(ficha);
                Grupo nuevoGrupo = new Grupo("mano", nuevasFichas.size(), nuevasFichas);
                gruposMano.add(nuevoGrupo);
                System.out.println("[DEBUG] Nuevo grupo creado en la mano con la ficha: ID=" + ficha.getId());
            }

            manoJugador.setFichasEnMano(manoJugador.getFichasEnMano() + 1);
        }
    }

    private boolean estaEnManoJugador(Ficha ficha) {
        for (Grupo g : jugador.getManoJugador().getGruposMano()) {
            for (Ficha f : g.getFichas()) {
                if (f.getId() == ficha.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
