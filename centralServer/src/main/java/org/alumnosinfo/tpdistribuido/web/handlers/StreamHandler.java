package org.alumnosinfo.tpdistribuido.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.alumnosinfo.tpdistribuido.CameraRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

public class StreamHandler extends BaseHttpHandler {
    private static final String BOUNDARY = "--BoundaryString";
    private final CameraRegistry registry;

    public StreamHandler(CameraRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());
        String targetCamId = queryParams.get("id");

        if (targetCamId == null) {
            sendError(exchange, 400, "Falta parámetro 'id'");
            return;
        }

        if (!registry.getCameraIds().contains(targetCamId)) {
            sendError(exchange, 404, "Cámara desconectada o inexistente");
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", "multipart/x-mixed-replace; boundary=" + BOUNDARY);
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, private");
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            byte[] lastFrame = new byte[0];

            while (registry.getCameraIds().contains(targetCamId)) {
                byte[] currentFrame = registry.getFrame(targetCamId);

                if (currentFrame != null && currentFrame.length > 0 && currentFrame != lastFrame) {
                    os.write((BOUNDARY + "\r\n").getBytes());
                    os.write("Content-Type: image/jpeg\r\n".getBytes());
                    os.write(("Content-Length: " + currentFrame.length + "\r\n\r\n").getBytes());
                    os.write(currentFrame);
                    os.write("\r\n".getBytes());
                    os.flush();
                    lastFrame = currentFrame;
                }
                Thread.sleep(30);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            // El cliente cerró la pestaña/conexión. Es normal.
        }
    }
}