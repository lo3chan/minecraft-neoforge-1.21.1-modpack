package com.seibel.distanthorizons.fabric.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.common.commonMixins.MixinVanillaFogCommon_fabric;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import net.minecraft.class_4184;
import net.minecraft.class_758;
import net.minecraft.class_758.class_4596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_758.class})
public class MixinFogRenderer {
   @Unique
   private static final float A_REALLY_REALLY_BIG_VALUE = 4.206942E14F;
   @Unique
   private static final float A_EVEN_LARGER_VALUE = 4.206942E19F;

   @Inject(
      at = {@At("RETURN")},
      method = {"setupFog"}
   )
   private static void disableSetupFog(class_4184 camera, class_4596 fogMode, float f, boolean bl, float g, CallbackInfo callback) {
      boolean cancelFog = MixinVanillaFogCommon_fabric.cancelFog(camera, fogMode);
      if (cancelFog) {
         RenderSystem.setShaderFogStart(4.206942E14F);
         RenderSystem.setShaderFogEnd(4.206942E19F);
         ClientApi.RENDER_STATE.vanillaFogEnabled = false;
      } else {
         ClientApi.RENDER_STATE.vanillaFogEnabled = true;
      }
   }
}
