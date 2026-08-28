/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.util.Mth
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.fog;

import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.sodiumextra.client.fog.FogOverrideState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method={"renderClouds"}, at={@At(value="HEAD")})
    private void setupCloudFog(PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (this.minecraft.level == null) {
            return;
        }
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        float viewDistance = this.minecraft.gameRenderer.getRenderDistance();
        boolean thickFog = this.minecraft.level.effects().isFoggyAt(Mth.floor((double)cameraX), Mth.floor((double)cameraY)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
        FogOverrideState.whileSettingUpCloudFog(() -> FogRenderer.setupFog((Camera)camera, (FogRenderer.FogMode)FogRenderer.FogMode.FOG_TERRAIN, (float)Math.max(viewDistance, 32.0f), (boolean)thickFog, (float)tickDelta));
    }
}

