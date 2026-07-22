package org.alumnosinfo.tpdistribuido;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CameraSession implements Runnable {
    private final Socket socket;
    private final CameraRegistry registry;
    private final BlockingQueue<byte[]> frameQueue = new LinkedBlockingQueue<>(5);

    public CameraSession(Socket socket, CameraRegistry registry) {
        this.socket = socket;
        this.registry = registry;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            String camId = in.readUTF();
            String ip = socket.getInetAddress().getHostAddress();
            registry.registerCameraConnection(camId, ip, out);
            
            // Refrescar el timer de inactividad al conectar para que no se apague inmediatamente
            registry.recordMotion(camId); 
            
            System.out.println("✅ Cámara conectada: " + camId + " desde IP: " + ip);

            Thread processorThread = new Thread(new FrameProcessor(camId, frameQueue, registry));
            processorThread.start();

            while (true) {
                int length = in.readInt();
                if (length > 0) {
                    byte[] imageBytes = new byte[length];
                    in.readFully(imageBytes);
                    
                    // Si la cola está llena, sacamos el frame más viejo para meter el nuevo (evita el lag visual)
                    if (frameQueue.remainingCapacity() == 0) {
                        frameQueue.poll(); 
                    }
                    frameQueue.offer(imageBytes);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Cámara desconectada: " + e.getMessage());
        }
    }
}