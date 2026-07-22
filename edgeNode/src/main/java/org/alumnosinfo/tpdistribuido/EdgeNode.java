package org.alumnosinfo.tpdistribuido;

import org.opencv.core.Mat;

public class EdgeNode {
    static { nu.pattern.OpenCV.loadLocally(); }

    private static final int TCP_PORT = 5555;

    public static void main(String[] args) {
        String host = (args.length > 0) ? args[0].trim() : "localhost";
        String camId = (args.length > 1) ? args[1].trim() : "CAM_01";
        String videoInput = (args.length > 2) ? args[2].trim() : "0";
        System.out.println("🚀 Iniciando Edge Node [" + camId + "] usando fuente: " + videoInput);

        // Gestor de Estados principal
        EdgeStateManager stateManager = new EdgeStateManager();

        // Componentes de deteccion y streaming
        VideoSource videoSource = null;
        MotionDetector motionDetector = new MotionDetector();
        StreamClient streamClient = new StreamClient(host, TCP_PORT, camId, stateManager);

        Mat frame = new Mat();

        long lastFpsCheck = System.currentTimeMillis();
        int frameCounter = 0;

        try {
            videoSource = new VideoSource(videoInput, 160, 120);

            while (true) {
                if (!streamClient.isConnected()) {
                    try {
                        streamClient.connect();
                    } catch (Exception e) {
                        System.out.println("⚠️ Esperando Servidor Central en " + host + "...");
                        Thread.sleep(3000);
                        continue;
                    }
                }

                try {
                    long start = System.currentTimeMillis();
                    boolean success = videoSource.readFrame(frame);
                    System.out.println("Tiempo de captura: " + (System.currentTimeMillis() - start) + "ms");

                    if (!success) {
                        Thread.sleep(30);
                        continue;
                    }

                    frameCounter++;
                    long now = System.currentTimeMillis();
                    if (now - lastFpsCheck >= 5000) {
                        double fps = (frameCounter * 1000.0) / (now - lastFpsCheck);
                        System.out.println("📊 Captura de cámara [" + camId + "]: " + String.format("%.1f", fps) + " FPS");
                        frameCounter = 0;
                        lastFpsCheck = now;
                    }

                    // ESTADOS PRINCIPALES
                    if (stateManager.isStreamingMode()) {
                        streamClient.sendFrame(frame);
                    } else {
                        boolean motionDetected = motionDetector.detectMotion(frame, stateManager);
                        
                        if (motionDetected) {
                            stateManager.setStreamingMode(true);
                        } else {
                            Thread.sleep(50);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error de red, desconectando: " + e.getMessage());
                    streamClient.disconnect();
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error crítico en Edge Node: " + e.getMessage());
        } finally {
            if (videoSource != null) videoSource.release();
            streamClient.disconnect();
        }
    }
}
