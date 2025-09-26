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
import java.util.List;
import java.util.Random;
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
                    ficha.isComodin()
            ))
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
        List<Ficha> fichasEnMano = jugador.getManoJugador().getFichasEnMano();
        List<FichaJuegoDTO> fichasJuegoDTO = new ArrayList<>();
        for (Ficha ficha : fichasEnMano) {
            fichasJuegoDTO.add(new FichaJuegoDTO(ficha.getId(), ficha.getNumero(), ficha.getColor(), ficha.isComodin()));
        }
        return fichasJuegoDTO;
    }

    public void iniciarJuego() {
        //Incializar mano en vista.
        //crearGruposMano();
        this.jugador = new Jugador("Sebas", "B1", new Mano());
        tablero.crearMazoCompleto();
        repartirMano(jugador);
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    // En la clase Modelo.java
    public void actualizarGruposEnTablero(List<GrupoDTO> gruposPropuestos) {
        // 1. Crear una nueva lista de grupos para el tablero interno del modelo.
        List<Grupo> nuevosGruposDelTablero = new ArrayList<>();

        for (GrupoDTO grupoDTO : gruposPropuestos) {
            // 2. Convertir las fichas del DTO a la entidad Ficha del modelo.
            List<Ficha> fichasDelGrupo = grupoDTO.getFichasGrupo().stream()
                    .map(fichaDTO -> new Ficha(fichaDTO.getIdFicha(), fichaDTO.getNumeroFicha(), fichaDTO.getColor(), fichaDTO.isComodin()))
                    .collect(Collectors.toList());

            // 3. Validar el grupo y determinar su tipo.
            String tipoGrupoValidado = "Invalido"; // Por defecto, el grupo es inválido.

            // Un grupo debe tener al menos 3 fichas para ser válido.
            if (fichasDelGrupo.size() >= 3) {
                if (esEscaleraValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "escalera";
                } else if (esTerciaValida(fichasDelGrupo)) {
                    tipoGrupoValidado = "tercia"; // O "cuarta", etc. Usaremos "tercia" como genérico.
                }
            }

            // 4. Crear el nuevo grupo con el tipo validado y añadirlo a nuestra lista.
            Grupo grupoValidado = new Grupo(tipoGrupoValidado, fichasDelGrupo.size(), fichasDelGrupo);
            nuevosGruposDelTablero.add(grupoValidado);
            System.out.println("Grupo validado: " +grupoValidado);
        }

        // 5. Reemplazar la lista de grupos antigua del tablero con la nueva, ya validada.
        tablero.setFichasEnTablero(nuevosGruposDelTablero);

        // 6. Notificar a la vista que el tablero se ha actualizado para que se repinte.
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO);
    }

    /**
     * Verifica si una lista de fichas forma una escalera válida. Regla: 3 o más
     * fichas del mismo color con números consecutivos.
     *
     * @param fichas La lista de fichas a validar.
     * @return true si es una escalera válida, false en caso contrario.
     */
    private boolean esEscaleraValida(List<Ficha> fichas) {
        if (fichas == null || fichas.size() < 3) {
            return false;
        }

        // Ordenar las fichas por número para facilitar la validación.
        fichas.sort(Comparator.comparingInt(Ficha::getNumero));

        // Comprobar que todas las fichas sean del mismo color.
        Color primerColor = fichas.get(0).getColor();
        for (int i = 1; i < fichas.size(); i++) {
            if (!fichas.get(i).getColor().equals(primerColor)) {
                return false; // Colores diferentes, no es escalera.
            }
        }

        // Comprobar que los números sean consecutivos.
        for (int i = 0; i < fichas.size() - 1; i++) {
            if (fichas.get(i + 1).getNumero() != fichas.get(i).getNumero() + 1) {
                return false; // Números no consecutivos.
            }
        }

        return true; // Si pasó todas las pruebas, es una escalera válida.
    }

    /**
     * Verifica si una lista de fichas forma una tercia (o cuarta) válida.
     * Regla: 3 o 4 fichas del mismo número pero de colores diferentes.
     *
     * @param fichas La lista de fichas a validar.
     * @return true si es una tercia válida, false en caso contrario.
     */
    private boolean esTerciaValida(List<Ficha> fichas) {
        if (fichas == null || fichas.size() < 3 || fichas.size() > 4) {
            return false; // Debe tener 3 o 4 fichas.
        }

        // Comprobar que todas tengan el mismo número.
        int primerNumero = fichas.get(0).getNumero();
        for (int i = 1; i < fichas.size(); i++) {
            if (fichas.get(i).getNumero() != primerNumero) {
                return false; // Números diferentes, no es tercia.
            }
        }

        // Comprobar que no haya colores repetidos.
        List<Color> colores = fichas.stream().map(Ficha::getColor).collect(Collectors.toList());
        long coloresUnicos = colores.stream().distinct().count();
        if (coloresUnicos != fichas.size()) {
            return false; // Hay colores repetidos.
        }

        return true; // Si pasó todas las pruebas, es una tercia válida.
    }

    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : observadores) {
            observer.actualiza(this, tipoEvento);
        }
    }

    public void agregarObservador(Observador obs) {
        observadores.add(obs);
    }

