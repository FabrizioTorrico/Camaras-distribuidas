package org.alumnosinfo.tpdistribuido;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class EdgeNode {
    // Cargamos la librería nativa de OpenCV
    static {
        nu.pattern.OpenCV.loadLocally();
    }

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5555;
    private static final String DEFAULT_CAM_ID = "cam1";
    private static final int FPS = 20;

    public static void main(String[] args) {
        String serverHost = (args.length > 0) ? args[0] : DEFAULT_HOST;
        int serverPort = DEFAULT_PORT;
        String cameraId = (args.length > 2) ? args[2] : DEFAULT_CAM_ID;

        if (args.length > 1) {
            try { serverPort = Integer.parseInt(args[1]); } catch (Exception e) {}
        }

        System.out.println("==========================================");
        System.out.println("               EDGE NODE Listo            ");
        System.out.println("==========================================");
        System.out.println("Config: " + serverHost + ":" + serverPort + " [" + cameraId + "]");

        VideoCapture camera = new VideoCapture(0);
        
        // Resolución
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

        if (!camera.isOpened()) {
            System.err.println("❌ Error: No se pudo abrir la cámara (Index 0).");
            System.exit(1);
        }
        
        System.out.println("✅ Cámara iniciada correctamente.");

        Mat frame = new Mat();
        MatOfByte buffer = new MatOfByte();

        while (true) {
            try (Socket socket = new Socket(serverHost, serverPort);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                System.out.println("✅ Conectado al servidor.");
                out.writeUTF(cameraId);
                out.flush();

                while (!socket.isClosed()) {
                    long startTime = System.currentTimeMillis();

                    // 2. Capturar Frame
                    if (!camera.read(frame)) {
                        System.err.println("⚠️ Error leyendo frame de cámara.");
                        Thread.sleep(100);
                        continue;
                    }

                    // 3. Comprimimos a JPG directamente
                    if (!frame.empty()) {
                        Imgcodecs.imencode(".jpg", frame, buffer);
                        byte[] imageBytes = buffer.toArray();

                        // Enviar
                        out.writeInt(imageBytes.length);
                        out.write(imageBytes);
                        out.flush();
                    }

                    // Control de FPS
                    long frameTime = System.currentTimeMillis() - startTime;
                    long waitTime = (1000 / FPS) - frameTime;
                    if (waitTime > 0) Thread.sleep(waitTime);
                }

            } catch (IOException e) {
                System.err.println("❌ Desconectado: " + e.getMessage());
                try { Thread.sleep(3000); } catch (InterruptedException ie) { break; }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        
        camera.release();
    }
}