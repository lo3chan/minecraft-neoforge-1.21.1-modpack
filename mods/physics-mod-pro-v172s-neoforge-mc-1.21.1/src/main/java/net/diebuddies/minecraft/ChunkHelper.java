/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 */
package net.diebuddies.minecraft;

import net.minecraft.core.SectionPos;

public class ChunkHelper {
    public static long calcChunkIndex(int chunkX, int chunkZ) {
        return ((long)chunkX & 0xFFFFFFFFL) << 32 | (long)chunkZ & 0xFFFFFFFFL;
    }

    public static int getChunkX(long index) {
        return (int)(index >> 32) & 0xFFFFFFFF;
    }

    public static int getChunkZ(long index) {
        return (int)index & 0xFFFFFFFF;
    }

    public static long calcIndex(int x, int y, int z) {
        return (long)x << 60 | (long)z << 56 | (long)y & 0xFFFFFFFFL;
    }

    public static long calcRelativeIndex(int x, int y, int z) {
        return (long)SectionPos.sectionRelative((int)x) << 60 | (long)SectionPos.sectionRelative((int)z) << 56 | (long)y & 0xFFFFFFFFL;
    }

    public static int calcBlockX(long index) {
        return (int)(index >> 60) & 0xF;
    }

    public static int calcBlockY(long index) {
        return (int)(index & 0xFFFFFFFFL);
    }

    public static int calcBlockZ(long index) {
        return (int)(index >> 56) & 0xF;
    }

    public static void main(String[] args) {
        int x = 13;
        int y = 42845;
        int z = 9;
        long index = ChunkHelper.calcIndex(x, y, z);
        System.out.println(ChunkHelper.calcBlockX(index) + ", " + ChunkHelper.calcBlockY(index) + ", " + ChunkHelper.calcBlockZ(index));
    }
}

