/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin.vertices.immediate;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MultiBufferSource.BufferSource.class})
public class MixinBufferSource {
    @WrapOperation(method={"getBuffer"}, at={@At(value="NEW", target="(Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;")})
    private BufferBuilder iris$redirectBegin(ByteBufferBuilder byteBufferBuilder, VertexFormat.Mode mode, VertexFormat vertexFormat, Operation<BufferBuilder> original) {
        ImmediateState.skipExtension.set(this.iris$notRenderingLevel());
        BufferBuilder builder = (BufferBuilder)original.call(new Object[]{byteBufferBuilder, mode, vertexFormat});
        ImmediateState.skipExtension.set(false);
        return builder;
    }

    @Inject(method={"endBatch(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/BufferBuilder;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderType;draw(Lcom/mojang/blaze3d/vertex/MeshData;)V")})
    private void iris$beforeFlushBuffer(RenderType renderType, BufferBuilder bufferBuilder, CallbackInfo ci) {
        if (this.iris$notRenderingLevel()) {
            ImmediateState.renderWithExtendedVertexFormat = false;
        }
    }

    @Inject(method={"endBatch(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/BufferBuilder;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderType;draw(Lcom/mojang/blaze3d/vertex/MeshData;)V", shift=At.Shift.AFTER)})
    private void iris$afterFlushBuffer(RenderType renderType, BufferBuilder bufferBuilder, CallbackInfo ci) {
        if (this.iris$notRenderingLevel()) {
            ImmediateState.renderWithExtendedVertexFormat = true;
        }
    }

    @Unique
    private boolean iris$notRenderingLevel() {
        return !ImmediateState.isRenderingLevel;
    }
}

