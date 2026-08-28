/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.core.Holder
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.material.FogType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.irisshaders.iris.mixin;

import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FogRenderer.class})
public class MixinFogRenderer {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method={"setupFog"}, at={@At(value="HEAD")})
    private static void iris$setupLegacyWaterFog(Camera camera, FogRenderer.FogMode $$1, float $$2, boolean $$3, float $$4, CallbackInfo ci) {
        if (camera.getFluidInCamera() == FogType.WATER) {
            Entity entity = camera.getEntity();
            float density = 0.05f;
            if (entity instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer)entity;
                density -= localPlayer.getWaterVision() * localPlayer.getWaterVision() * 0.03f;
                Holder biome = localPlayer.level().getBiome(localPlayer.blockPosition());
                if (biome.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    density += 0.005f;
                }
            }
            CapturedRenderingState.INSTANCE.setFogDensity(density);
        } else {
            CapturedRenderingState.INSTANCE.setFogDensity(-1.0f);
        }
    }

    @Inject(method={"setupColor"}, at={@At(value="TAIL")})
    private static void render(Camera camera, float tickDelta, ClientLevel level, int i, float f, CallbackInfo ci) {
        CapturedRenderingState.INSTANCE.setFogColor(fogRed, fogGreen, fogBlue);
    }
}

