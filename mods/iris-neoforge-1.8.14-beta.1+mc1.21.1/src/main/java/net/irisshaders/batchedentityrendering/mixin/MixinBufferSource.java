/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  java.util.SequencedMap
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.SequencedMap;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={MultiBufferSource.BufferSource.class})
public class MixinBufferSource
implements MemoryTrackingBuffer {
    @Shadow
    @Final
    protected ByteBufferBuilder sharedBuffer;
    @Shadow
    @Final
    protected SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;

    @Override
    public long getAllocatedSize() {
        long allocatedSize = ((MemoryTrackingBuffer)this.sharedBuffer).getAllocatedSize();
        for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
            allocatedSize += ((MemoryTrackingBuffer)builder).getAllocatedSize();
        }
        return allocatedSize;
    }

    @Override
    public long getUsedSize() {
        long allocatedSize = ((MemoryTrackingBuffer)this.sharedBuffer).getUsedSize();
        for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
            allocatedSize += ((MemoryTrackingBuffer)builder).getUsedSize();
        }
        return allocatedSize;
    }

    @Override
    public void freeAndDeleteBuffer() {
        ((MemoryTrackingBuffer)this.sharedBuffer).freeAndDeleteBuffer();
        for (ByteBufferBuilder builder : this.fixedBuffers.values()) {
            ((MemoryTrackingBuffer)builder).freeAndDeleteBuffer();
        }
    }
}

