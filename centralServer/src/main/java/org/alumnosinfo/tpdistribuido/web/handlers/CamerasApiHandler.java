package org.alumnosinfo.tpdistribuido.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.alumnosinfo.tpdistribuido.CameraRegistry;
import org.alumnosinfo.tpdistribuido.web.CameraDto;

import java.io.IOException;
import java.util.stream.Collectors;

public class CamerasApiHandler extends BaseHttpHandler {
    private final CameraRegistry registry;

    public CamerasApiHandler(CameraRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        // Uso de Streams y DTO para armar el JSON limpiamente
        String jsonList = registry.getRegisteredCameras().stream()
                .map(camId -> new CameraDto(
                        camId, 
                        registry.getCameraIp(camId), 
                        registry.isStreaming(camId)
                ))
                .map(CameraDto::toJson)
                .collect(Collectors.joining(",", "[", "]"));

        sendJsonResponse(exchange, 200, jsonList);
    }
}