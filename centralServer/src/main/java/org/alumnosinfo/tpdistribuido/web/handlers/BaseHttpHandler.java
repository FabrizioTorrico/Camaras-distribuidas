package org.alumnosinfo.tpdistribuido.web.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        // 1. Configuración global de CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        // 2. Manejo de pre-flight requests (CORS)
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // 3. Ejecutar la lógica específica del handler hijo
        execute(exchange);
    }

    protected abstract void execute(HttpExchange exchange) throws IOException;

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    protected void sendError(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        String jsonError = String.format("{\"error\":\"%s\"}", errorMessage);
        sendJsonResponse(exchange, statusCode, jsonError);
    }

    protected Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }
}