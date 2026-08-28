/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.batchedentityrendering.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.irisshaders.batchedentityrendering.impl.BufferBuilderExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BufferBuilder.class}, priority=1010)
public class MixinBufferBuilder_SegmentRendering
implements BufferBuilderExt {
    @Final
    @Shadow
    private ByteBufferBuilder buffer;
    @Final
    @Shadow
    private VertexFormat format;
    @Shadow
    private int vertices;
    @Shadow
    @Final
    private int vertexSize;
    @Unique
    private boolean dupeNextVertex;
    @Unique
    private boolean dupeNextVertexAfter;

    @Override
    public void splitStrip() {
        if (this.vertices == 0) {
            return;
        }
        this.duplicateLastVertex();
        this.dupeNextVertexAfter = true;
        this.dupeNextVertex = false;
    }

    @Unique
    private void duplicateLastVertex() {
        long l = this.buffer.reserve(this.vertexSize);
        MemoryIntrinsics.copyMemory((long)(l - (long)this.vertexSize), (long)l, (int)this.vertexSize);
        ++this.vertices;
    }

    @Inject(method={"endLastVertex"}, at={@At(value="RETURN")})
    private void batchedentityrendering$onNext(CallbackInfo ci) {
        if (this.dupeNextVertexAfter) {
            this.dupeNextVertexAfter = false;
            this.dupeNextVertex = true;
            return;
        }
        if (this.dupeNextVertex) {
            this.dupeNextVertex = false;
            this.duplicateLastVertex();
        }
    }
}

