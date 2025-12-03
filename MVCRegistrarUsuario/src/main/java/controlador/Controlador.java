/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.iModeloRegistro;
import contratos.controladoresMVC.iControlRegistro;
import java.awt.Color;

/**
 *
 * @author chris
 */
public class Controlador implements iControlRegistro {

    private iModeloRegistro modelo;

    public Controlador(iModeloRegistro modelo) {
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
    public void intentarRegistrar(String nickname, String avatar, Color color) {
        int rgb = color.getRGB();
        modelo.registrarUsuario(nickname, avatar, rgb);
    }

    @Override
    public void iniciarRegistro() {
        modelo.iniciar();
    }
}
