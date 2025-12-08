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
import Util.Configuracion;
import Vista.Observador;
import Vista.TipoEvento;
import contratos.iDespachador;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private Map<String, JugadorDTO> perfilesJugadores = new HashMap<>();
    private List<String> nombresJugadores = new ArrayList<>();
    private int mazoFichasRestantes = 0;

    private boolean coloresCargados = false;

    private String idCliente;
    private String miId;
    Configuracion config;

    //variables adicionales de CU registrar jugador//
    private String miNickname = "Cargando...";
    private String miAvatar = "avatar1";
    private int[] misColores = {0, 0, 0, 0};

    private static final int SERVER_NEGRO = -16777216;
    private static final int SERVER_ROJO = -65536;
    private static final int SERVER_AZUL = -16776961;
    private static final int SERVER_AMARILLO = -14336;

    public Modelo() {
        this.config = new Configuracion();
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
                                dtoPerfil.setNombre(perfilSplit[0]); // Nickname real

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
                    notificarObservadores(TipoEvento.TOMO_FICHA);
                    notificarObservadores(TipoEvento.REPINTAR_MANO);
                    notificarObservadores(TipoEvento.MOSTRAR_JUEGO);
                    notificarObservadores(TipoEvento.INCIALIZAR_FICHAS);

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
                    String dataContadores = partesTurno[2]; // "J1=14;J2=13;"
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
                    List<GrupoDTO> gruposMovidos = GrupoDTO.deserializarLista(payloadd);
                    if (gruposMovidos != null) {
                        this.actualizarVistaTemporal(gruposMovidos);
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
                        this.miAvatar = partes[1]; // AVATAR_ID_STRING

                        if (partes.length > 2) {
                            String coloresStr = partes[2];
                            String[] colorStrings = coloresStr.split(",");

                            if (colorStrings.length >= 4) {
                                this.misColores[0] = Integer.parseInt(colorStrings[0]); // C1
                                this.misColores[1] = Integer.parseInt(colorStrings[1]); // C2
                                this.misColores[2] = Integer.parseInt(colorStrings[2]); // C3
                                this.misColores[3] = Integer.parseInt(colorStrings[3]); // C4

                                System.out.println("[Modelo Debug] Colores personalizados cargados: "
                                        + this.misColores[0] + ", "
                                        + this.misColores[1] + ", "
                                        + this.misColores[2] + ", "
                                        + this.misColores[3]);
                            }

                        }

                        // 3. ACTIVAR BANDERA (FIX COLORES)
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
        }
    }

    /**
     * Refresca estado del tablero temporalmente antes de validar jugada.
     */
    private void actualizarVistaTemporal(List<GrupoDTO> gruposPropuestos) {
        this.gruposDeTurnoDTO = gruposPropuestos;

        List<Grupo> nuevosGrupos = gruposPropuestos.stream()
                .map(this::convertirGrupoDtoAEntidad)
                .collect(Collectors.toList());
        juego.colocarFichasEnTablero(nuevosGrupos);

        notificarObservadores(TipoEvento.ACTUALIZAR_TABLERO_TEMPORAL);
    }

    /**
     * Envía movimiento al servidor y actualiza tablero local.
     */
    public void colocarFicha(List<GrupoDTO> grupos) {
        if (!this.esMiTurno) {
            System.out.println("[Modelo] Acción 'colocarFicha' ignorada. No es mi turno.");
            return;
        }
        this.actualizarVistaTemporal(grupos);

        StringBuilder payloadBuilder = new StringBuilder();
        for (int i = 0; i < grupos.size(); i++) {
            payloadBuilder.append(grupos.get(i).serializarParaPayload());
            if (i < grupos.size() - 1) {
                payloadBuilder.append("$");
            }
        }
        String payloadCompleto = payloadBuilder.toString();
        String mensaje = this.miId + ":MOVER:" + payloadCompleto;

        try {
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);

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
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensajeTomar);

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
                this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);

            } catch (IOException ex) {
                Logger.getLogger(Modelo.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            if (!gruposDTOAlInicioDelTurno.equals(gruposDeTurnoDTO)) {

                System.out.println("Jugada invalida");
                System.out.println(gruposDTOAlInicioDelTurno.toString());
                System.out.println(gruposDeTurnoDTO.toString());

                notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
                try {
                    String payloadJuegoRevertido = serializarEstadoRevertido();
                    String mensaje = this.miId + ":MOVER:" + payloadJuegoRevertido;
                    this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);

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
     */
    public void agregarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    /**
     * Notifica cambios a las vistas con un DTO de actualización.
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
                this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);

            } catch (IOException ex) {
                Logger.getLogger(Modelo.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            notificarObservadores(TipoEvento.JUGADA_INVALIDA_REVERTIR);
        }
    }

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
     * Metodo para cargar la imagen del avatar
     *
     * @param nombreAvatar
     * @return
     */
    private byte[] cargarAvatarBytes(String nombreAvatar) {
        String path = "/avatares/" + nombreAvatar + ".png";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            System.err.println("Error cargando avatar: " + path);
            return new byte[0];
        }
    }

    //diferente puerto de escucha
    /**
     * Obtiene el estado actual del juego para enviarlo a la vista.
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

        dto.setJugadorActual(this.idJugadorEnTurnoGlobal);

        List<DTO.JugadorDTO> listaJugadoresDTO = new ArrayList<>();

        // Lista de IDs que existen en tu juego (Debería venir del servidor, 
        // pero para que funcione visualmente ahora, usaremos los fijos).
        List<String> ordenJugadoresUI = new ArrayList<>();

        if (this.nombresJugadores.contains(this.miId)) {
            ordenJugadoresUI.add(this.miId);
        }

        for (String id : this.nombresJugadores) {
            if (!id.equals(this.miId)) {
                ordenJugadoresUI.add(id);
            }
        }

        for (String id : ordenJugadoresUI) { // Usar la lista con el orden fijo
            DTO.JugadorDTO jugadorDto = new DTO.JugadorDTO();

            // 1. Buscamos la info estática (Avatar, Nombre Real)
            JugadorDTO perfil = perfilesJugadores.get(id);
            if (perfil != null) {
                jugadorDto.setNombre(perfil.getNombre());
                jugadorDto.setIdAvatar(perfil.getIdAvatar());
                jugadorDto.setColores(perfil.getColores());
            } else {
                // Fallback
                jugadorDto.setNombre(id);
                jugadorDto.setIdAvatar(1);
            }

            // 2. Determinar si es turno
            boolean esSuTurno = id.equals(this.idJugadorEnTurnoGlobal);
            jugadorDto.setEsTurno(esSuTurno);

            if (id.equals(this.miId)) { // Compara contra el ID local
                int cantidad = juego.getJugadorActual().getManoJugador().getFichasEnMano().size();
                jugadorDto.setFichasRestantes(cantidad);
            } else {
                int cantidadRival = conteoFichasRivales.getOrDefault(id, 14);
                jugadorDto.setFichasRestantes(cantidadRival);
            }

            listaJugadoresDTO.add(jugadorDto);
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
     */
    private int aplicarColorPersonalizado(int colorOriginal) {
        if (this.misColores == null || this.misColores.length < 4) {
            return colorOriginal;
        }

        if (colorOriginal == SERVER_NEGRO) {
            return this.misColores[0]; // Set 1 (Color Personalizado 1)
        }
        // SERVER_AZUL = -16776961 (Blue)
        if (colorOriginal == SERVER_AZUL) {
            return this.misColores[1]; // Set 2 (Color Personalizado 2)
        }
        // SERVER_ROJO = -65536 (Red)
        if (colorOriginal == SERVER_ROJO) {
            return this.misColores[2]; // Set 3 (Color Personalizado 3)
        }
        // SERVER_AMARILLO = -14336 (Yellow)
        if (colorOriginal == SERVER_AMARILLO) {
            return this.misColores[3]; // Set 4 (Color Personalizado 4)
        }

        return colorOriginal; // Para comodines u otros colores.
    }

    /**
     * Convierte DTO de ficha a entidad Ficha.
     */
    private Ficha convertirFichaDtoAEntidad(FichaJuegoDTO fDto) {
        if (fDto == null) {
            return null;
        }

        int colorOriginalRGB = fDto.getColor().getRGB();
        int colorFinal = colorOriginalRGB;

        if (this.coloresCargados) {
            colorFinal = aplicarColorPersonalizado(colorOriginalRGB);

            if (colorFinal != colorOriginalRGB) {
                System.out.println("[Modelo Debug Ficha] Aplicado color personalizado. Original Server: "
                        + colorOriginalRGB + ", Color Final Personalizado: " + colorFinal);
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
            this.despachador.enviar(Configuracion.getIpServidor(), Configuracion.getPuerto(), mensaje);
        } catch (IOException ex) {
            System.err.println("[Modelo] Error al enviar comando INICIAR_PARTIDA: " + ex.getMessage());
        }
    }

    /**
     * Envía comando al servidor para iniciar partida.
     */
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    /**
     * Establece la identificación única (ID) del cliente/jugador actual, usada
     * para construir los mensajes enviados al servidor.
     *
     * public void setMiId(String miId) { this.miId = miId; }
     */
    public void setIdCliente(String miId) {
        this.miId = miId;
        this.idCliente = miId; // Es vital que ambas tengan el ID
        System.out.println("[ModeloJuego] ID Cliente asignado: " + miId);
    }
}
