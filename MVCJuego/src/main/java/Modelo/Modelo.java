package Modelo;

import DTO.FichaJuegoDTO;
import java.awt.Color;
import DTO.GrupoDTO;
import DTO.JuegoDTO;
import DTO.JugadorDTO;
import Dtos.ActualizacionDTO;
import Entidades.Ficha;
import Entidades.Grupo;
import Fachada.IJuegoRummy;
import Fachada.JuegoRummyFachada;
import Vista.Observador;
import Vista.TipoEvento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Clase Modelo en el patrón MVC. Gestiona el estado local del juego, interactúa
 * con la Fachada de lógica de juego (juego) y maneja la comunicación de red
 * mediante el despachador.
 *
 * @author benja
 */
public class Modelo implements IModelo, PropertyChangeListener {

    private List<Observador> observadores;
    private final IJuegoRummy juego;
    private List<GrupoDTO> gruposDeTurnoDTO;
    private List<GrupoDTO> gruposDTOAlInicioDelTurno;
    private Map<String, Integer> conteoFichasRivales = new HashMap<>();
    private String idJugadorEnTurnoGlobal = "";
    private boolean esMiTurno;
    private iDespachador despachador;

    String ipServidor;
    int puertoServidor;
    String ipCliente;
    int puertoCliente;
    private List<String> nombresJugadores = new ArrayList<>();
    private int mazoFichasRestantes = 0;
    private String miId;
    private Map<String, JugadorDTO> perfilesJugadores = new HashMap<>();
    private boolean coloresCargados = false;
    private String idCliente;

    //variables adicionales de CU registrar jugador//
    private String miNickname = "Cargando...";
    private String miAvatar = "avatar1";
    private int[] misColores = {0, 0, 0, 0};

    private static final int SERVER_NEGRO = -16777216;
    private static final int SERVER_ROJO = -65536;
    private static final int SERVER_AZUL = -16776961;
    private static final int SERVER_AMARILLO = -14336;

    public Modelo() {
        this.observadores = new ArrayList<>();
        this.juego = new JuegoRummyFachada();
        this.gruposDeTurnoDTO = new ArrayList<>();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>();
        this.esMiTurno = false;
    }

    /**
     * Inicia el juego localmente y notifica a la vista para generar fichas
     * iniciales.
     */
    public void iniciarJuego() {
        juego.iniciarPartida();
        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
        notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
    }

    public void abrirCU() {
        iniciarJuego();

        notificarObservadores(TipoEvento.MOSTRAR_JUEGO);
    }

