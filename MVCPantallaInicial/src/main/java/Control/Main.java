/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Control;

import Ensambladores.EnsambladorCliente;
import Modelo.Modelo;
import Vista.VistaLobby;
import contratos.Configuracion;
import contratos.iDespachador;
import contratos.iEnsambladorCliente;
import contratos.iListener;
import contratos.iNavegacion;
import controlador.Controlador;
import java.io.IOException;
import java.net.Socket;
import main.BlackBoardServer;
import modelo.ModeloConfig;
import modelo.iModeloConfig;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;
import Modelo.iModelo;

/**
 *
 * @author moren
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static final int PUERTO_SERVER = 5000;
    private static final int PUERTO_CLIENTE_INICIAL = 9002;

    public static void main(String[] args) {

        // 1. Instanciamos el Modelo del primer MVC
        Modelo modeloInicial = new Modelo();
        iEnsambladorCliente ensamblador = new EnsambladorCliente();

        iDespachador despachador = ensamblador.crearDespachador(Configuracion.getIpServidor(), Configuracion.getPuerto());

        modeloInicial.setDespachador(despachador);
        iListener listener = ensamblador.crearListener("ClienteInicial", modeloInicial);

        new Thread(() -> {
            try {
                listener.iniciar(PUERTO_CLIENTE_INICIAL);
            } catch (IOException e) {
                System.err.println("Error al iniciar listener: " + e.getMessage());
            }
        }).start();

        iNavegacion logicaNavegacion = new iNavegacion() {
            @Override
            public void iniciarConfiguracionPartida() {
                System.out.println("Orquestador: Arrancando módulo Configurar Partida...");

                // 1. Instancia (usando a interface)
                iModeloConfig modeloConfig = new ModeloConfig();
                iDespachador despachador = new ClienteTCP();
                modeloConfig.setDespachador(despachador);
                // 2. Controlador
                Controlador controladorConfig = new Controlador(modeloConfig);

                // 3. Vista (A vista implementa Observador)
                ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfig);

                // 4. Ligar Observador (Agora funciona direto!)
                modeloConfig.añadirObservador(vistaConfig);

                // 5. Iniciar
                controladorConfig.iniciarCU();
            }

           
        };

        // 3. Instanciamos el Control INYECTANDO la navegación
        Control controlInicial = new Control(modeloInicial, logicaNavegacion);

        // 4. Instanciamos la Vista y la ligamos (Control no sabe de Vista)
        VistaLobby vistaLobby = new VistaLobby(controlInicial);
        modeloInicial.añadirObservador(vistaLobby);

        iniciarServidorSiNoExiste();

    }

    private static void iniciarServidorSiNoExiste() {
        if (estaCorriendoElServidor(PUERTO_SERVER)) {
            System.out.println(">> El servidor ya esta corriendo. No se iniciará una nueva instancia.");
        } else {
            System.out.println(">> Servidor no detectado. Iniciando BlackBoardServer...");
            new Thread(() -> {
                BlackBoardServer.main(new String[]{});
            }).start();
        }
    }

    private static boolean estaCorriendoElServidor(int puerto) {
        try (Socket s = new Socket("localhost", puerto)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
