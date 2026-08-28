/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics.snow;

import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.thread.MultipleEvent;

public class ChunkRenderUpdate {
    public MultipleEvent event;
    public ChunkContouring chunk;
    public double distance;
    public boolean dataChanged;

    public ChunkRenderUpdate(double distance, ChunkContouring chunk) {
        this.distance = distance;
        this.chunk = chunk;
    }

    public int hashCode() {
        return this.chunk.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ChunkRenderUpdate other = (ChunkRenderUpdate)obj;
        return !(this.chunk == null ? other.chunk != null : !this.chunk.equals(other.chunk));
    }
}

