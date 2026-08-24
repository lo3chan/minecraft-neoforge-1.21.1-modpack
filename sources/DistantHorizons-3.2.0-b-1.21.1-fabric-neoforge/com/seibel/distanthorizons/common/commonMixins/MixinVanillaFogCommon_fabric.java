package com.seibel.distanthorizons.common.commonMixins;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dependencyInjection.ModAccessorInjector;
import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftRenderWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor.IImmersivePortalsAccessor;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import net.minecraft.class_758.class_4596;

public class MixinVanillaFogCommon_fabric {
   public static boolean cancelFog(class_4184 camera, class_4596 fogMode) {
      class_1297 entity = camera.method_19331();
      boolean cameraNotInFluid = cameraNotInFluid(camera);
      boolean isSpecialFog = entity instanceof class_1309 && ((class_1309)entity).method_6059(class_1294.field_5919);
      boolean cancelFog = !isSpecialFog;
      cancelFog = cancelFog && cameraNotInFluid;
      cancelFog = cancelFog && fogMode == class_4596.field_20946;
      cancelFog = cancelFog && !SingletonInjector.INSTANCE.get(IMinecraftRenderWrapper.class).isFogStateSpecial();
      cancelFog = cancelFog && !Config.Client.Advanced.Graphics.Fog.enableVanillaFog.get();
      IImmersivePortalsAccessor immersivePortals = ModAccessorInjector.INSTANCE.get(IImmersivePortalsAccessor.class);
      if (immersivePortals != null && immersivePortals.isRenderingPortal()) {
         cancelFog = false;
      }

      return cancelFog;
   }

   private static boolean cameraNotInFluid(class_4184 camera) {
      class_5636 fogTypes = camera.method_19334();
      return fogTypes == class_5636.field_27888;
   }
}
