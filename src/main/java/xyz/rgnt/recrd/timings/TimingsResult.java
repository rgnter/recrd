package xyz.rgnt.recrd.timings;

public class TimingsResult {

    private final long nanoTime;

    public TimingsResult(long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public double getAsSeconds() {
        return nanoTime / 1e-9;
    }

    public double getAsMilli() {
        return nanoTime /  1e-6;
    }

    public long getAsNano() {
        return nanoTime;
    }
}