//    public void colocarFicha(FichaJuegoDTO fichaDTO, int x, int y) {
//        // 1. Crear la ficha a colocar
//        Ficha fichaAColocar = fichaDTO.toFicha(x, y);
//
//        // 2. Eliminar la ficha de la mano
//        eliminarFichaDeMano(fichaAColocar);
//
//        // 3. Quitar la ficha de cualquier grupo donde ya esté
//        for (Grupo g : tablero.getFichasEnTablero()) {
//            g.getFichas().removeIf(f -> f.getId() == fichaAColocar.getId());
//        }
//
//        // 4. Agregar la ficha directamente a la lista de fichas del tablero
//        List<Ficha> todasFichas = new ArrayList<>();
//        for (Grupo g : tablero.getFichasEnTablero()) {
//            todasFichas.addAll(g.getFichas());
//        }
//        todasFichas.add(fichaAColocar);
//
//        // 5. Limpiar grupos actuales y reorganizar con TODAS las fichas
//        tablero.getFichasEnTablero().clear();
//        reorganizarGruposTablero(todasFichas);
//
//        // 6. Notificar a los observadores
//        notificarObservadores(TipoEvento.ACTUALIZAR_JUGADA);
//
//        System.out.println("Ficha colocada y grupos reorganizados correctamente: " + fichaAColocar);
//    }
//
//    public void reorganizarGruposTablero(List<Ficha> todasFichas) {
//        List<Ficha> fichasOrdenadas = new ArrayList<>(todasFichas);
//        fichasOrdenadas.sort(Comparator.comparingInt(Ficha::getY).thenComparingInt(Ficha::getX));
//
//        List<Grupo> gruposFinales = new ArrayList<>();
//        boolean[] usadas = new boolean[fichasOrdenadas.size()];
//
//        for (int i = 0; i < fichasOrdenadas.size(); i++) {
//            if (usadas[i]) {
//                continue;
//            }
//            Ficha base = fichasOrdenadas.get(i);
//
//            // --- ESCALERA ---
//            List<Ficha> escalera = new ArrayList<>();
//            escalera.add(base);
//            for (int j = i + 1; j < fichasOrdenadas.size(); j++) {
//                if (usadas[j]) {
//                    continue;
//                }
//                Ficha siguiente = fichasOrdenadas.get(j);
//                Ficha ultima = escalera.get(escalera.size() - 1);
//
//                boolean mismaFila = Math.abs(siguiente.getY() - ultima.getY()) <= 5;
//                boolean mismoColor = siguiente.getColor().equals(ultima.getColor());
//                boolean consecutivo = siguiente.getNumero() == ultima.getNumero() + 1;
//                boolean xCorrecto = siguiente.getX() - ultima.getX() == 29;
//
//                if (mismaFila && mismoColor && consecutivo && xCorrecto) {
//                    escalera.add(siguiente);
//                }
//            }
//
//            if (escalera.size() >= 3) {
//                for (Ficha f : escalera) {
//                    usadas[fichasOrdenadas.indexOf(f)] = true;
//                }
//                gruposFinales.add(new Grupo("escalera", escalera.size(), new ArrayList<>(escalera)));
//                continue;
//            }
//
//            // --- MISMO NÚMERO (LÓGICA CORREGIDA) ---
//            List<Ficha> candidatos = new ArrayList<>();
//            // Agrega la ficha base y busca otras con el mismo número en la misma fila
//            candidatos.add(base);
//            for (int j = i + 1; j < fichasOrdenadas.size(); j++) {
//                if (usadas[j]) {
//                    continue;
//                }
//
//                Ficha potencial = fichasOrdenadas.get(j);
//                boolean mismoNumero = potencial.getNumero() == base.getNumero();
//                boolean mismaFila = Math.abs(potencial.getY() - base.getY()) <= 5;
//
//                if (mismoNumero && mismaFila) {
//                    candidatos.add(potencial);
//                }
//            }
//
//            // Si hay suficientes candidatos para formar un grupo, búscalos
//            if (candidatos.size() >= 3) {
//                // Ordena los candidatos por su posición X para verificar adyacencia
//                candidatos.sort(Comparator.comparingInt(Ficha::getX));
//
//                List<Ficha> grupoActual = new ArrayList<>();
//
//                for (Ficha fichaCandidata : candidatos) {
//                    // Si el grupo está vacío o la ficha actual está pegada a la anterior
//                    if (grupoActual.isEmpty() || (fichaCandidata.getX() - grupoActual.get(grupoActual.size() - 1).getX() < 44)) { // 44 = ~1.5 * ancho de ficha
//                        // Y además no tiene un color repetido
//                        if (grupoActual.stream().noneMatch(f -> f.getColor().equals(fichaCandidata.getColor()))) {
//                            grupoActual.add(fichaCandidata);
//                        }
//                    } else {
//                        // Si la ficha está muy lejos, el grupo anterior se termina.
//                        // Verificamos si el grupo que acabamos de formar es válido
//                        if (grupoActual.size() >= 3) {
//                            gruposFinales.add(new Grupo("numero", grupoActual.size(), new ArrayList<>(grupoActual)));
//                            for (Ficha f : grupoActual) {
//                                usadas[fichasOrdenadas.indexOf(f)] = true;
//                            }
//                        }
//                        // Empezamos un nuevo grupo con la ficha actual (que estaba separada)
//                        grupoActual.clear();
//                        // Comprobamos que no se repita el color antes de añadir
//                        if (grupoActual.stream().noneMatch(f -> f.getColor().equals(fichaCandidata.getColor()))) {
//                            grupoActual.add(fichaCandidata);
//                        }
//                    }
//                }
//
//                // Al final del bucle, revisa el último grupo que se estaba formando
//                if (grupoActual.size() >= 3) {
//                    gruposFinales.add(new Grupo("numero", grupoActual.size(), new ArrayList<>(grupoActual)));
//                    for (Ficha f : grupoActual) {
//                        usadas[fichasOrdenadas.indexOf(f)] = true;
//                    }
//                }
//            }
//            // Si después de todo, la ficha 'base' no se usó en ningún grupo de "mismo número" válido, trátala como no establecida.
//            if (!usadas[i]) {
//                gruposFinales.add(new Grupo("No establecido", 1, new ArrayList<>(List.of(base))));
//                usadas[i] = true;
//            }
//        }
//
//        tablero.setFichasEnTablero(gruposFinales);
//        System.out.println("Grupos reorganizados correctamente: " + gruposFinales);
//    }
    /**
     * Metodo para repartir la mano de un solo jugador en la vista.
     *
     * @param jugador
     */
    public void repartirMano(Jugador jugador) {
        List<Ficha> mazo = tablero.getMazo();
        List<Ficha> fichasMano = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            fichasMano.add(mazo.remove(0)); // quita del mazo
        }

        Mano mano = jugador.getManoJugador();
        mano.setFichasEnMano(fichasMano);
        mano.setCantidadFichasEnMano(fichasMano.size());
    }

    /**
     * Metodo para tomar una ficha del mazo y notificar a la vista.
     */
    public void tomarFichaMazo() {
        Ficha fichaTomada = tablero.tomarFichaMazo();
        boolean tomada = jugador.agregarFichaAJugador(fichaTomada);

        if (tomada) {
            notificarObservadores(TipoEvento.REPINTAR_MANO);
            notificarObservadores(TipoEvento.TOMO_FICHA);
        }

    }

