/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 */
package net.diebuddies.util;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public class PerformanceTracker {
    private static ThreadLocal<PerformanceTracker> threadLocal = ThreadLocal.withInitial(() -> new PerformanceTracker());
    private Object2LongMap<String> tracked = new Object2LongOpenHashMap();
    private Object2LongMap<String> results = new Object2LongOpenHashMap();

    private static PerformanceTracker getInstance() {
        return threadLocal.get();
    }

    public static void start(String identifier) {
        PerformanceTracker tracker = PerformanceTracker.getInstance();
        tracker.results.removeLong((Object)identifier);
        tracker.tracked.put((Object)identifier, System.nanoTime());
    }

    public static void startNoFlush(String identifier) {
        PerformanceTracker.getInstance().tracked.put((Object)identifier, System.nanoTime());
    }

    public static void flush(String identifier) {
        PerformanceTracker.getInstance().results.removeLong((Object)identifier);
    }

    public static void end(String identifier) {
        PerformanceTracker tracker = PerformanceTracker.getInstance();
        long nanoTime = tracker.tracked.getLong((Object)identifier);
        long diff = tracker.results.getOrDefault((Object)identifier, 0L);
        tracker.results.put((Object)identifier, diff + (System.nanoTime() - nanoTime));
    }

    public static double getInMillis(String identifier) {
        return (double)PerformanceTracker.getInstance().results.getOrDefault((Object)identifier, 0L) / 1000000.0;
    }

    public static long getInNanos(String identifier) {
        return PerformanceTracker.getInstance().results.getOrDefault((Object)identifier, 0L);
    }

    public static String getInMillisFormatted(String identifier) {
        return String.format("%.2f", (double)PerformanceTracker.getInstance().results.getOrDefault((Object)identifier, 0L) / 1000000.0);
    }

    public static void flushAll() {
        PerformanceTracker.getInstance().results.clear();
    }
}

