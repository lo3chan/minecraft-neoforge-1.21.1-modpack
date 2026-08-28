/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.math;

public class Color {
    private static final float RANGE = 255.0f;

    public static int packRGBA(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (b & 0xFF) << 16 | (g & 0xFF) << 8 | r & 0xFF;
    }

    public static int packRGBA(float r, float g, float b, float a) {
        return Color.packRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static int packBGRA(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int packBGRA(float r, float g, float b, float a) {
        return Color.packBGRA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }
}

