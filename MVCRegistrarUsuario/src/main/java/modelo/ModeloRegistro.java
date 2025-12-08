/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import TipoEventos.EventoRegistro;
import Util.Configuracion;
import Util.SesionUsuario;
import contratos.iDespachador;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import vista.ObservadorRegistro;

/**
 *
 * @author chris
 */
public class ModeloRegistro implements iModeloRegistro, PropertyChangeListener {

    private List<ObservadorRegistro> observadores = new ArrayList<>();
    private iDespachador despachador;

    private String ipServidor;
    private int puertoServidor;
    private String ipCliente;
    private String idCliente;

    public ModeloRegistro() {
    }

    /**
     * Metodo para registrar al usuario, necesitando un nickname y obteniendo
     * tanto su IP como su puerto de manera local.
     *
     * @param nickname
     * @param idAvatar
     * @param colores
     */
    @Override
    public void registrarUsuario(String nickname, int idAvatar, int[] colores) {
        try {
            // 1. Serializamos los colores a un string simple "c1,c2,c3,c4"
            String coloresSerializados = colores[0] + "," + colores[1] + "," + colores[2] + "," + colores[3];

            // 2. Construir Payload
            // Formato: NICKNAME $ ID_AVATAR $ COLORES
            String payload = nickname + "$" + idAvatar + "$" + coloresSerializados;

            // 3. Construir Mensaje de Protocolo
            // ID_CLIENTE:COMANDO:PAYLOAD
            // Usamos "ACTUALIZAR_PERFIL" porque la conexión (socket) ya se creó en el Ensamblador.
            String mensaje = this.idCliente + ":ACTUALIZAR_PERFIL:" + payload;

            // 4. Enviar al Servidor (Blackboard)
            if (despachador != null) {
                despachador.enviar(this.ipServidor, this.puertoServidor, mensaje);
                System.out.println("[ModeloRegistro] Solicitud ACTUALIZAR_PERFIL enviada para: " + this.idCliente);
            } else {
                System.err.println("[ModeloRegistro] Error: No hay despachador (conexión) configurado.");
            }

        } catch (IOException e) {
            System.err.println("[ModeloRegistro] Error al enviar registro: " + e.getMessage());
        }
    }

    public void iniciar() {
        notificarObservadores(EventoRegistro.ABRIR_VENTANA, null);
    }

    // MOCK ---------------
    public void enviarConfiguracionMock() {
        try {
            // Valores por defecto (Mock ordenado por el maestro)
            int comodines = 2;
            int fichas = 13;

            // Protocolo: ID_CLIENTE:CONFIGURAR_PARTIDA:COMODINES$FICHAS
            String mensaje = this.idCliente + ":CONFIGURAR_PARTIDA:" + comodines + "$" + fichas;

            if (despachador != null) {
                despachador.enviar(this.ipServidor, this.puertoServidor, mensaje);
                System.out.println("[ModeloRegistro] Configuración Default enviada automágicamente.");
            }
        } catch (IOException e) {
            System.err.println("[ModeloRegistro] Error enviando config mock: " + e.getMessage());
        }
    }

    //
    /**
     * Método auxiliar para notificar a todas las vistas suscritas.
     *
     * @param evento El tipo de evento (ENUM)
     * @param mensaje Un mensaje opcional (puede ser null si no lo usas en la
     * vista)
     */
    private void notificarObservadores(EventoRegistro evento, String mensaje) {
        for (ObservadorRegistro obs : observadores) {
            obs.actualiza(evento, mensaje);
        }
    }

    @Override
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    @Override
    public void setPuertoServidor(int puertoServidor) {
        this.puertoServidor = puertoServidor;
    }

    @Override
    public void setIpCliente(String ipCliente) {
        this.ipCliente = ipCliente;
    }

    @Override
    public void setDespachador(iDespachador despachador) {
        this.despachador = despachador;
    }

    @Override
    public void agregarObservador(ObservadorRegistro observador) {
        observadores.add(observador);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        // El payload puede venir en evt.getNewValue() si el servidor manda mensaje de error, etc.
        String mensajeServidor = (evt.getNewValue() != null) ? evt.getNewValue().toString() : "";

        switch (evento) {
            case "REGISTRO_EXITOSO":
                System.out.println("[ModeloRegistro] Confirmación de registro recibida del servidor.");
                notificarObservadores(EventoRegistro.REGISTRO_EXITOSO, null);
                break;

            case "NOMBRE_REPETIDO":
                System.out.println("[ModeloRegistro] Error: Nombre repetido.");
                notificarObservadores(EventoRegistro.NOMBRE_REPETIDO, mensajeServidor);
                break;

            default:
                break;
        }
    }

}
