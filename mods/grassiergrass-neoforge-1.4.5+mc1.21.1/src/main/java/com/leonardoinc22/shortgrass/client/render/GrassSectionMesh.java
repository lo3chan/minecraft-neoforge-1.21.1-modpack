/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  org.lwjgl.system.MemoryUtil
 */
package com.leonardoinc22.shortgrass.client.render;

import com.leonardoinc22.shortgrass.client.render.GrassComputeAnimator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.lwjgl.system.MemoryUtil;

final class GrassSectionMesh {
    VertexBuffer buffer;
    VertexBuffer bladeBufferAlt;
    int bladeInputGl;
    GrassComputeAnimator.Layout bladeComputeLayout;
    int bladeVertexCount;
    VertexBuffer plantBuffer;
    VertexBuffer plantBufferAlt;
    int plantInputGl;
    GrassComputeAnimator.Layout plantComputeLayout;
    int plantVertexCount;
    ByteBuffer vertexBytes;
    ByteBuffer plantVertexBytes;
    LightRun[] lightRuns = LightRun.EMPTY;
    LightRun[] plantLightRuns = LightRun.EMPTY;
    int vertexStride;
    int lightOffset;
    int plantVertexStride;
    int plantLightOffset;
    boolean irisMode;
    int lodTier = -1;
    Anim anim = Anim.ANIMATED;
    AABB bounds;
    long builtAtTick;
    long lastLightRefreshTick;

    GrassSectionMesh() {
    }

    boolean isEmpty() {
        return this.buffer == null && this.plantBuffer == null;
    }

    void close() {
        if (this.buffer != null) {
            this.buffer.close();
            this.buffer = null;
        }
        if (this.bladeBufferAlt != null) {
            this.bladeBufferAlt.close();
            this.bladeBufferAlt = null;
        }
        GrassComputeAnimator.deleteBuffer(this.bladeInputGl);
        this.bladeInputGl = 0;
        this.bladeComputeLayout = null;
        if (this.vertexBytes != null) {
            MemoryUtil.memFree((Buffer)this.vertexBytes);
            this.vertexBytes = null;
        }
        if (this.plantBuffer != null) {
            this.plantBuffer.close();
            this.plantBuffer = null;
        }
        if (this.plantBufferAlt != null) {
            this.plantBufferAlt.close();
            this.plantBufferAlt = null;
        }
        GrassComputeAnimator.deleteBuffer(this.plantInputGl);
        this.plantInputGl = 0;
        this.plantComputeLayout = null;
        if (this.plantVertexBytes != null) {
            MemoryUtil.memFree((Buffer)this.plantVertexBytes);
            this.plantVertexBytes = null;
        }
    }

    boolean hasComputeBuffers() {
        return this.bladeInputGl != 0 || this.plantInputGl != 0 || this.bladeBufferAlt != null || this.plantBufferAlt != null;
    }

    void releaseComputeBuffers() {
        if (this.bladeBufferAlt != null) {
            this.bladeBufferAlt.close();
            this.bladeBufferAlt = null;
        }
        GrassComputeAnimator.deleteBuffer(this.bladeInputGl);
        this.bladeInputGl = 0;
        this.bladeComputeLayout = null;
        if (this.plantBufferAlt != null) {
            this.plantBufferAlt.close();
            this.plantBufferAlt = null;
        }
        GrassComputeAnimator.deleteBuffer(this.plantInputGl);
        this.plantInputGl = 0;
        this.plantComputeLayout = null;
        if (this.vertexBytes != null) {
            MemoryUtil.memFree((Buffer)this.vertexBytes);
            this.vertexBytes = null;
        }
        if (this.plantVertexBytes != null) {
            MemoryUtil.memFree((Buffer)this.plantVertexBytes);
            this.plantVertexBytes = null;
        }
    }

    void swapAnimatedBuffers() {
        VertexBuffer blade = this.buffer;
        this.buffer = this.bladeBufferAlt;
        this.bladeBufferAlt = blade;
        VertexBuffer plant = this.plantBuffer;
        this.plantBuffer = this.plantBufferAlt;
        this.plantBufferAlt = plant;
    }

    private static int packLightSample(int localX, int localY, int localZ) {
        return Mth.clamp((int)localY, (int)0, (int)31) << 8 | Mth.clamp((int)localZ, (int)0, (int)15) << 4 | Mth.clamp((int)localX, (int)0, (int)15);
    }

    record LightRun(int startVertex, int vertexCount, int sample) {
        private static final LightRun[] EMPTY = new LightRun[0];
    }

    static enum Anim {
        ANIMATED,
        BAKE_PENDING,
        BAKED;

    }

    static final class LightRunBuilder {
        private final ArrayList<LightRun> runs = new ArrayList();
        private int vertexCount;
        private int currentRun = -1;
        private int currentStart;
        private int currentSample;

        LightRunBuilder() {
        }

        void begin(int localX, int localY, int localZ) {
            this.finish();
            this.currentRun = this.runs.size();
            this.currentStart = this.vertexCount;
            this.currentSample = GrassSectionMesh.packLightSample(localX, localY, localZ);
        }

        void addVertices(int count) {
            this.vertexCount += count;
        }

        void finish() {
            if (this.currentRun < 0) {
                return;
            }
            int count = this.vertexCount - this.currentStart;
            if (count > 0) {
                this.runs.add(new LightRun(this.currentStart, count, this.currentSample));
            }
            this.currentRun = -1;
        }

        LightRun[] toArray() {
            this.finish();
            return this.runs.isEmpty() ? LightRun.EMPTY : (LightRun[])this.runs.toArray(LightRun[]::new);
        }
    }
}