    /**
     * Maneja eventos recibidos desde red (servidor) usando observer pattern.
     * Actualiza el modelo según el tipo de evento.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        String payloadd = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        switch (evento) {

            case "COMANDO_INICIAR_PARTIDA":
                System.out.println("[Modelo] Recibida orden del servidor para iniciar la partida.");
                enviarComandoIniciarPartida();
                break;

            case "MANO_INICIAL":
                System.out.println("[Modelo] Evento 'MANO_INICIAL' detectado!");
                System.out.println("PAYLOAD: " + payloadd);
                try {
                    String[] payloadPartes = payloadd.split("\\$");
                    String manoPayload = payloadPartes[0];

                    if (payloadPartes.length >= 3) {
                        StringBuilder listaJugadoresBuilder = new StringBuilder();
                        for (int i = 2; i < payloadPartes.length; i++) {
                            listaJugadoresBuilder.append(payloadPartes[i]);
                            if (i < payloadPartes.length - 1) {
                                listaJugadoresBuilder.append("$");
                            }
                        }
                        String listaJugadoresStr = listaJugadoresBuilder.toString();
                        System.out.println("[Modelo Debug] Lista de jugadores completa reconstruida: " + listaJugadoresStr);

                        String[] usuariosRaw = listaJugadoresStr.split(";");
                        System.out.println("[Modelo Debug] Número de jugadores después de split por ';': " + usuariosRaw.length);

                        this.nombresJugadores.clear();
                        this.perfilesJugadores.clear();

                        for (String usuario : usuariosRaw) {
                            if (usuario == null || usuario.trim().isEmpty()) {
                                continue;
                            }

                            System.out.println("[Modelo Debug] Procesando cadena de usuario: " + usuario);

                            String[] partes = usuario.split(",", 2);

                            if (partes.length >= 2) {
                                String idReal = partes[0];
                                String datosPerfil = partes[1]; // "Nick$Avatar$..."

                                String[] perfilSplit = datosPerfil.split("\\$");
                                System.out.println("[Modelo Debug] Nickname: " + perfilSplit[0] + ", Avatar ID Raw: " + (perfilSplit.length > 1 ? perfilSplit[1] : "N/A"));

                                // Creamos un DTO temporal para guardar estos datos estáticos
                                JugadorDTO dtoPerfil = new JugadorDTO();
                                dtoPerfil.setNombre(perfilSplit[0]);

                                try {
                                    if (perfilSplit.length > 1) {
                                        dtoPerfil.setIdAvatar(Integer.parseInt(perfilSplit[1]));
                                    }
                                } catch (Exception e) {
                                    dtoPerfil.setIdAvatar(1);
                                }

                                // 3. OBTENER COLORES (C1,C2,C3,C4) para todos los jugadores
                                if (perfilSplit.length > 2) {
                                    String[] colorStrings = perfilSplit[2].split(",");
                                    int[] colores = new int[4];
                                    for (int i = 0; i < 4 && i < colorStrings.length; i++) {
                                        colores[i] = Integer.parseInt(colorStrings[i]);
                                    }
                                    dtoPerfil.setColores(colores);

                                    // DEBUG: Muestra si los colores se procesaron.
                                    System.out.println("[Modelo Debug] Colores cargados.");
                                }

                                this.perfilesJugadores.put(idReal, dtoPerfil);
                                this.nombresJugadores.add(idReal);
                            } else {
                                System.err.println("[Modelo Debug] ERROR: Cadena de usuario mal formada (sin ',') para: " + usuario);
                            }
                        }
                        System.out.println("[Modelo] Jugadores en la partida: " + this.nombresJugadores.toString());

                        JugadorDTO miPerfil = this.perfilesJugadores.get(this.miId);
                        if (miPerfil != null && miPerfil.getColores() != null) {
                            this.misColores = miPerfil.getColores();
                            this.coloresCargados = true;
                            System.out.println("[Modelo Fix] Colores de mi jugador cargados desde MANO_INICIAL payload.");
                        }
                    }
                    List<FichaJuegoDTO> fichasDTO = deserializarMano(manoPayload);
                    List<Ficha> manoEntidad = fichasDTO.stream()
                            .map(this::convertirFichaDtoAEntidad)
                            .collect(Collectors.toList());

                    juego.setManoInicial(manoEntidad);

                    // Notificar a la vista
                    notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);
                    notificarObservadores(TipoEvento.TOMO_FICHA);
                    notificarObservadores(TipoEvento.REPINTAR_MANO);
                    notificarObservadores(TipoEvento.MOSTRAR_JUEGO);
                } catch (Exception e) {
                    System.err.println("[Modelo] Error al procesar MANO_INICIAL: " + e.getMessage());
                    e.printStackTrace();
                }
                break;

            case "FICHA_RECIBIDA":
                System.out.println("[Modelo] Evento 'FICHA_RECIBIDA' detectado!");
                try {
                    FichaJuegoDTO fichaDTO = FichaJuegoDTO.deserializar(payloadd);
                    if (fichaDTO != null) {
                        Ficha fichaEntidad = convertirFichaDtoAEntidad(fichaDTO);
                        juego.getJugadorActual().agregarFichaAJugador(fichaEntidad);

                        this.mazoFichasRestantes--;

                        notificarObservadores(TipoEvento.REPINTAR_MANO);
                        notificarObservadores(TipoEvento.TOMO_FICHA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "TURNO_CAMBIADO":
                System.out.println("[Modelo] Evento 'TURNO_CAMBIADO' detectado! Payload: " + payloadd);
                String[] partesTurno = payloadd.split(":");
                this.idJugadorEnTurnoGlobal = partesTurno[0];
                String nuevoJugadorId = partesTurno[0];
                this.esMiTurno = nuevoJugadorId.equals(this.miId);

                if (partesTurno.length > 1) {
                    try {
                        this.mazoFichasRestantes = Integer.parseInt(partesTurno[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("[Modelo] Error al parsear conteo del mazo en TURNO_CAMBIADO");
                    }
                }

                if (partesTurno.length > 2) {
                    String dataContadores = partesTurno[2];
                    String[] pares = dataContadores.split(";");
                    for (String par : pares) {
                        String[] kv = par.split("=");
                        if (kv.length == 2) {
                            conteoFichasRivales.put(kv[0], Integer.parseInt(kv[1]));
                        }
                    }
                }

                notificarObservadores(TipoEvento.CAMBIO_DE_TURNO);
                notificarObservadores(TipoEvento.TOMO_FICHA);
                break;

            case "MOVIMIENTO_RECIBIDO":
                System.out.println("[Modelo] Evento 'MOVIMIENTO_RECIBIDO' (Temporal) detectado!");
                try {

                    String[] partes = payloadd.split(":", 2);
                    String idJugadorMovimiento = partes[0];
                    String payloadGrupos = (partes.length > 1) ? partes[1] : "";

                    List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payloadd);
                    if (gruposMovidos != null) {
                        this.actualizarVistaRival(gruposMovidos, idJugadorMovimiento);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "ESTADO_FINAL_TABLERO":
                System.out.println("[Modelo] Evento 'ESTADO_FINAL_TABLERO' detectado!");
                try {
                    List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payloadd);
                    if (gruposMovidos != null) {
                        this.actualizarVistaTemporal(gruposMovidos);
                        juego.guardarEstadoTurno();
                        this.gruposDTOAlInicioDelTurno = new ArrayList<>(this.gruposDeTurnoDTO);
                        notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //nuevo metodo para CU Registrar Jugador
            case "CONFIRMACION_REGISTRO":
                System.out.println("[Modelo] Datos de perfil recibidos del servidor.");
                try {
                    // El servidor envía: ID_FINAL:AVATAR_ID:C1:C2:C3:C4.

                    // payloadd es la parte después de "CONFIRMACION_REGISTRO:"
                    String[] partes = payloadd.split(":");

                    if (partes.length >= 3) {
                        String nuevoIdCliente = partes[0];
                        if (!nuevoIdCliente.equals(this.miId)) {
                            System.out.println("[Modelo] Actualizando mi ID de: " + this.miId + " a: " + nuevoIdCliente);
                            this.miId = nuevoIdCliente;
                        }

                        // 2. Cargar datos de perfil (Avatar y Colores)
                        this.miAvatar = partes[1];

                        if (partes.length > 2) {
                            String coloresStr = partes[2];
                            String[] colorStrings = coloresStr.split(",");

                            if (colorStrings.length >= 4) {
                                this.misColores[0] = Integer.parseInt(colorStrings[0]);
                                this.misColores[1] = Integer.parseInt(colorStrings[1]);
                                this.misColores[2] = Integer.parseInt(colorStrings[2]);
                                this.misColores[3] = Integer.parseInt(colorStrings[3]);

                                System.out.println("[Modelo Debug] Colores personalizados cargados: "
                                        + this.misColores[0] + ", "
                                        + this.misColores[1] + ", "
                                        + this.misColores[2] + ", "
                                        + this.misColores[3]);
                            }

                        }

                        this.coloresCargados = true;

                        System.out.println("[Modelo] Identidad y colores personalizados cargados y activados.");
                    } else {
                        System.err.println("[Modelo] Error de formato en CONFIRMACION_REGISTRO. Partes insuficientes: " + partes.length);
                    }

                    notificarObservadores(TipoEvento.REPINTAR_MANO);
                } catch (Exception e) {
                    System.err.println("Error procesando confirmacion registro: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("[Modelo MVC] Evento PropertyChange desconocido: " + evento);
        }
    }

    /**
     * Refresca estado del tablero temporalmente antes de validar jugada.
     */
    private void actualizarVistaTemporal(List<GrupoDTO> gruposPropuestos) {
        List<Grupo> nuevosGrupos = new ArrayList<>();

        for (GrupoDTO dto : gruposPropuestos) {

            List<Ficha> fichasEntity = new ArrayList<>();
            for (FichaJuegoDTO fichaDto : dto.getFichasGrupo()) {

                // 1. Convertir a Entity. Aquí se aplica el color personalizado (MIS colores)
                // Usamos this.miId por defecto.
                Ficha fichaEntity = convertirFichaDtoAEntidad(fichaDto, this.miId, true);
                fichasEntity.add(fichaEntity);

                // 2. Actualizar el DTO con el nuevo color para la vista.
                fichaDto.setColor(fichaEntity.getColor());
            }

            // 3. Crear el Grupo Entity para pasarlo a la lógica del juego (Facade)
            Grupo grupo = new Grupo(dto.getTipo(), fichasEntity.size(), fichasEntity);
            if (!dto.isEsTemporal()) {
                grupo.setValidado();
            }
            nuevosGrupos.add(grupo);
        }

        juego.colocarFichasEnTablero(nuevosGrupos);

        // 4. Almacenar la lista de DTOs original, ahora con los colores actualizados.
        this.gruposDeTurnoDTO = gruposPropuestos;

        // [FIN DE MODIFICACIÓN]
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

    /**
     * Refresca estado del tablero con el movimiento de un rival. Utiliza los
     * colores personalizados del jugador que realizó el movimiento.
     */
    private void actualizarVistaRival(List<GrupoDTO> gruposPropuestos, String idJugadorMovimiento) {

        // [INICIO DE MODIFICACIÓN: Conversión Entity/DTO con actualización de color local]
        List<Grupo> nuevosGrupos = new ArrayList<>();

        for (GrupoDTO dto : gruposPropuestos) {

            List<Ficha> fichasEntity = new ArrayList<>();
            for (FichaJuegoDTO fichaDto : dto.getFichasGrupo()) {

                // 1. Convertir a Entity. Aquí se aplica el color personalizado (MIS colores)
                // Usamos el ID del jugador que movió, pero la lógica de conversión usará this.misColores.
                Ficha fichaEntity = convertirFichaDtoAEntidad(fichaDto, idJugadorMovimiento, false);
                fichasEntity.add(fichaEntity);

                // 2. Actualizar el DTO con el nuevo color para la vista.
                fichaDto.setColor(fichaEntity.getColor());
            }

            // 3. Crear el Grupo Entity para pasarlo a la lógica del juego (Facade)
            Grupo grupo = new Grupo(dto.getTipo(), fichasEntity.size(), fichasEntity);
            if (!dto.isEsTemporal()) {
                grupo.setValidado();
            }
            nuevosGrupos.add(grupo);
        }

        juego.colocarFichasEnTablero(nuevosGrupos);

        // 4. Almacenar la lista de DTOs original, ahora con los colores actualizados.
        this.gruposDeTurnoDTO = gruposPropuestos;

        // [FIN DE MODIFICACIÓN]
        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

    /**
     * Convierte DTO de grupo a entidad Grupo, usando los colores del jugador
     * especificado.
     */
    private Grupo convertirGrupoDtoAEntidadRival(GrupoDTO dto, String idJugador) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                // Pasar el ID del jugador a la función de conversión de ficha
                .map(fichaDto -> convertirFichaDtoAEntidad(fichaDto, idJugador, false))
                .collect(Collectors.toList());

        Grupo grupo = new Grupo(dto.getTipo(), fichas.size(), fichas);
        if (!dto.isEsTemporal()) {
            grupo.setValidado();
        }
        return grupo;
    }

    /**
     * Envía movimiento al servidor y actualiza tablero local.
     *
     * @param grupos
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'colocarFicha' ignorada. No es mi turno.");
            return;
        }
        // 1. Actualiza el estado local (gruposDeTurnoDTO ahora tiene colores CUSTOM)
        this.actualizarVistaTemporal(grupos);

        // 2. Serializa el estado con los colores originales del servidor.
        String payloadCompleto = serializarJuegoFinal();

        String mensaje = this.miId + ":MOVER:" + payloadCompleto;

        try {
            this.despachador.enviar(ipServidor, puertoServidor, mensaje);

        } catch (IOException ex) {
            Logger.getLogger(Modelo.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reversa jugada si no fue válida y solicita ficha al servidor.
     */
    public void tomarFichaMazo() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'tomarFichaMazo' ignorada. No es mi turno.");
            return;
        }

