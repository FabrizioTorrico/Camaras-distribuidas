package org.alumnosinfo.tpdistribuido;

import java.net.ServerSocket;
import java.net.Socket;

public class CentralServer {

    private static final int PORT_CAMERAS = 5555;
    private static final int PORT_WEB = 8081;

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================");
        System.out.println("   CENTRAL SERVER - Cámaras Distribuidas 2");
        System.out.println("==========================================");

        // Hilo 1. Iniciar el Servidor Web
        new Thread(new WebServer(PORT_WEB)).start();

        // Hilo 2. Iniciar el Servidor de Sockets
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_CAMERAS);
            System.out.println("🎥 Escuchando Cámaras en puerto TCP: " + PORT_CAMERAS);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new CameraHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}