////////////////////////////////METODOS FUERTES/////////////////////////////////////////////////////
//    private void eliminarFichaDeMano(Ficha ficha) {
//        for (Grupo grupo : jugador.getManoJugador().getGruposMano()) {
//            boolean removida = grupo.getFichas().removeIf(f -> f.getId() == ficha.getId());
//            if (removida) {
//                grupo.setNumFichas(grupo.getFichas().size());
//                System.out.println("Ficha eliminada de la mano: " + ficha);
//                break; // ya se eliminó, no seguir buscando
//            }
//        }
//    }
//
//    public Tablero copiaTablero(Tablero original) {
//        System.out.println("[DEBUG] Iniciando copia profunda del tablero...");
//        Tablero copia = new Tablero();
//        List<Grupo> gruposCopia = new ArrayList<>();
//
//        for (Grupo g : original.getFichasEnTablero()) {
//            System.out.println("[DEBUG] Copiando grupo: " + g.getTipo() + " con " + g.getFichas().size() + " fichas.");
//            List<Ficha> fichasCopia = new ArrayList<>();
//            for (Ficha f : g.getFichas()) {
//                Ficha fCopia = new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin(), f.getX(), f.getY());
//                fichasCopia.add(fCopia);
//                System.out.println("[DEBUG]   Copiada ficha ID=" + f.getId() + " Num=" + f.getNumero());
//            }
//            Grupo gCopia = new Grupo(g.getTipo(), fichasCopia.size(), fichasCopia);
//            gruposCopia.add(gCopia);
//        }
//
//        copia.setFichasEnTablero(gruposCopia);
//        copia.setMazo(tablero.getMazo());
//        System.out.println("[DEBUG] Copia de tablero finalizada.");
//        return copia;
//    }
//
//    public void iniciarTurno() {
//        System.out.println("[DEBUG] Iniciando turno...");
//        tableroAnterior = copiaTablero(tablero); // Esto ya hace una copia profunda, ¡bien!
//
//        // Asegúrate de hacer una copia profunda de las fichas en la mano también
//        fichasJugadorAlInicioTurno = new ArrayList<>();
//        for (Grupo g : jugador.getManoJugador().getGruposMano()) {
//            for (Ficha f : g.getFichas()) {
//                // Crea un nuevo objeto Ficha para la copia
//                fichasJugadorAlInicioTurno.add(new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()));
//            }
//        }
//        System.out.println("[DEBUG] Se guardaron " + fichasJugadorAlInicioTurno.size() + " fichas del jugador al inicio del turno.");
//    }
//
//    public boolean terminarTurno() {
//        System.out.println("[DEBUG] Intentando terminar turno...");
//
//        // 1. Validar el estado final del tablero
//        boolean tableroValido = true;
//        for (Grupo g : tablero.getFichasEnTablero()) {
//            // Un grupo es inválido si no está bien formado O tiene menos de 3 fichas.
//            if ("No establecido".equals(g.getTipo()) || g.getFichas().size() < 3) {
//                tableroValido = false;
//                System.out.println("[DEBUG] Movimiento inválido detectado. Grupo: " + g.getTipo() + ", Tamaño: " + g.getFichas().size());
//                break; // Si uno es inválido, todo el tablero lo es.
//            }
//        }
//
//        // 2. Actuar según la validación
//        if (tableroValido) {
//            // --- CASO DE ÉXITO ---
//            System.out.println("[DEBUG] Todos los grupos son válidos. Turno finalizado correctamente.");
//            // Se consolida el estado actual como el nuevo "estado anterior" para el próximo turno.
//            iniciarTurno();
//            notificarObservadores(TipoEvento.ACTUALIZAR_JUGADA);
//            return true;
//
//        } else {
//            // --- CASO DE FALLO (AQUÍ ESTÁ LA CORRECCIÓN) ---
//            System.out.println("[DEBUG] No se puede terminar el turno, revirtiendo cambios.");
//
//            // a. Restaurar el tablero a su estado inicial
//            tablero = copiaTablero(tableroAnterior);
//            System.out.println("[DEBUG] Tablero restaurado.");
//
//            // b. Restaurar la mano del jugador COMPLETAMENTE
//            Mano manoJugador = jugador.getManoJugador();
//            manoJugador.getGruposMano().clear(); // Limpia la mano actual
//
//            // Vuelve a crear la mano usando la copia guardada
//            List<Ficha> manoRestauradaFichas = new ArrayList<>();
//            for (Ficha f : fichasJugadorAlInicioTurno) {
//                manoRestauradaFichas.add(new Ficha(f.getId(), f.getNumero(), f.getColor(), f.isComodin()));
//            }
//
//            Grupo grupoManoRestaurado = new Grupo("mano", manoRestauradaFichas.size(), manoRestauradaFichas);
//            manoJugador.getGruposMano().add(grupoManoRestaurado);
//            manoJugador.setFichasEnMano(manoRestauradaFichas.size());
//            System.out.println("[DEBUG] Mano del jugador restaurada.");
//
//            // c. Notificar a la Vista para que se repinte todo
//            notificarObservadores(TipoEvento.REPINTAR_MANO);
//            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO); // Este evento debería forzar el repintado del tablero
//            notificarObservadores(TipoEvento.ACTUALIZAR_JUGADA);
//            return false;
//        }
//    }
}
