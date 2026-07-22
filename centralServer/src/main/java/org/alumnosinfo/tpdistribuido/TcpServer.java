package org.alumnosinfo.tpdistribuido;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {
    private final int port;
    private final CameraRegistry registry;

    public TcpServer(int port, CameraRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("📹 Servidor TCP (Video) escuchando en puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new CameraSession(clientSocket, registry)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error en servidor TCP: " + e.getMessage());
        }
    }
}