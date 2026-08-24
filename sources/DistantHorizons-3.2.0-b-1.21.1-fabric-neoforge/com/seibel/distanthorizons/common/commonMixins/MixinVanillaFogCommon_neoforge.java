package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IImmersivePortalsAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;

public class MixinVanillaFogCommon_neoforge {
   public static boolean cancelFog(Camera camera, FogMode fogMode) {
      Entity entity = camera.getEntity();
      boolean cameraNotInFluid = cameraNotInFluid(camera);
      boolean isSpecialFog = entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.BLINDNESS);
      boolean cancelFog = !isSpecialFog;
      cancelFog = cancelFog && cameraNotInFluid;
      cancelFog = cancelFog && fogMode == FogMode.FOG_TERRAIN;
      cancelFog = cancelFog && !SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class).isFogStateSpecial();
      cancelFog = cancelFog && !Config.Client.Advanced.Graphics.Fog.enableVanillaFog.get();
      IImmersivePortalsAccessor immersivePortals = ModAccessorInjector.INSTANCE.get(IImmersivePortalsAccessor.class);
      if (immersivePortals != null && immersivePortals.isRenderingPortal()) {
         cancelFog = false;
      }

      return cancelFog;
   }

   private static boolean cameraNotInFluid(Camera camera) {
      FogType fogTypes = camera.getFluidInCamera();
      return fogTypes == FogType.NONE;
   }
}
