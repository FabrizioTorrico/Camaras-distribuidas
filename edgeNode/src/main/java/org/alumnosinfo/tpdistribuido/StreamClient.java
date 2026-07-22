package org.alumnosinfo.tpdistribuido;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StreamClient {
    private final String host;
    private final int port;
    private final String camId;
    private final EdgeStateManager stateManager;
    
    private Socket socket;
    private DataOutputStream out;
    private final MatOfByte buffer = new MatOfByte();

    public StreamClient(String host, int port, String camId, EdgeStateManager stateManager) {
        this.host = host;
        this.port = port;
        this.camId = camId;
        this.stateManager = stateManager;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        
        System.out.println("✅ Conectado al Servidor Central en " + host + ":" + port);
        out.writeUTF(camId); // Handshake inicial
        
        // Hilo para escuchar comandos TCP
        new Thread(() -> {
            try {
                while (true) {
                    String cmd = in.readUTF();
                    if ("START".equals(cmd)) {
                        System.out.println("Petición TCP: START (Forzando MODO STREAMING)");
                        stateManager.setStreamingMode(true);
                    } else if ("STOP".equals(cmd)) {
                        System.out.println("Petición TCP: STOP (Volviendo a MODO ANÁLISIS)");
                        stateManager.setStreamingMode(false);
                        stateManager.setResetPrevGray(true);
                    }
                }
            } catch (IOException e) {
                System.out.println("⚠️ Conexión TCP de comandos finalizada o interrumpida.");
            }
        }).start();
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void sendFrame(Mat frame) throws IOException {
        Imgcodecs.imencode(".jpg", frame, buffer);
        byte[] imageBytes = buffer.toArray();

        synchronized (out) {
            out.writeInt(imageBytes.length);
            out.write(imageBytes);
            out.flush();
        }
    }

    public void disconnect() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignorado intencionalmente al desconectar
        }
    }
}