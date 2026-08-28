/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.debug.DebugRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sonicether.soundphysics.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonicether.soundphysics.debug.RaycastRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={DebugRenderer.class})
public class DebugRendererMixin {
    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void onDrawBlockOutline(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double x, double y, double z, CallbackInfo ci) {
        RaycastRenderer.renderRays(poseStack, bufferSource, x, y, z);
    }
}

