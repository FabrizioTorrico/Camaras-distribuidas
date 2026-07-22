package org.alumnosinfo.tpdistribuido;

public class EdgeStateManager {
    private volatile boolean streamingMode = false;
    private volatile boolean resetPrevGray = false;

    public boolean isStreamingMode() {
        return streamingMode;
    }

    public void setStreamingMode(boolean streamingMode) {
        this.streamingMode = streamingMode;
    }

    public boolean isResetPrevGray() {
        return resetPrevGray;
    }

    public void setResetPrevGray(boolean resetPrevGray) {
        this.resetPrevGray = resetPrevGray;
    }
}