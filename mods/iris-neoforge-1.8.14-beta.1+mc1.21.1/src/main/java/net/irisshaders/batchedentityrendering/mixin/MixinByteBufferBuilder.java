/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.irisshaders.batchedentityrendering.impl.MemoryTrackingBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ByteBufferBuilder.class})
public abstract class MixinByteBufferBuilder
implements MemoryTrackingBuffer {
    @Shadow
    private int capacity;
    @Shadow
    private int writeOffset;

    @Shadow
    public abstract void close();

    @Override
    public long getAllocatedSize() {
        return this.capacity;
    }

    @Override
    public long getUsedSize() {
        return this.writeOffset;
    }

    @Override
    public void freeAndDeleteBuffer() {
        this.close();
    }
}

