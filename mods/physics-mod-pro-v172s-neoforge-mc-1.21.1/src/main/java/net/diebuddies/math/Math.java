/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Random
 */
package net.diebuddies.math;

import net.diebuddies.math.FastRandomSource;
import org.joml.Random;

public class Math {
    public static final double PI = java.lang.Math.PI;
    public static final float INVERT_COLOR = 0.003921569f;
    public static final FastRandomSource fastRandomSource;
    private static final Random random;

    public static double lerpDegree(double first, double second, double target) {
        if (first == second) {
            return first;
        }
        double shortestAngle = ((second - first) % 360.0 + 540.0) % 360.0 - 180.0;
        return first + shortestAngle * target;
    }

    public static double lerpRadians(double first, double second, double target) {
        if (first == second) {
            return first;
        }
        return first + target * Math.wrapRadians(second - first);
    }

    public static double lerpRadians(double first, double second, double maxMovement, double target) {
        if (first == second) {
            return first;
        }
        double movement = target * Math.wrapRadians(second - first);
        if (movement < -maxMovement) {
            movement = -maxMovement;
        } else if (movement > maxMovement) {
            movement = maxMovement;
        }
        return first + movement;
    }

    private static double wrapRadians(double radians) {
        return Math.wrapMinMax(radians, -java.lang.Math.PI, java.lang.Math.PI);
    }

    private static double wrapMax(double x, double max) {
        return (max + x % max) % max;
    }

    private static double wrapMinMax(double x, double min, double max) {
        return min + Math.wrapMax(x - min, max - min);
    }

    public static double getWeight(double first, double second, double target) {
        if (second == first) {
            return 1.0;
        }
        return (target - first) / (second - first);
    }

    public static float getWeight(float first, float second, float target) {
        if (second == first) {
            return 1.0f;
        }
        return (target - first) / (second - first);
    }

    public static double clamp(double val, double min, double max) {
        return java.lang.Math.max(min, java.lang.Math.min(val, max));
    }

    public static byte clamp(int val, byte min, byte max) {
        return (byte)java.lang.Math.max(min, java.lang.Math.min(val, max));
    }

    public static int clamp(int val, int min, int max) {
        return java.lang.Math.max(min, java.lang.Math.min(val, max));
    }

    public static float clamp(float val, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(val, max));
    }

    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * java.lang.Math.PI;
    }

    public static double remap(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
    }

    public static double remapClamp(double value, double oldMin, double oldMax, double newMin, double newMax) {
        if (newMin < newMax) {
            return Math.clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMin, newMax);
        }
        return Math.clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMax, newMin);
    }

    public static float remap(float value, float oldMin, float oldMax, float newMin, float newMax) {
        return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
    }

    public static float remapClamp(float value, float oldMin, float oldMax, float newMin, float newMax) {
        if (newMin < newMax) {
            return Math.clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMin, newMax);
        }
        return Math.clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMax, newMin);
    }

    public static int fastFloor(double x) {
        int xi = (int)x;
        return x < (double)xi ? xi - 1 : xi;
    }

    public static int fastFloor(float x) {
        int xi = (int)x;
        return x < (float)xi ? xi - 1 : xi;
    }

    public static final int fastRound(float x) {
        return Math.fastFloor(x + 0.5f);
    }

    public static final int fastRound(double x) {
        return Math.fastFloor(x + 0.5);
    }

    public static float random() {
        return random.nextFloat();
    }

    public static int randomInt(int size) {
        return random.nextInt(size);
    }

    static {
        random = new Random();
        fastRandomSource = new FastRandomSource();
    }
}

