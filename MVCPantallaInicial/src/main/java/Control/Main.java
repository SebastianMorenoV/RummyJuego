/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Control;

import Modelo.Modelo;
import Vista.VistaLobby;
import contratos.iDespachador;
import contratos.iNavegacion;
import controlador.Controlador;
import java.io.IOException;
import java.net.Socket;
import main.BlackBoardServer;
import modelo.ModeloConfig;
import modelo.iModeloConfig;
import sockets.ClienteTCP;
import vista.ConfigurarPartida;

/**
 * Esta clase es el iniciador de RummyKub
 * Su main por el momento ensambla al primer mvc en pantalla el lobby, del cual este necesita el configurar(Crear Partida) o el solicitar unirse.
 * Se ensamblan los dos MVCs aqui mismo temporalmente.
 * @author moren
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static final int PUERTO_SERVER = 5000;

    public static void main(String[] args) {
        Modelo modeloInicial = new Modelo();
        iNavegacion logicaNavegacion = new iNavegacion() {
            @Override
            public void iniciarConfiguracionPartida() {
                System.out.println("Arrancando módulo Configurar Partida...");

                iModeloConfig modeloConfig = new ModeloConfig();
                iDespachador despachador = new ClienteTCP();
                
                modeloConfig.setDespachador(despachador);
                
                Controlador controladorConfig = new Controlador(modeloConfig);

                ConfigurarPartida vistaConfig = new ConfigurarPartida(controladorConfig);

                modeloConfig.añadirObservador(vistaConfig);

            }
        };

        Control controlInicial = new Control(modeloInicial, logicaNavegacion);

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
