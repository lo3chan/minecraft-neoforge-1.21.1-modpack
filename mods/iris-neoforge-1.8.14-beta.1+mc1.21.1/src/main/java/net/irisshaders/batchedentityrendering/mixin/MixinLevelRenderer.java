/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.batchedentityrendering.mixin;

import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class}, priority=999)
public class MixinLevelRenderer {
    @Unique
    private static final String RENDER_ENTITY = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V";
    @Shadow
    private RenderBuffers renderBuffers;
    @Unique
    private Groupable groupable;

    @Inject(method={"renderLevel"}, at={@At(value="HEAD")})
    private void batchedentityrendering$beginLevelRender(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (this.renderBuffers instanceof DrawCallTrackingRenderBuffers) {
            ((DrawCallTrackingRenderBuffers)this.renderBuffers).resetDrawCounts();
        }
        ((RenderBuffersExt)this.renderBuffers).beginLevelRendering();
        MultiBufferSource.BufferSource provider = this.renderBuffers.bufferSource();
        if (provider instanceof Groupable) {
            this.groupable = (Groupable)provider;
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V")})
    private void batchedentityrendering$preRenderEntity(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (this.groupable != null) {
            this.groupable.startGroup();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V", shift=At.Shift.AFTER)})
    private void batchedentityrendering$postRenderEntity(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (this.groupable != null) {
            this.groupable.endGroup();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"})})
    private void batchedentityrendering$beginTranslucents(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        if (bufferSource instanceof FullyBufferedMultiBufferSource) {
            FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource = (FullyBufferedMultiBufferSource)bufferSource;
            fullyBufferedMultiBufferSource.readyUp();
        }
        if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
            Minecraft.getInstance().getProfiler().popPush("entity_draws_opaque");
            bufferSource = this.renderBuffers.bufferSource();
            if (bufferSource instanceof FullyBufferedMultiBufferSource) {
                FullyBufferedMultiBufferSource source = (FullyBufferedMultiBufferSource)bufferSource;
                source.endBatchWithType(TransparencyType.OPAQUE);
                source.endBatchWithType(TransparencyType.OPAQUE_DECAL);
                source.endBatchWithType(TransparencyType.WATER_MASK);
            } else {
                this.renderBuffers.bufferSource().endBatch();
            }
        } else {
            Minecraft.getInstance().getProfiler().popPush("entity_draws");
            this.renderBuffers.bufferSource().endBatch();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="CONSTANT", args={"stringValue=translucent"}, shift=At.Shift.AFTER)})
    private void batchedentityrendering$endTranslucents(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
            this.renderBuffers.bufferSource().endBatch();
        }
    }

    @Inject(method={"renderLevel"}, at={@At(value="RETURN")})
    private void batchedentityrendering$endLevelRender(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        ((RenderBuffersExt)this.renderBuffers).endLevelRendering();
        this.groupable = null;
    }
}

