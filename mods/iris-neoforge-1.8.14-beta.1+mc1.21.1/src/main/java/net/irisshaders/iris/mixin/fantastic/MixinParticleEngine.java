/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  javax.annotation.Nullable
 *  net.minecraft.client.Camera
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.culling.Frustum
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package net.irisshaders.iris.mixin.fantastic;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.irisshaders.iris.pipeline.programs.ShaderAccess;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ParticleEngine.class})
public class MixinParticleEngine {
    @Redirect(method={"Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"))
    private void iris$changeParticleShader(Supplier<ShaderInstance> pSupplier0, LightTexture p_107339_, Camera p_107340_, float p_107341_, @Nullable Frustum frustum, Predicate<ParticleRenderType> renderTypePredicate) {
        RenderSystem.setShader(!renderTypePredicate.test(ParticleRenderType.PARTICLE_SHEET_OPAQUE) ? ShaderAccess::getParticleTranslucentShader : pSupplier0);
    }
}

