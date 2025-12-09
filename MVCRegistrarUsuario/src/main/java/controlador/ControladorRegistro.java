/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import TipoEventos.EventoRegistro;
import modelo.iModeloRegistro;
import contratos.controladoresMVC.iControlCUPrincipal;
import contratos.controladoresMVC.iControlRegistro;
import java.awt.Color;
import vista.ObservadorRegistro;
import vista.RegistrarUsuario;

/**
 *
 * @author chris
 */
public class ControladorRegistro implements iControlRegistro {

    private iModeloRegistro modelo;
    private iControlCUPrincipal controlNavegacion;
    private RegistrarUsuario vista;

    public ControladorRegistro(iModeloRegistro modelo) {
        this.modelo = modelo;
    }

    @Override
    public void setConfiguracion(String ipServidor, int puertoServidor, String ipCliente) {
        modelo.setIpServidor(ipServidor);
        modelo.setPuertoServidor(puertoServidor);
        modelo.setIpCliente(ipCliente);

        System.out.println("[ControladorRegistro] Configuración de red establecida en el modelo.");
    }

    @Override
    public void intentarRegistrar(String nickname, String avatar,
            Color set1, Color set2, Color set3, Color set4) {

        if (nickname == null || nickname.trim().isEmpty()) {
            System.out.println("Validación: Nickname vacío.");
            return;
        }

        // 1. Obtenemos los valores enteros RGB
        int rgb1 = set1.getRGB();
        int rgb2 = set2.getRGB();
        int rgb3 = set3.getRGB();
        int rgb4 = set4.getRGB();

        // 2. Creamos el arreglo
        int[] colores = {rgb1, rgb2, rgb3, rgb4};

        // 3. Convertimos el avatar a int
        int idAvatar = 1; // Valor por defecto
        try {
            idAvatar = Integer.parseInt(avatar);
        } catch (NumberFormatException e) {
            System.out.println("Error parseando avatar, usando default");
        }

        // 4. Enviamos al modelo
        modelo.registrarUsuario(nickname, idAvatar, colores);
    }

    @Override
    public void iniciarRegistro() {
        modelo.iniciar();
    }

    public void setVista(RegistrarUsuario vista) {
        this.vista = vista;
    }

    public void cerrarVista() {
        if (vista != null) {
            vista.setVisible(false);
        } else {
            System.err.println("[ControladorRegistro] No se puede cerrar la vista: es null.");
        }
    }

    /**
     * Inyección de dependencias para la navegación.
     *
     * @param controlNavegacion El controlador principal o del Lobby.
     */
    @Override
    public void setNavegacion(iControlCUPrincipal controlNavegacion) {
        this.controlNavegacion = controlNavegacion;
    }

    // Método opcional por si la vista necesita pedir la navegación explícitamente, 
    // aunque idealmente la vista usará su propia referencia de navegación.
    public void navegarSiguientePantalla() {
        if (controlNavegacion != null) {
            System.out.println("[ControladorRegistro] Navegando al Lobby/Principal...");
            controlNavegacion.iniciarCU();
        }
    }
}
