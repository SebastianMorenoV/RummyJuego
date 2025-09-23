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
import java.util.List;
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
                    ficha.getNumero(),
                    ficha.getColor(),
                    ficha.isComodin()))
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
        Ficha fichaAColocar = fichaDTO.toFicha(x, y);

        // Eliminar la ficha de la mano del jugador
        eliminarFichaDeMano(fichaAColocar);

        // 1. Revisar si la ficha ya está en algún grupo (movimiento dentro del tablero)
        Grupo grupoOrigen = buscarGrupoPorFicha(fichaAColocar.getId());
        if (grupoOrigen != null) {
            // Quitar ficha del grupo actual
            grupoOrigen.getFichas().removeIf(fichaEnGrupo -> fichaEnGrupo.getId() == fichaAColocar.getId());
            grupoOrigen.setNumFichas(grupoOrigen.getFichas().size());

            // Si el grupo quedó inválido, eliminarlo del tablero
            if (!esGrupoValido(grupoOrigen)) {
                tablero.getFichasEnTablero().remove(grupoOrigen);
                System.out.println("Grupo eliminado por ser incompleto tras mover ficha: " + grupoOrigen);

                // Si quieres, aquí podrías crear nuevos grupos a partir de los fragmentos restantes
                for (Ficha f : grupoOrigen.getFichas()) {
                    List<Ficha> lista = new ArrayList<>();
                    lista.add(f);
                    Grupo nuevo = new Grupo("No establecido", 1, lista);
                    tablero.getFichasEnTablero().add(nuevo);
                    System.out.println("Creado nuevo grupo de ficha aislada: " + nuevo);
                }
            }
        }

        // 2. Buscar un grupo cercano donde se pueda agregar
        Grupo grupoCercano = encontrarGrupo(fichaAColocar);
        if (grupoCercano != null && puedeAgregarAFichas(grupoCercano, fichaAColocar)) {
            // Agregar ficha al grupo cercano
            grupoCercano.getFichas().add(fichaAColocar);
            grupoCercano.setNumFichas(grupoCercano.getFichas().size());
            // Actualizar tipo de grupo (puede ser "numero" o "escalera")
            // se hace dentro de puedeAgregarAFichas
            // Limpiar grupos incompletos restantes
            deshacerGruposIncompletos();
            System.out.println("Ficha agregada a grupo existente: " + grupoCercano);
            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
            return;
        }
        // 3. Si no hay grupo cercano válido, crear un nuevo grupo
        List<Ficha> fichasAGrupo = new ArrayList<>();
        fichasAGrupo.add(fichaAColocar);
        Grupo grupoNuevo = new Grupo("No establecido", fichasAGrupo.size(), fichasAGrupo);
        tablero.getFichasEnTablero().add(grupoNuevo);
        System.out.println("Creacion de grupo nuevo correctamente: " + grupoNuevo);
        deshacerGruposIncompletos();
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
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
    private List<Grupo> encontrarGruposColindantes(Ficha nuevaFicha) {
        return tablero.getFichasEnTablero().stream()
                .filter(grupo -> grupo.getFichas().stream()
                .anyMatch(f -> estaCerca(f, nuevaFicha.getX(), nuevaFicha.getY())))
                .sorted(Comparator.comparingInt(grupo -> grupo.getFichas()
                .stream().mapToInt(Ficha::getX).min().orElse(Integer.MAX_VALUE)))
                .toList();
    }
    
    private Grupo encontrarGrupo(Ficha nuevaFicha) {
        return tablero.getFichasEnTablero().stream()
                //encuentra el grupo de la ficha que le pasas como parametro
                .filter(grupo -> grupo.getFichas().stream().anyMatch(f -> estaCerca(f, nuevaFicha.getX(), nuevaFicha.getY())))
                .findFirst()
                .orElse(null);
    }
    
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
    
    private boolean puedeAgregarAFichas(Grupo grupo, Ficha nuevaFicha) {
        List<Ficha> fichas = grupo.getFichas();
        
        if (grupo.getTipo().equals("No establecido")) {
            Ficha primerFichaGrupo = fichas.get(0);
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
            // Máx 13 fichas, misma color
            if (fichas.size() >= 13) {
                return false;
            }
            if (!fichas.get(0).getColor().equals(nuevaFicha.getColor())) {
                return false;
            }

            // Obtener números y posiciones X
            List<Ficha> ordenadas = new ArrayList<>(fichas);
            ordenadas.sort(Comparator.comparingInt(Ficha::getX)); // izquierda a derecha
            int minNum = ordenadas.get(0).getNumero();
            int maxNum = ordenadas.get(ordenadas.size() - 1).getNumero();
            int minX = ordenadas.get(0).getX();
            int maxX = ordenadas.get(ordenadas.size() - 1).getX();

            // Verificar que el nuevo número encaje al inicio o final de la escalera
            if (nuevaFicha.getNumero() == minNum - 1) {
                // Debe ir a la izquierda
                return nuevaFicha.getX() < minX;
            } else if (nuevaFicha.getNumero() == maxNum + 1) {
                // Debe ir a la derecha
                return nuevaFicha.getX() > maxX;
            }
            
            return false; // no encaja en la secuencia
        }
        
        return false;
    }
    
    private void deshacerGruposIncompletos() {
        for (Grupo grupo : tablero.getFichasEnTablero()) {
            if (!esGrupoValido(grupo)) {
                grupo.setTipo("No establecido");
                System.out.println("⚠ Grupo marcado como no establecido por ser incompleto: " + grupo);
            }
        }
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
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
        List<Ficha> fichas = grupo.getFichas();
        int size = fichas.size();
        
        switch (grupo.getTipo()) {
            case "No establecido":
                return size >= 1; // grupo en construcción

            case "numero":
                if (size < 2 || size > 4) {
                    return false;
                }
                
                int numero = fichas.get(0).getNumero();
                Set<Color> colores = new HashSet<>();
                
                for (Ficha f : fichas) {
                    if (f.getNumero() != numero) {
                        return false; // todos el mismo número
                    }
                    if (!colores.add(f.getColor())) {
                        return false; // colores duplicados
                    }
                }

                // Verificar cercanía horizontal
                fichas.sort(Comparator.comparingInt(Ficha::getX));
                for (int i = 0; i < fichas.size() - 1; i++) {
                    int distancia = Math.abs(fichas.get(i + 1).getX() - fichas.get(i).getX());
                    if (distancia > 29) {
                        return false;
                    }
                }
                
                return true;
            
            case "escalera":
                if (size < 2 || size > 13) {
                    return false;
                }
                
                Color color = fichas.get(0).getColor();
                List<Integer> numeros = new ArrayList<>();
                for (Ficha f : fichas) {
                    if (!f.getColor().equals(color)) {
                        return false; // mismo color
                    }
                    numeros.add(f.getNumero());
                }
                Collections.sort(numeros);
                
                for (int i = 0; i < numeros.size() - 1; i++) {
                    if (numeros.get(i + 1) != numeros.get(i) + 1) {
                        return false; // secuencia
                    }
                }

                // Verificar cercanía horizontal
                fichas.sort(Comparator.comparingInt(Ficha::getX));
                for (int i = 0; i < fichas.size() - 1; i++) {
                    int distancia = Math.abs(fichas.get(i + 1).getX() - fichas.get(i).getX());
                    if (distancia > 29) {
                        return false;
                    }
                }
                
                return true;
            
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
        System.out.println("[DEBUG] Copia de tablero finalizada.");
        return copia;
    }
    
    public void iniciarTurno() {
        System.out.println("[DEBUG] Iniciando turno...");
        tableroAnterior = copiaTablero(tablero);
        
        fichasJugadorAlInicioTurno = new ArrayList<>();
        for (Grupo g : jugador.getManoJugador().getGruposMano()) {
            fichasJugadorAlInicioTurno.addAll(g.getFichas());
        }
        System.out.println("[DEBUG] Se guardaron " + fichasJugadorAlInicioTurno.size() + " fichas del jugador al inicio del turno.");
    }
    
    public boolean terminarTurno() {
        System.out.println("[DEBUG] Intentando terminar turno...");

        // Revisar si hay grupos inválidos (estado "No establecido")
        for (Grupo g : tablero.getFichasEnTablero()) {
            if ("No establecido".equals(g.getTipo())) {
                System.out.println("[DEBUG] Grupo inválido encontrado con estado 'No establecido': " + g);

                // Restaurar el tablero al inicio del turno
                tablero = copiaTablero(tableroAnterior);
                System.out.println("[DEBUG] Tablero restaurado al estado inicial del turno.");

                // Devolver solo las fichas del jugador que se movieron al tablero
                List<Ficha> fichasADevolver = fichasJugadorAlInicioTurno.stream()
                        .filter(f -> !estaEnManoJugador(f)) // si no está en la mano, fue movida
                        .collect(Collectors.toList());
                
                devolverFichasAMano(fichasADevolver);
                System.out.println("[DEBUG] Fichas del jugador devueltas a la mano: " + fichasADevolver.size());
                notificarObservadores(TipoEvento.REPINTAR_MANO);
                notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
                System.out.println("[DEBUG] No se puede terminar el turno: hay grupos con estado 'No establecido'.");
                return false;
            }
        }
        
        System.out.println("[DEBUG] Todos los grupos son válidos. Turno finalizado correctamente.");

        // Actualizar tableroAnterior y fichasJugadorAlInicioTurno para el siguiente turno
        tableroAnterior = copiaTablero(tablero);

        // Guardar el estado de la mano del jugador para el próximo turno
        fichasJugadorAlInicioTurno = new ArrayList<>();
        for (Grupo g : jugador.getManoJugador().getGruposMano()) {
            fichasJugadorAlInicioTurno.addAll(g.getFichas());
        }
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
        return true;
        
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
