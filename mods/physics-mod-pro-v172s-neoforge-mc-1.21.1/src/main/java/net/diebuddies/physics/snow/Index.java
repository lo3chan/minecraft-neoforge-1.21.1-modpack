/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3i
 */
package net.diebuddies.physics.snow;

import net.diebuddies.physics.snow.IChunk;
import org.joml.Vector3i;

public class Index {
    public static int xOffset;
    public static int zOffset;
    public static int bitmask;
    public static final int vanillaXOffset = 8;
    public static final int vanillaZOffset = 4;

    public static void updateMasks() {
        xOffset = (int)(Math.log(IChunk.CHUNK_SIZE * IChunk.CHUNK_SIZE) / Math.log(2.0));
        zOffset = (int)(Math.log(IChunk.CHUNK_SIZE) / Math.log(2.0));
        bitmask = (int)Math.round(Math.pow(2.0, zOffset)) - 1;
    }

    public static long chunk(int x, int y, int z) {
        return ((long)Index.intTo28Bit(x) & 0xFFFFFFFL) << 28 | (long)Index.intTo28Bit(z) & 0xFFFFFFFL | ((long)y & 0xFFL) << 56;
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

    public static Vector3i getPosFromIndexChunk(long index, Vector3i dst) {
        return dst.set(Index.getXFromIndexChunk(index), Index.getYFromIndexChunk(index), Index.getZFromIndexChunk(index));
    }

    public static int chunkStorage(int x, int y, int z) {
        return x << xOffset | z << zOffset | y;
    }

    public static int vanillaChunkStorage(int x, int y, int z) {
        return x << 8 | z << 4 | y;
    }

    public static int chunkStorageXPos(int index) {
        return index >> xOffset & bitmask;
    }

    public static int chunkStorageYPos(int index) {
        return index & bitmask;
    }

    public static int chunkStorageZPos(int index) {
        return index >> zOffset & bitmask;
    }

    public static int chunkStorage(int lod, int x, int y, int z) {
        return x << xOffset - lod * 2 | z << zOffset - lod | y;
    }

    static {
        Index.updateMasks();
    }
}

