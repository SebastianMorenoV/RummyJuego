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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import vista.ObservadorRegistro;

/**
 *
 * @author chris
 */
public class ModeloRegistro implements iModeloRegistro {

    private List<ObservadorRegistro> observadores = new ArrayList<>();
    private iDespachador despachador;

    private String ipServidor;
    private int puertoServidor;
    private String ipCliente;

    public ModeloRegistro() {
    }

    /**
     * Metodo para registrar al usuario, necesitando un nickname y obteniendo
     * tanto su IP como su puerto de manera local.
     *
     * @param nickname
     * @param avatar
     * @param color1
     * @param color2
     * @param color3
     * @param color4
     */
    @Override
    public void registrarUsuario(String nickname, String avatar, int color1, int color2, int color3, int color4) {
        try {
            // 1. Guardar sesión localmente
            Color c1 = new Color(color1);
            Color c2 = new Color(color2);
            Color c3 = new Color(color3);
            Color c4 = new Color(color4);

            SesionUsuario.guardarSesion(nickname, avatar, c1, c2, c3, c4);;

            // 2. Construir Payload
            // Protocolo: NICKNAME:REGISTRAR:IP_CLIENTE$PUERTO$AVATAR$COLOR
            String payload
                    = this.ipCliente + "$"
                    + this.puertoServidor + "$"
                    + avatar + "$"
                    + color1 + "$"
                    + color2 + "$"
                    + color3 + "$"
                    + color4;
            String mensaje = nickname + ":REGISTRAR:" + payload;

            // 3. Enviar al Servidor
            if (despachador != null) {
                despachador.enviar(this.ipServidor, this.puertoServidor, mensaje);
                notificarObservadores(EventoRegistro.REGISTRO_EXITOSO, "Registro enviado");
            } else {
                System.out.println("[ModeloRegistro] Modo offline: Despachador es null, pero la sesión se guardó.");
                notificarObservadores(EventoRegistro.REGISTRO_EXITOSO, "Registro local exitoso");
            }

        } catch (IOException e) {
            System.err.println("[ModeloRegistro] Error al enviar registro: " + e.getMessage());
        }
    }

    public void iniciar() {
        notificarObservadores(EventoRegistro.ABRIR_VENTANA, null);
    }

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

}
