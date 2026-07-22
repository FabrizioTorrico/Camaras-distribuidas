package org.alumnosinfo.tpdistribuido.web;

import com.sun.net.httpserver.HttpServer;
import org.alumnosinfo.tpdistribuido.CameraRegistry;
import org.alumnosinfo.tpdistribuido.web.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer implements Runnable {

    private final int port;
    private final CameraRegistry registry;

    public WebServer(int port, CameraRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Inyectamos las dependencias necesarias en cada ruta
            server.createContext("/stream", new StreamHandler(registry));
            server.createContext("/api/cameras", new CamerasApiHandler(registry));
            server.createContext("/api/start", new CommandApiHandler("start", registry));
            server.createContext("/api/stop", new CommandApiHandler("stop", registry));
            
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            
            System.out.println("🌐 Servidor Web iniciado en http://localhost:" + port);
            
        } catch (IOException e) {
            System.err.println("❌ Error iniciando Servidor Web: " + e.getMessage());
        }
    }
}