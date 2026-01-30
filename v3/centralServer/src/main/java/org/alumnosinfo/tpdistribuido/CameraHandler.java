package org.alumnosinfo.tpdistribuido;

import java.io.DataInputStream;
import java.net.Socket;

public class CameraHandler implements Runnable {

    private final Socket socket;

    public CameraHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String camId = in.readUTF();

            System.out.println("✅ Cámara conectada: " + camId);

            while (true) {
                int length = in.readInt();

                if (length > 0) {
                    byte[] imageBytes = new byte[length];
                    in.readFully(imageBytes);
                    FrameManager.setFrame(camId, imageBytes);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Cámara desconectada: " + e.getMessage());
        }
    }
}
