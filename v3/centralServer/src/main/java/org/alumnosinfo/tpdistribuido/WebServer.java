package org.alumnosinfo.tpdistribuido;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WebServer implements Runnable {

    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/stream", new StreamHandler());
            
            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
            
            System.out.println("🌐 Servidor Web iniciado en http://localhost:" + port);
            System.out.println("   --> Ver cámara: http://localhost:" + port + "/stream?id=cam1");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase interna que maneja la conexión con el navegador
    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 1. Obtener el ID de la cámara de la URL (ej: id=cam1)
            String query = exchange.getRequestURI().getQuery();
            String camId = "cam1"; // Default
            if (query != null && query.contains("id=")) {
                camId = query.split("id=")[1].split("&")[0];
            }

            System.out.println("Nuevo espectador conectado a: " + camId);

            // 2. Configurar cabeceras MJPEG
            exchange.getResponseHeaders().set("Content-Type", "multipart/x-mixed-replace; boundary=--BoundaryString");
            exchange.sendResponseHeaders(200, 0);

            OutputStream os = exchange.getResponseBody();

            // 3. Bucle infinito de envío de imágenes
            try {
                while (true) {
                    byte[] imageBytes = FrameManager.getFrame(camId);

                    if (imageBytes != null) {
                        // Escribir cabeceras de ESTE frame específico
                        os.write(("--BoundaryString\r\n").getBytes());
                        os.write("Content-Type: image/jpeg\r\n".getBytes());
                        os.write(("Content-Length: " + imageBytes.length + "\r\n\r\n").getBytes());
                        
                        // Escribir la imagen
                        os.write(imageBytes);
                        os.write("\r\n".getBytes());
                        os.flush();
                    }

                    // Control de FPS de salida (para no saturar al navegador)
                    Thread.sleep(50); // ~20 FPS
                }
            } catch (Exception e) {
                System.out.println("Espectador desconectado (" + camId + ")");
            } finally {
                os.close();
            }
        }
    }
}