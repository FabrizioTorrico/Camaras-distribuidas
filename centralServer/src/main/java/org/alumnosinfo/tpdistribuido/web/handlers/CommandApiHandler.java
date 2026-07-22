package org.alumnosinfo.tpdistribuido.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.alumnosinfo.tpdistribuido.CameraRegistry;

import java.io.IOException;
import java.util.Map;

public class CommandApiHandler extends BaseHttpHandler {
    private final String cmd;
    private final CameraRegistry registry;

    public CommandApiHandler(String cmd, CameraRegistry registry) {
        this.cmd = cmd;
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

        String ip = registry.getCameraIp(targetCamId);
        if (ip == null) {
            sendError(exchange, 404, "Cámara no encontrada o no registrada");
            return;
        }

        registry.sendCommand(targetCamId, cmd.toUpperCase());

        // Si forzamos un /start, le damos 1 minuto de gracia al monitor de inactividad
        if ("start".equalsIgnoreCase(cmd)) {
            registry.recordMotion(targetCamId);
        }
        sendJsonResponse(exchange, 200, "{\"status\":\"success\", \"message\":\"" + cmd + " ejecutado\"}");
    }
}