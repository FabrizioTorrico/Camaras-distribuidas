package org.alumnosinfo.tpdistribuido;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FrameProcessor implements Runnable {
    private final String camId;
    private final BlockingQueue<byte[]> frameQueue;
    private final CameraRegistry registry;

    public FrameProcessor(String camId, BlockingQueue<byte[]> frameQueue, CameraRegistry registry) {
        this.camId = camId;
        this.frameQueue = frameQueue;
        this.registry = registry;
    }

    @Override
    public void run() {
        Mat prevGray = new Mat();
        
        while (!Thread.currentThread().isInterrupted()) {
            try {
                byte[] imageBytes = frameQueue.take();
                Mat frame = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
                
                if (frame != null && !frame.empty()) {
                    Mat gray = new Mat();
                    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

                    if (prevGray.empty()) {
                        gray.copyTo(prevGray);
                    } else {
                        Mat diff = new Mat();
                        Core.absdiff(prevGray, gray, diff);
                        Imgproc.threshold(diff, diff, 25, 255, Imgproc.THRESH_BINARY);
                        Imgproc.dilate(diff, diff, new Mat(), new Point(-1, -1), 2);

                        List<MatOfPoint> contours = new ArrayList<>();
                        Mat hierarchy = new Mat();
                        Imgproc.findContours(diff, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                        boolean motionDetected = false;
                        for (MatOfPoint contour : contours) {
                            if (Imgproc.contourArea(contour) > 500.0) {
                                motionDetected = true;
                                Rect rect = Imgproc.boundingRect(contour);
                                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
                            }
                        }

                        gray.copyTo(prevGray);

                        if (motionDetected) {
                            registry.recordMotion(camId);
                            Imgproc.putText(frame, "MOVIMIENTO DETECTADO", new Point(10, 20), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255), 2);
                        }
                        
                        // Guardar la imagen procesada
                        MatOfByte buffer = new MatOfByte();
                        Imgcodecs.imencode(".jpg", frame, buffer);
                        registry.setFrame(camId, buffer.toArray());
                    }
                    gray.release();
                } else {
                     registry.setFrame(camId, imageBytes); // Fallback
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        prevGray.release();
    }
}