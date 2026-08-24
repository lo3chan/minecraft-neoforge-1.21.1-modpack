package me.flashyreese.mods.sodiumextra.mixin.fog;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import me.flashyreese.mods.sodiumextra.client.fog.FogOverrideState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FogRenderer.class})
public abstract class MixinFogRenderer {
   @Shadow
   @Nullable
   private static MobEffectFogFunction getPriorityFogFunction(Entity entity, float tickDelta) {
      return null;
   }

   @Inject(
      method = {"setupFog"},
      at = {@At("TAIL")}
   )
   private static void sodiumExtra$applyFog(Camera camera, FogMode fogMode, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         SodiumExtraGameOptions.AtmosphericFogSettings settings = FogDistanceHelper.getAtmosphericSettings(minecraft.level);
         if (FogOverrideState.isSettingUpCloudFog()) {
            sodiumExtra$applyCloudFog(settings);
         } else {
            Entity entity = camera.getEntity();
            FogType fluid = camera.getFluidInCamera();
            MobEffectFogFunction mobEffectFogFunction = getPriorityFogFunction(entity, tickDelta);
            if (!sodiumExtra$applyProtectedGameplayFog(fluid, mobEffectFogFunction, fogMode)) {
               if (fluid == FogType.NONE && mobEffectFogFunction == null && !FogDistanceHelper.isBossFogActive()) {
                  if (fogMode == FogMode.FOG_SKY) {
                     sodiumExtra$applySkyFog(settings, viewDistance);
                  } else {
                     if (fogMode == FogMode.FOG_TERRAIN || thickFog) {
                        sodiumExtra$applyTerrainFog(settings);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean sodiumExtra$applyProtectedGameplayFog(FogType fluid, @Nullable MobEffectFogFunction mobEffectFogFunction, FogMode fogMode) {
      if (!FogDistanceHelper.shouldModifyProtectedGameplayFog()) {
         return false;
      } else if (fluid == FogType.LAVA) {
         sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.LAVA, fogMode, 0.25F, 1.0F);
         return true;
      } else if (fluid == FogType.POWDER_SNOW) {
         sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.POWDER_SNOW, fogMode, 0.0F, 1.0F);
         return true;
      } else if (fluid == FogType.WATER) {
         sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.WATER, fogMode, 0.0F, 1.0F);
         return true;
      } else if (mobEffectFogFunction == null) {
         return false;
      } else if (MobEffects.BLINDNESS.equals(mobEffectFogFunction.getMobEffect())) {
         sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.BLINDNESS, fogMode, 0.25F, 0.8F);
         return true;
      } else if (MobEffects.DARKNESS.equals(mobEffectFogFunction.getMobEffect())) {
         sodiumExtra$applyProtectedGameplayFog(FogDistanceHelper.ProtectedFogType.DARKNESS, fogMode, 0.75F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private static void sodiumExtra$applyProtectedGameplayFog(
      FogDistanceHelper.ProtectedFogType type, FogMode fogMode, float terrainStartMultiplier, float skyEndMultiplier
   ) {
      int distanceBlocks = FogDistanceHelper.getProtectedGameplayFogDistance(type);
      if (fogMode == FogMode.FOG_SKY) {
         FogDistanceHelper.applyProtectedGameplayFog(distanceBlocks, 0.0F, skyEndMultiplier);
      } else {
         FogDistanceHelper.applyProtectedGameplayFog(distanceBlocks, terrainStartMultiplier, 1.0F);
      }
   }

   private static void sodiumExtra$applySkyFog(SodiumExtraGameOptions.AtmosphericFogSettings settings, float viewDistance) {
      int fogDistance = settings.distanceChunks;
      if (fogDistance != 0) {
         if (!FogDistanceHelper.disablesFog(fogDistance)) {
            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(Math.min(FogDistanceHelper.getEnd(fogDistance), viewDistance));
         }
      }
   }

   private static void sodiumExtra$applyTerrainFog(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
      int fogDistance = settings.distanceChunks;
      if (fogDistance == 0) {
         float start = FogDistanceHelper.applyStartMultiplier(RenderSystem.getShaderFogStart(), settings);
         float end = RenderSystem.getShaderFogEnd();
         RenderSystem.setShaderFogStart(start);
         RenderSystem.setShaderFogEnd(end);
         FogDistanceHelper.applyRenderDistanceShape(start, end, settings);
      } else if (FogDistanceHelper.disablesFog(fogDistance)) {
         RenderSystem.setShaderFogStart(3.4028235E38F);
         RenderSystem.setShaderFogEnd(3.4028235E38F);
      } else {
         float start = FogDistanceHelper.getStart(settings);
         float end = FogDistanceHelper.getEnd(fogDistance);
         RenderSystem.setShaderFogStart(start);
         RenderSystem.setShaderFogEnd(end);
         FogDistanceHelper.applyRenderDistanceShape(start, end, settings);
      }
   }

   private static void sodiumExtra$applyCloudFog(SodiumExtraGameOptions.AtmosphericFogSettings settings) {
      if (settings.cloudFogPercent != 100) {
         RenderSystem.setShaderFogEnd(Math.min(RenderSystem.getShaderFogEnd(), FogDistanceHelper.getCloudEnd(settings.cloudFogPercent)));
      }
   }
}
