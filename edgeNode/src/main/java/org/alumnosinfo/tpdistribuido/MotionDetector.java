package org.alumnosinfo.tpdistribuido;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class MotionDetector {
    private static final double MIN_CONTOUR_AREA = 500.0;
    private static final int MOVEMENT_THRESHOLD = 25;
    
    private final Mat prevGray = new Mat();
    private final Mat gray = new Mat();
    private final Mat diff = new Mat();

    public boolean detectMotion(Mat frame, EdgeStateManager state) {
        // Limpieza si venimos de Estado 2 (Streaming) a Estado 1 (Análisis)
        if (state.isResetPrevGray()) {
            if (!prevGray.empty()) prevGray.release();
            state.setResetPrevGray(false);
        }

        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

        if (prevGray.empty()) {
            gray.copyTo(prevGray);
            return false; 
        }

        Core.absdiff(prevGray, gray, diff);
        Imgproc.threshold(diff, diff, MOVEMENT_THRESHOLD, 255, Imgproc.THRESH_BINARY);
        Imgproc.dilate(diff, diff, new Mat(), new Point(-1, -1), 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(diff, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        boolean motionDetected = false;
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > MIN_CONTOUR_AREA) {
                motionDetected = true;
                break;
            }
        }

        // Actualizar el frame previo para la próxima iteración
        gray.copyTo(prevGray);
        return motionDetected;
    }
}