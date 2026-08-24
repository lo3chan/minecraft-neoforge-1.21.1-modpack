package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.client.renderer.level.AetherSkyRenderEffects;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;

public class DimensionClientHooks {
   @Nullable
   public static Float renderNearFog(Camera camera, FogMode mode, float farDistance) {
      if (camera.getEntity().level() instanceof ClientLevel clientLevel && clientLevel.effects() instanceof AetherSkyRenderEffects) {
         FogType fluidState = camera.getFluidInCamera();
         if (mode == FogMode.FOG_TERRAIN && fluidState == FogType.NONE) {
            return farDistance / 2.0F;
         }
      }

      return null;
   }

   @Nullable
   public static Float reduceLavaFog(Camera camera, float nearDistance) {
      if (camera.getEntity().level() instanceof ClientLevel
         && camera.getEntity() instanceof LivingEntity livingEntity
         && EquipmentUtil.hasFullPhoenixSet(livingEntity)) {
         FogType fluidState = camera.getFluidInCamera();
         if (fluidState == FogType.LAVA) {
            return nearDistance * 5.0F;
         }
      }

      return null;
   }

   @Nullable
   public static Triple<Float, Float, Float> renderFogColors(Camera camera, float red, float green, float blue) {
      if (camera.getEntity().level() instanceof ClientLevel clientLevel && clientLevel.effects() instanceof AetherSkyRenderEffects) {
         ClientLevelData worldInfo = clientLevel.getLevelData();
         double d0 = (camera.getPosition().y() - clientLevel.getMinBuildHeight()) * worldInfo.getClearColorScale();
         FogType fluidState = camera.getFluidInCamera();
         if (d0 < 1.0 && fluidState != FogType.LAVA) {
            if (d0 < 0.0) {
               d0 = 0.0;
            }

            d0 *= d0;
            if (d0 != 0.0) {
               return Triple.of((float)(red / d0), (float)(green / d0), (float)(blue / d0));
            }
         }
      }

      return null;
   }

   @Nullable
   public static Triple<Float, Float, Float> adjustWeatherFogColors(Camera camera, float red, float green, float blue) {
      if (camera.getEntity().level() instanceof ClientLevel clientLevel && clientLevel.effects() instanceof AetherSkyRenderEffects) {
         FogType fluidState = camera.getFluidInCamera();
         if (fluidState == FogType.NONE) {
            Vec3 defaultSky = Vec3.fromRGB24(((Biome)clientLevel.getBiome(camera.getBlockPosition()).value()).getModifiedSpecialEffects().getFogColor());
            if (clientLevel.rainLevel > 0.0) {
               float f14 = 1.0F + clientLevel.rainLevel * 0.8F;
               float f17 = 1.0F + clientLevel.rainLevel * 0.56F;
               red *= f14;
               green *= f14;
               blue *= f17;
            }

            if (clientLevel.thunderLevel > 0.0) {
               float f18 = 1.0F + clientLevel.thunderLevel * 0.66F;
               float f19 = 1.0F + clientLevel.thunderLevel * 0.76F;
               red *= f18;
               green *= f18;
               blue *= f19;
            }

            red = (float)Math.min((double)red, defaultSky.x());
            green = (float)Math.min((double)green, defaultSky.y());
            blue = (float)Math.min((double)blue, defaultSky.z());
            return Triple.of(red, green, blue);
         }
      }

      return null;
   }

   public static void tickTime() {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null
         && !Minecraft.getInstance().isPaused()
         && level.dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())) {
         AetherTimeAttachment data = (AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME);
         if (!data.isTimeSynced()) {
            LevelAccessor levelAccessor = (LevelAccessor)level;
            if (levelAccessor.aether$getLevelData().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
               level.setDayTime(data.tickTime(level) - 1L);
            }
         }
      }
   }
}
