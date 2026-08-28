/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.snow.thread;

import net.diebuddies.physics.snow.ChunkContouring;

public interface ChunkCreator {
    public int getX();

    public int getY();

    public int getZ();

    public ChunkContouring create();
}

