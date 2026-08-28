/*
 * Decompiled with CFR 0.152.
 */
package me.flashyreese.mods.sodiumextra.client;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class FrameCounter {
    private static final FrameCounter INSTANCE = new FrameCounter();
    private final Deque<FrameSample> samples = new ArrayDeque<FrameSample>();
    private final long smoothWindow = 500000000L;
    private final long windowNanos = 5000000000L;
    private final long updateIntervalNanos = 500000000L;
    private long lastFrameTime = -1L;
    private long lastUpdateTime = 0L;
    private double cachedSmoothFps = 0.0;
    private double cachedAverageFps = 0.0;
    private double cachedOnePercentLowFps = 0.0;
    private double cachedPointOnePercentLowFps = 0.0;

    public static FrameCounter getInstance() {
        return INSTANCE;
    }

    public synchronized void onFrame() {
        long now = System.nanoTime();
        if (this.lastFrameTime != -1L) {
            long delta = now - this.lastFrameTime;
            this.samples.addLast(new FrameSample(now, delta));
        }
        this.lastFrameTime = now;
        while (!this.samples.isEmpty() && now - this.samples.peekFirst().timestamp > this.windowNanos) {
            this.samples.removeFirst();
        }
        Objects.requireNonNull(this);
        if (now - this.lastUpdateTime >= 500000000L) {
            this.lastUpdateTime = now;
            if (!this.samples.isEmpty()) {
                long totalNanos = this.samples.stream().mapToLong(s -> s.deltaNanos).sum();
                this.cachedAverageFps = (double)((long)this.samples.size() * 1000000000L) / (double)totalNanos;
                this.cachedOnePercentLowFps = this.computePercentileLow(1.0);
                this.cachedPointOnePercentLowFps = this.computePercentileLow(0.1);
                this.cachedSmoothFps = this.computeSmoothFpsFromRecentFrames(now);
            } else {
                this.cachedSmoothFps = 0.0;
                this.cachedPointOnePercentLowFps = 0.0;
                this.cachedOnePercentLowFps = 0.0;
                this.cachedAverageFps = 0.0;
            }
        }
    }

    private double computeSmoothFpsFromRecentFrames(long now) {
        List<Long> recent = this.samples.stream().filter(s -> {
            long l = now - s.timestamp;
            Objects.requireNonNull(this);
            return l <= 500000000L;
        }).map(s -> s.deltaNanos).toList();
        if (recent.isEmpty()) {
            return 0.0;
        }
        double avgDelta = recent.stream().mapToLong(Long::longValue).average().orElse(0.0);
        return 1.0E9 / avgDelta;
    }

    public synchronized int getSmoothFps() {
        return (int)Math.round(this.cachedSmoothFps);
    }

    public synchronized int getAverageFps() {
        return (int)Math.round(this.cachedAverageFps);
    }

    public synchronized int getOnePercentLowFps() {
        return (int)Math.round(this.cachedOnePercentLowFps);
    }

    public synchronized int getPointOnePercentLowFps() {
        return (int)Math.round(this.cachedPointOnePercentLowFps);
    }

    private double computePercentileLow(double percent) {
        if (this.samples.isEmpty()) {
            return 0.0;
        }
        List<Long> deltas = this.samples.stream().map(s -> s.deltaNanos).sorted(Comparator.reverseOrder()).toList();
        int count = Math.max(1, (int)Math.ceil((double)deltas.size() * (percent / 100.0)));
        long sum = 0L;
        for (int i = 0; i < count; ++i) {
            sum += deltas.get(i).longValue();
        }
        double avgDelta = (double)sum / (double)count;
        return 1.0E9 / avgDelta;
    }

    private record FrameSample(long timestamp, long deltaNanos) {
    }
}

