package org.alumnosinfo.tpdistribuido;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CameraRegistry {
    private final ConcurrentHashMap<String, byte[]> frames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> cameraIps = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DataOutputStream> commandStreams = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastFrameTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastMotionTime = new ConcurrentHashMap<>();

    public void setFrame(String camId, byte[] data) {
        frames.put(camId, data);
        lastFrameTime.put(camId, System.currentTimeMillis());
    }

    public byte[] getFrame(String camId) {
        return frames.get(camId);
    }

    public Set<String> getCameraIds() {
        return frames.keySet();
    }

    public void registerCameraConnection(String camId, String ip, DataOutputStream out) {
        cameraIps.put(camId, ip);
        commandStreams.put(camId, out);
    }

    public String getCameraIp(String camId) {
        return cameraIps.get(camId);
    }

    public Set<String> getRegisteredCameras() {
        return cameraIps.keySet();
    }
    
    public boolean isStreaming(String camId) {
        Long lastTime = lastFrameTime.get(camId);
        if (lastTime == null) return false;
        return (System.currentTimeMillis() - lastTime) < 2000;
    }

    public void recordMotion(String camId) {
        lastMotionTime.put(camId, System.currentTimeMillis());
    }

    public Long getLastMotionTime(String camId) {
        return lastMotionTime.get(camId);
    }

    public void sendCommand(String camId, String command) {
        DataOutputStream out = commandStreams.get(camId);
        if (out != null) {
            try {
                out.writeUTF(command);
                out.flush();
            } catch (IOException e) {
                System.err.println("❌ Error enviando comando a " + camId + ": " + e.getMessage());
            }
        }
    }
}