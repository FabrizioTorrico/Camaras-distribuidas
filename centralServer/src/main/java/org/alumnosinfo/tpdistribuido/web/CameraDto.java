package org.alumnosinfo.tpdistribuido.web;

public class CameraDto {
    private final String id;
    private final String ip;
    private final boolean isStreaming;

    public CameraDto(String id, String ip, boolean isStreaming) {
        this.id = id;
        this.ip = ip;
        this.isStreaming = isStreaming;
    }

    public String toJson() {
        return String.format("{\"id\":\"%s\", \"ip\":\"%s\", \"isStreaming\":%b}", id, ip, isStreaming);
    }
}