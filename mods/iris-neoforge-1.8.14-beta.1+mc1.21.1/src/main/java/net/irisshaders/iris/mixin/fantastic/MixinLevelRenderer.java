/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 */
package net.irisshaders.iris.mixin.fantastic;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.properties.ParticleRenderingSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LevelRenderer.class})
public abstract class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private RenderBuffers renderBuffers;

    @Shadow
    public abstract Frustum getFrustum();

    @WrapOperation(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V", ordinal=1)})
    private void redirectSolidParticles(ParticleEngine instance, LightTexture lightTexture, Camera camera, float v, Frustum frustum, Predicate<ParticleRenderType> predicate, Operation<Void> original) {
        ParticleRenderingSettings settings = this.getRenderingSettings();
        Predicate<ParticleRenderType> newPredicate = predicate;
        if (settings == ParticleRenderingSettings.BEFORE) {
            newPredicate = t -> true;
        } else if (settings == ParticleRenderingSettings.AFTER) {
            return;
        }
        original.call(new Object[]{instance, lightTexture, camera, Float.valueOf(v), frustum, newPredicate});
    }

    @WrapOperation(method={"renderLevel"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V", ordinal=2)})
    private void redirectTransParticles(ParticleEngine instance, LightTexture lightTexture, Camera camera, float v, Frustum frustum, Predicate<ParticleRenderType> predicate, Operation<Void> original) {
        ParticleRenderingSettings settings = this.getRenderingSettings();
        Predicate<ParticleRenderType> newPredicate = predicate;
        if (settings == ParticleRenderingSettings.BEFORE) {
            return;
        }
        if (settings == ParticleRenderingSettings.AFTER) {
            newPredicate = t -> true;
        }
        original.call(new Object[]{instance, lightTexture, camera, Float.valueOf(v), frustum, newPredicate});
    }

    private ParticleRenderingSettings getRenderingSettings() {
        return Iris.getPipelineManager().getPipeline().map(WorldRenderingPipeline::getParticleRenderingSettings).orElse(ParticleRenderingSettings.MIXED);
    }
}

