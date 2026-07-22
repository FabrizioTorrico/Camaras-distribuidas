package org.alumnosinfo.tpdistribuido;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoWriter;

public class VideoSource {
    private final VideoCapture camera;
    private final boolean isFile;

    public VideoSource(int deviceIndex, int width, int height) {
        this(String.valueOf(deviceIndex), width, height);
    }

    public VideoSource(String source, int width, int height) {
        boolean numeric = source != null && source.matches("\\d+");
        if (numeric) {
            int deviceIndex = Integer.parseInt(source);
            camera = new VideoCapture();

            // Intentar abrir con V4L2
            camera.open(deviceIndex, Videoio.CAP_V4L2);
            if (!camera.isOpened()) {
                camera.open(deviceIndex);
            }

            if (camera.isOpened()) {
                // Configurar resolución 320x240 nativa soportada por ambas cámaras (YUYV)
                // camera.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('Y', 'U', 'Y', 'V'));
                camera.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
                camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
                camera.set(Videoio.CAP_PROP_FPS, 30);
                camera.set(Videoio.CAP_PROP_BUFFERSIZE, 1);
            }
            isFile = false;
        } else {
            camera = new VideoCapture(source);
            isFile = true;
        }

        if (!camera.isOpened()) {
            throw new RuntimeException("No se pudo abrir la fuente de video/cámara: " + source);
        }
    }

    public boolean readFrame(Mat frame) {
        boolean success = camera.read(frame) && !frame.empty();
        if (!success && isFile) {
            // Rebobinar el archivo de video cuando termina para reproducirlo en bucle
            camera.set(Videoio.CAP_PROP_POS_FRAMES, 0);
            success = camera.read(frame) && !frame.empty();
        }
        return success;
    }

    public void release() {
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }
}