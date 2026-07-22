package org.alumnosinfo.tpdistribuido;

import org.alumnosinfo.tpdistribuido.web.WebServer;

public class CentralServer {
    static { nu.pattern.OpenCV.loadLocally(); }

    private static final int TCP_PORT = 5555;
    private static final int WEB_PORT = 8081;

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Servidor Central...");

        // 1. Instanciar el Registro Central
        CameraRegistry registry = new CameraRegistry();

        // 2. Iniciar el Monitor de Inactividad 
        InactivityMonitor monitor = new InactivityMonitor(registry);
        monitor.start();

        // 3. Iniciar el Servidor Web
        WebServer webServer = new WebServer(WEB_PORT, registry);   
        new Thread(webServer).start();

        // 4. Iniciar el Servidor TCP
        TcpServer tcpServer = new TcpServer(TCP_PORT, registry);
        tcpServer.run();
    }
}