package org.alumnosinfo.tpdistribuido;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InactivityMonitor {
    private final CameraRegistry registry;
    private final long timeoutMs = 60 * 1000; // 60 segundos de inactividad
    private final ScheduledExecutorService scheduler;

    public InactivityMonitor(CameraRegistry registry) {
        this.registry = registry;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            
            for (String camId : registry.getRegisteredCameras()) {
                if (registry.isStreaming(camId)) {
                    Long lastMotion = registry.getLastMotionTime(camId);
                    
                    if (lastMotion != null && (now - lastMotion) > timeoutMs) {
                        System.out.println("⏳ Timeout de inactividad alcanzado para " + camId + ". Forzando Estado 1...");
                        
                        // Evita spamear requests reseteando el timer temporalmente
                        registry.recordMotion(camId); 
                        stop(camId);
                    }
                }
            }
        }, 10, 10, TimeUnit.SECONDS); // Revisa cada 10 segundos
    }

    private void stop(String camId) {
        registry.sendCommand(camId, "STOP");
        System.out.println("✅ Comando TCP STOP enviado exitosamente a " + camId);
    }
}