package org.alumnosinfo.tpdistribuido;

import java.util.concurrent.ConcurrentHashMap;

public class FrameManager {

    // K: ID de Cámara (ej: "cam1"), V: Bytes de la imagen JPG
    private static final ConcurrentHashMap<String, byte[]> frames = new ConcurrentHashMap<>();

    public static void setFrame(String camId, byte[] data) {
        frames.put(camId, data);
    }

    public static byte[] getFrame(String camId) {
        return frames.get(camId);
    }
}
