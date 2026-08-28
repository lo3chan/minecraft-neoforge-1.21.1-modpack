/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.FogRenderer
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.client.renderer.FogRenderer$MobEffectFogFunction
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.material.FogType
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.fog;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import me.flashyreese.mods.sodiumextra.client.fog.FogOverrideState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FogRenderer.class})
public abstract class MixinFogRenderer {
    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float tickDelta) {
        return null;
    }

    @Inject(method={"setupFog"}, at={@At(value="TAIL")})
    private static void sodiumExtra$applyFog(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        FogRenderer.MobEffectFogFunction mobEffectFogFunction;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        SodiumExtraGameOptions.AtmosphericFogSettings settings = FogDistanceHelper.getAtmosphericSettings(minecraft.level);
        if (FogOverrideState.isSettingUpCloudFog()) {
            MixinFogRenderer.sodiumExtra$applyCloudFog(settings);
            return;
        }
        Entity entity = camera.getEntity();
        FogType fluid = camera.getFluidInCamera();
        if (MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(fluid, mobEffectFogFunction = MixinFogRenderer.getPriorityFogFunction(entity, tickDelta), fogMode)) {
            return;
        }
        if (fluid != FogType.NONE || mobEffectFogFunction != null || FogDistanceHelper.isBossFogActive()) {
            return;
        }
        if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            MixinFogRenderer.sodiumExtra$applySkyFog(settings, viewDistance);
            return;
        }
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN || thickFog) {
            MixinFogRenderer.sodiumExtra$applyTerrainFog(settings);
        }
    }

    private static boolean sodiumExtra$applyProtectedGameplayFog(FogType fluid, @Nullable FogRenderer.MobEffectFogFunction mobEffectFogFunction, FogRenderer.FogMode fogMode) {
        if (!FogDistanceHelper.shouldModifyProtectedGameplayFog()) {
            return false;
        }
        if (fluid == FogType.LAVA) {
            MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.LAVA, fogMode, 0.25f, 1.0f);
            return true;
        }
        if (fluid == FogType.POWDER_SNOW) {
            MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.POWDER_SNOW, fogMode, 0.0f, 1.0f);
            return true;
        }
        if (fluid == FogType.WATER) {
            MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.WATER, fogMode, 0.0f, 1.0f);
            return true;
        }
        if (mobEffectFogFunction == null) {
            return false;
        }
        if (MobEffects.BLINDNESS.equals((Object)mobEffectFogFunction.getMobEffect())) {
            MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.BLINDNESS, fogMode, 0.25f, 0.8f);
            return true;
        }
        if (MobEffects.DARKNESS.equals((Object)mobEffectFogFunction.getMobEffect())) {
            MixinFogRenderer.sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.DARKNESS, fogMode, 0.75f, 1.0f);
            return true;
        }
        return false;
    }

    private static void sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType type, FogRenderer.FogMode fogMode, float terrainStartMultiplier, float skyEndMultiplier) {
        int distanceBlocks = FogDistanceHelper.getProtectedGameplayFogDistance(type);
        if (fogMode == FogRenderer.FogMode.FOG_SKY) {
            FogDistanceHelper.applyProtectedGameplayFog(distanceBlocks, 0.0f, skyEndMultiplier);
        } else {
            FogDistanceHelper.applyProtectedGameplayFog(distanceBlocks, terrainStartMultiplier, 1.0f);
        }
    }

    private static void sodiumExtra$applySkyFog(SodiumExtraGameOptions.AtmosphericFogSettings settings, float viewDistance) {
        int fogDistance = settings.distanceChunks;
        if (fogDistance == 0) {
            return;
        }
        if (FogDistanceHelper.disablesFog(fogDistance)) {
            return;
        }
        RenderSystem.setShaderFogStart((float)0.0f);
        RenderSystem.setShaderFogEnd((float)Math.min(FogDistanceHelper.getEnd(fogDistance), viewDistance));
    }

    private static void sodiumExtra$applyTerrainFog(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
        int fogDistance = settings.distanceChunks;
        if (fogDistance == 0) {
            float start = FogDistanceHelper.applyStartMultiplier(RenderSystem.getShaderFogStart(), settings);
            float end = RenderSystem.getShaderFogEnd();
            RenderSystem.setShaderFogStart((float)start);
            RenderSystem.setShaderFogEnd((float)end);
            FogDistanceHelper.applyRenderDistanceShape(start, end, settings);
            return;
        }
        if (FogDistanceHelper.disablesFog(fogDistance)) {
            RenderSystem.setShaderFogStart((float)Float.MAX_VALUE);
            RenderSystem.setShaderFogEnd((float)Float.MAX_VALUE);
            return;
        }
        float start = FogDistanceHelper.getStart(settings);
        float end = FogDistanceHelper.getEnd(fogDistance);
        RenderSystem.setShaderFogStart((float)start);
        RenderSystem.setShaderFogEnd((float)end);
        FogDistanceHelper.applyRenderDistanceShape(start, end, settings);
    }

    private static void sodiumExtra$applyCloudFog(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
        if (settings.cloudFogPercent == 100) {
            return;
        }
        RenderSystem.setShaderFogEnd((float)Math.min(RenderSystem.getShaderFogEnd(), FogDistanceHelper.getCloudEnd(settings.cloudFogPercent)));
    }
}