        juego.revertirCambiosDelTurno();
        notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR_TOMO_FICHA);

        try {
            String payloadJuegoRevertido = serializarEstadoRevertido();
            String mensajeTomar = this.miId + ":TOMAR_FICHA:" + payloadJuegoRevertido;
            this.despachador.enviar(ipServidor, puertoServidor, mensajeTomar);

        } catch (IOException ex) {
            Logger.getLogger(Modelo.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Finaliza turno: valida jugada, envía estado al servidor y notifica a la
     * vista.
     */
    public void terminarTurno() {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'terminarTurno' ignorada. No es mi turno.");
            notificarObservadores(TipoEvento.NO_ES_MI_TURNO);
            return;
        }

        boolean jugadaFueValida = juego.validarYFinalizarTurno();

        if (jugadaFueValida) {

            notificarObservadores(TipoEvento.JUGADA_VALIDA_FINALIZADA);
            try {
                String payloadJuego = serializarJuegoFinal();
                int misFichas = juego.getJugadorActual().getManoJugador().getFichasEnMano().size();
                String mensaje = this.miId + ":FINALIZAR_TURNO:" + payloadJuego + "#" + misFichas;
                this.despachador.enviar(ipServidor, puertoServidor, mensaje);

            } catch (IOException ex) {
                Logger.getLogger(Modelo.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            if (!gruposDTOAlInicioDelTurno.equals(gruposDeTurnoDTO)) {

                System.out.println("Jugada invalida");
                System.out.println(gruposDTOAlInicioDelTurno.toString());
                System.out.println(gruposDeTurnoDTO.toString());

                this.gruposDeTurnoDTO = new ArrayList<>(this.gruposDTOAlInicioDelTurno);

                notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
                try {
                    String payloadJuegoRevertido = serializarEstadoRevertido();
                    String mensaje = this.miId + ":MOVER:" + payloadJuegoRevertido;
                    this.despachador.enviar(ipServidor, puertoServidor, mensaje);

                } catch (IOException ex) {
                    Logger.getLogger(Modelo.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                notificarObservadores(TipoEvento.TOMAR_FICHA_POR_FINALIZARTURNO);
            }

        }
        notificarObservadores(TipoEvento.REPINTAR_MANO);
    }

    /**
     * Serializa el estado final del turno para enviarlo por red.
     */
    private String serializarJuegoFinal() {
        List<GrupoDTO> grupos = this.gruposDeTurnoDTO;
        StringBuilder payloadBuilder = new StringBuilder();

        for (int i = 0; i < grupos.size(); i++) {
            GrupoDTO grupo = grupos.get(i);

            List<FichaJuegoDTO> fichasOriginalColor = new ArrayList<>();
            for (FichaJuegoDTO ficha : grupo.getFichasGrupo()) {
                int originalRGB = aplicarColorOriginal(ficha.getColor().getRGB());

                FichaJuegoDTO fichaOriginal = new FichaJuegoDTO(
                        ficha.getIdFicha(), ficha.getNumeroFicha(), new Color(originalRGB),
                        ficha.isComodin(), ficha.getFila(), ficha.getColumna()
                );
                fichasOriginalColor.add(fichaOriginal);
            }

            GrupoDTO grupoOriginal = new GrupoDTO(
                    grupo.getTipo(), grupo.getCantidad(), fichasOriginalColor,
                    grupo.getFila(), grupo.getColumna(), grupo.isEsTemporal()
            );

            payloadBuilder.append(grupoOriginal.serializarParaPayload());

            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        return payloadBuilder.toString();
    }

    /**
     * Serializa el estado inicial del turno para revertir jugada.
     */
    private String serializarEstadoRevertido() {
        List<GrupoDTO> grupos = this.gruposDTOAlInicioDelTurno;
        StringBuilder payloadBuilder = new StringBuilder();

        for (int i = 0; i < grupos.size(); i++) {
            GrupoDTO grupo = grupos.get(i);

            // FichaDTOs colores originales
            List<FichaJuegoDTO> fichasOriginalColor = new ArrayList<>();
            for (FichaJuegoDTO ficha : grupo.getFichasGrupo()) {
                int originalRGB = aplicarColorOriginal(ficha.getColor().getRGB());

                FichaJuegoDTO fichaOriginal = new FichaJuegoDTO(
                        ficha.getIdFicha(), ficha.getNumeroFicha(), new Color(originalRGB),
                        ficha.isComodin(), ficha.getFila(), ficha.getColumna()
                );
                fichasOriginalColor.add(fichaOriginal);
            }

            // GrupoDTO temporal con los colores originales
            GrupoDTO grupoOriginal = new GrupoDTO(
                    grupo.getTipo(), grupo.getCantidad(), fichasOriginalColor,
                    grupo.getFila(), grupo.getColumna(), grupo.isEsTemporal()
            );

            payloadBuilder.append(grupoOriginal.serializarParaPayload());

            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        return payloadBuilder.toString();
    }

    /**
     * Registra observadores para actualizaciones del modelo (patrón Observer).
     *
     * @param obs
     */
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    /**
     * Notifica cambios a las vistas con un DTO de actualización.
     *
     * @param tipoEvento
     */
    public void notificarObservadores(TipoEvento tipoEvento) {
        for (Observador observer : this.observadores) {

            List<Ficha> manoEntidad = juego.getJugadorActual().getManoJugador().getFichasEnMano();

            List<FichaJuegoDTO> manoDTO = manoEntidad.stream()
                    .map(f -> new FichaJuegoDTO(f.getId(), f.getNumero(), f.getColor(), f.isComodin()))
                    .collect(Collectors.toList());

            boolean esSuTurno = this.esMiTurno;
            ActualizacionDTO dto = new ActualizacionDTO(tipoEvento, esSuTurno, manoDTO);

            observer.actualiza(this, dto);
        }
    }

    /**
     * Regresa ficha a la mano (si el movimiento es válido) y actualiza la UI.
     *
     * @param idFicha
     */
    public void regresarFichaAMano(int idFicha) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'regresarFichaAMano' ignorada. No es mi turno.");
            return;
        }

        boolean fueRegresadaExitosamente = juego.intentarRegresarFichaAMano(idFicha);

        if (fueRegresadaExitosamente) {

            boolean fichaEncontrada = false;
            for (GrupoDTO grupoDTO : this.gruposDeTurnoDTO) {
                Iterator<FichaJuegoDTO> iter = grupoDTO.getFichasGrupo().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getIdFicha() == idFicha) {
                        iter.remove();
                        grupoDTO.setCantidad(grupoDTO.getFichasGrupo().size());
                        fichaEncontrada = true;
                        break;
                    }
                }
                if (fichaEncontrada) {
                    break;
                }
            }

            this.gruposDeTurnoDTO.removeIf(g -> g.getFichasGrupo().isEmpty());

            notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
            notificarObservadores(TipoEvento.REPINTAR_MANO);

            try {
                String payloadJuegoActualizado = serializarJuegoFinal();
                String mensaje = this.miId + ":MOVER:" + payloadJuegoActualizado;
                this.despachador.enviar(ipServidor, puertoServidor, mensaje);

            } catch (IOException ex) {
                Logger.getLogger(Modelo.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

    /**
     * Metodo para tornar los sets a sus colores originales
     *
     * Logica para el cambio de colores dinamico entre jugadores.
     *
     * @param colorCustom
     * @return
     */
    private int aplicarColorOriginal(int colorCustom) {
        if (this.misColores == null || this.misColores.length < 4) {
            return colorCustom;
        }

        if (colorCustom == this.misColores[0]) {
            return SERVER_NEGRO;
        }
        if (colorCustom == this.misColores[1]) {
            return SERVER_AZUL;
        }
        if (colorCustom == this.misColores[2]) {
            return SERVER_ROJO;
        }
        if (colorCustom == this.misColores[3]) {
            return SERVER_AMARILLO;
        }
        return colorCustom;
    }

    /**
     * Obtiene el estado actual del juego para enviarlo a la vista.
     *
     * @return
     */
    @Override
    public JuegoDTO getTablero() {
        JuegoDTO dto = new JuegoDTO();
        List<Grupo> gruposDelJuego = juego.getGruposEnTablero();

        if (this.gruposDeTurnoDTO != null && this.gruposDeTurnoDTO.size() == gruposDelJuego.size()) {
            for (int i = 0; i < gruposDelJuego.size(); i++) {
                String tipoValidado = gruposDelJuego.get(i).getTipo();
                this.gruposDeTurnoDTO.get(i).setTipo(tipoValidado);
                this.gruposDeTurnoDTO.get(i).setEsTemporal(gruposDelJuego.get(i).esTemporal());
            }
            dto.setGruposEnTablero(this.gruposDeTurnoDTO);
        } else {
            List<GrupoDTO> gruposDTO = gruposDelJuego.stream()
                    .map(this::convertirGrupoEntidadADto)
                    .collect(Collectors.toList());
            dto.setGruposEnTablero(gruposDTO);
        }

        dto.setFichasMazo(this.mazoFichasRestantes);

        // Obtener nombre del jugador actual (quien tiene el turno)
        String nombreJugadorActual = (juego.getJugadorActual() != null)
                ? juego.getJugadorActual().getNickname()
                : "...";

        dto.setJugadorActual(this.idJugadorEnTurnoGlobal);

        List<DTO.JugadorDTO> listaJugadoresDTO = new ArrayList<>();

        // Lista de IDs que existen en tu juego (Debería venir del servidor, 
        // pero para que funcione visualmente ahora, usaremos los fijos).
        List<String> jugadoresAPintar = this.nombresJugadores.isEmpty() ? new ArrayList<>() : this.nombresJugadores;

        for (String id : jugadoresAPintar) {
            DTO.JugadorDTO jugadorDto = new DTO.JugadorDTO();
            jugadorDto.setNombre(id); // Ahora 'id' es el UUID real (ej: Jugador_a1b2)

            // Lista de IDs que existen en tu juego
            List<String> ordenJugadoresUI = new ArrayList<>();

            if (this.nombresJugadores.contains(this.miId)) {
                ordenJugadoresUI.add(this.miId);
            }

            for (String idNombre : this.nombresJugadores) {
                if (!idNombre.equals(this.miId)) {
                    ordenJugadoresUI.add(idNombre);
                }
            }

            for (String idJugadores : ordenJugadoresUI) {
                DTO.JugadorDTO jugadorDTO1 = new DTO.JugadorDTO();

                // 1. Buscamos la info estática (Avatar, Nombre Real)
                JugadorDTO perfil = perfilesJugadores.get(idJugadores);
                if (perfil != null) {
                    jugadorDTO1.setNombre(perfil.getNombre());
                    jugadorDTO1.setIdAvatar(perfil.getIdAvatar());
                    jugadorDTO1.setColores(perfil.getColores());
                } else {
                    // Fallback
                    jugadorDTO1.setNombre(idJugadores);
                    jugadorDTO1.setIdAvatar(1);
                }

                // 2. Determinar si es turno
                boolean esSuTurno = idJugadores.equals(this.idJugadorEnTurnoGlobal);
                jugadorDTO1.setEsTurno(esSuTurno);

                if (idJugadores.equals(this.miId)) {
                    int cantidad = juego.getJugadorActual().getManoJugador().getFichasEnMano().size();
                    jugadorDTO1.setFichasRestantes(cantidad);
                } else {
                    int cantidadRival = conteoFichasRivales.getOrDefault(idJugadores, 14);
                    jugadorDTO1.setFichasRestantes(cantidadRival);
                }

                listaJugadoresDTO.add(jugadorDTO1);
            }

        }

        // Asignar la lista al DTO
        dto.setJugadores(listaJugadoresDTO);

        return dto;
    }

    /**
     * Convierte DTO de grupo a entidad Grupo.
     */
    private Grupo convertirGrupoDtoAEntidad(GrupoDTO dto) {
        List<Ficha> fichas = dto.getFichasGrupo().stream()
                .map(this::convertirFichaDtoAEntidad)
                .collect(Collectors.toList());
        Grupo grupo = new Grupo(dto.getTipo(), fichas.size(), fichas);
        if (!dto.isEsTemporal()) {
            grupo.setValidado();
        }
        return grupo;
    }

    /**
     * Convierte entidad Grupo a DTO.
     */
    private GrupoDTO convertirGrupoEntidadADto(Grupo g) {
        List<FichaJuegoDTO> fichasDTO = g.getFichas().stream()
                .map(f -> new FichaJuegoDTO(f.getId(),
                f.getNumero(), f.getColor(), f.isComodin()))
                .collect(Collectors.toList());
        return new GrupoDTO(g.getTipo(), fichasDTO.size(), fichasDTO, 0, 0, g.esTemporal());
    }

    /**
     * Parte de CU Registrar Usuario Traduce el color "oficial" del servidor al
     * color personalizado que el usuario eligió en el registro.
     *
     * @param coloresSet Colores del jugador para aplicar (ya sea mío o del
     * rival).
     */
    private int aplicarColorPersonalizado(int colorOriginal, int[] coloresSet) {
        if (coloresSet == null || coloresSet.length < 4) {
            return colorOriginal;
        }

        if (colorOriginal == SERVER_NEGRO) {
            return coloresSet[0]; // Lo cambio por el Set 1 del jugador
        }
        if (colorOriginal == SERVER_AZUL) {
            return coloresSet[1]; // Lo cambio por el Set 2 del jugador
        }
        if (colorOriginal == SERVER_ROJO) {
            return coloresSet[2]; // Lo cambio por el Set 3 del jugador
        }
        if (colorOriginal == SERVER_AMARILLO) {
            return coloresSet[3]; // Lo cambio por el Set 4 del jugador
        }
        return colorOriginal; // Para comodines u otros colores.
    }

    private int aplicarColorPersonalizado(int colorOriginal) {
        return aplicarColorPersonalizado(colorOriginal, this.misColores);
    }

    /**
     * Convierte DTO de ficha a entidad Ficha, aplicando los colores
     * personalizados del jugador especificado.
     */
    private Ficha convertirFichaDtoAEntidad(FichaJuegoDTO fDto, String idJugador, boolean esLocal) {
        if (fDto == null) {
            return null;
        }

        int colorOriginalRGB = fDto.getColor().getRGB();
        int colorFinal = colorOriginalRGB;

        // Aplicamos siempre los colores personalizados del jugador local (this.misColores)
        // para que las fichas del rival se vean con el set de colores que eligió el jugador local.
        if (!esLocal) {
            int[] coloresJugador = this.misColores;

            if (coloresJugador != null) {
                colorFinal = aplicarColorPersonalizado(colorOriginalRGB, coloresJugador);

                if (colorFinal != colorOriginalRGB) {
                    System.out.println("[Modelo Debug Ficha] Aplicado color de " + idJugador
                            + ". Original Server: " + colorOriginalRGB + ", Color Final Personalizado: " + colorFinal);
                }
            }
        }

        return new Ficha(
                fDto.getIdFicha(),
                fDto.getNumeroFicha(),
                new Color(colorFinal),
                fDto.isComodin()
        );

    }

    /**
     * Obtiene el arreglo de colores personalizado de un jugador por su ID.
     */
    private int[] obtenerColoresDeRival(String idJugador) {
        JugadorDTO perfil = perfilesJugadores.get(idJugador);
        if (perfil != null && perfil.getColores() != null) {
            return perfil.getColores();
        }
        // Fallback a null o algún color por defecto si no se encuentra
        return null;
    }

    private Ficha convertirFichaDtoAEntidad(FichaJuegoDTO fDto) {
        return convertirFichaDtoAEntidad(fDto, this.miId, false);
    }

    /**
     * Convierte payload en una lista de fichas DTO.
     */
    private List<FichaJuegoDTO> deserializarMano(String payload) {
        List<FichaJuegoDTO> mano = new ArrayList<>();
        if (payload == null || payload.isEmpty()) {
            return mano;
        }

        String[] fichasData = payload.split("\\|");

        for (String fichaData : fichasData) {
            if (fichaData != null && !fichaData.isEmpty()) {
                FichaJuegoDTO ficha = FichaJuegoDTO.deserializar(fichaData);
                if (ficha != null) {
                    mano.add(ficha);
                }
            }
        }
        return mano;
    }

    /**
     * Método auxiliar para parsear la lista de jugadores que llega en el evento
     * MANO_INICIAL
     *
     * @param payloadJugadores String con formato
     * "id,nombre,avatar,c1,c2,c3,c4;..."
     * @return
     */
    public List<JugadorDTO> deserializarListaJugadores(String payloadJugadores) {
        List<JugadorDTO> lista = new ArrayList<>();

        if (payloadJugadores == null || payloadJugadores.isEmpty()) {
            return lista;
        }

        // Separar por jugador (;)
        String[] jugadoresRaw = payloadJugadores.split(";");

        for (String datos : jugadoresRaw) {
            // Separar campos por (,)
            String[] campos = datos.split(",");

            // Validación básica para no tronar
            if (campos.length >= 7) {
                JugadorDTO dto = new JugadorDTO();
                // campos[0] es el ID del socket, lo usamos internamente o como nombre si gustas
                dto.setNombre(campos[1]); // Nickname real

                try {
                    dto.setIdAvatar(Integer.parseInt(campos[2]));

                    int[] colores = new int[4];
                    colores[0] = Integer.parseInt(campos[3]);
                    colores[1] = Integer.parseInt(campos[4]);
                    colores[2] = Integer.parseInt(campos[5]);
                    colores[3] = Integer.parseInt(campos[6]);
                    dto.setColores(colores);

                    // Inicializamos fichas
                    dto.setFichasRestantes(14);

                } catch (NumberFormatException e) {
                    System.err.println("Error parseando datos de jugador: " + datos);
                }

                lista.add(dto);
            }
        }
        return lista;
    }

    public void enviarComandoIniciarPartida() {
        try {
            String mensaje = this.miId + ":INICIAR_PARTIDA:";
            System.out.println("[Modelo] Enviando comando INICIAR_PARTIDA al servidor.");
            this.despachador.enviar(ipServidor, puertoServidor, mensaje);
        } catch (IOException ex) {
            System.err.println("[Modelo] Error al enviar comando INICIAR_PARTIDA: " + ex.getMessage());
        }
    }

    /**
     * Envía comando al servidor para iniciar partida.
     *
     * @param despachador
     */
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    /**
     * Establece la identificación única (ID) del cliente/jugador actual, usada
     * para construir los mensajes enviados al servidor.
     *
     * public void setMiId(String miId) { this.miId = miId; }
     *
     * @param miId
     */
    public void setIdCliente(String miId) {
        this.miId = miId;
        this.idCliente = miId; // Es vital que ambas tengan el ID
        System.out.println("[ModeloJuego] ID Cliente asignado: " + miId);
    }

    public String getIpCliente() {
        return ipCliente;
    }

    public void setIpCliente(String ipCliente) {
        this.ipCliente = ipCliente;
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public void setPuertoCliente(int puertoCliente) {
        this.puertoCliente = puertoCliente;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public int getPuertoServidor() {
        return puertoServidor;
    }

    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    public int getPuertoCliente() {
        return puertoCliente;
    }

}
