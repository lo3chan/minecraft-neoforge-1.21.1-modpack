/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.irisshaders.iris.vertices.MojangBufferAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ByteBufferBuilder.class})
public class MixinByteBufferBuilder
implements MojangBufferAccessor {
    @Shadow
    long pointer;

    @Override
    public long getPointer() {
        return this.pointer;
    }
}

