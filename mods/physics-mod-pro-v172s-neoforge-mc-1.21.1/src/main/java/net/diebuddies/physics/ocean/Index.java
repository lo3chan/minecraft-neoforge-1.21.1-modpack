/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.ocean;

public class Index {
    public static final int xOffset = (int)(Math.log(256.0) / Math.log(2.0));
    public static final int zOffset = (int)(Math.log(16.0) / Math.log(2.0));

    public static long chunk(int x, int y, int z) {
        return ((long)Index.intTo28Bit(x) & 0xFFFFFFFL) << 28 | (long)Index.intTo28Bit(z) & 0xFFFFFFFL | ((long)y & 0xFFL) << 56;
    }

    public static long oceanLayerChunk(int x, int z) {
        return (long)x << 32 | (long)z & 0xFFFFFFFFL;
    }

    public static int getXFromOceanLayer(long index) {
        return (int)(index >> 32);
    }

    public static int getZFromOceanLayer(long index) {
        return (int)index;
    }

    private static int intTo28Bit(int value) {
        return value & 0xFFFFFFF;
    }

    private static int intFrom28Bit(int bit28) {
        return (bit28 & 0xF000000) << 4 | bit28;
    }

    public static int getXFromIndexChunk(long index) {
        return Index.intFrom28Bit((int)(index >> 28 & 0xFFFFFFFL));
    }

    public static int getYFromIndexChunk(long index) {
        return (byte)(index >> 56 & 0xFFL);
    }

    public static int getZFromIndexChunk(long index) {
        return Index.intFrom28Bit((int)(index & 0xFFFFFFFL));
    }

    public static int chunkStorage(int x, int y, int z) {
        return y << xOffset | z << zOffset | x;
    }

    public static int chunkStorage(int x, int z) {
        return z << zOffset | x;
    }
}

