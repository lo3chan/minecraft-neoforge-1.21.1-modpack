package com.seibel.distanthorizons.neoforge.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.seibel.distanthorizons.common.commonMixins.MixinVanillaFogCommon_neoforge;
import com.seibel.distanthorizons.core.api.internal.ClientApi;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FogRenderer.class})
public class MixinFogRenderer {
   @Unique
   private static final float A_REALLY_REALLY_BIG_VALUE = 4.206942E14F;
   @Unique
   private static final float A_EVEN_LARGER_VALUE = 4.206942E19F;

   @Inject(
      at = {@At("RETURN")},
      method = {"setupFog"}
   )
   private static void disableSetupFog(Camera camera, FogMode fogMode, float f, boolean bl, float g, CallbackInfo callback) {
      boolean cancelFog = MixinVanillaFogCommon_neoforge.cancelFog(camera, fogMode);
      if (cancelFog) {
         RenderSystem.setShaderFogStart(4.206942E14F);
         RenderSystem.setShaderFogEnd(4.206942E19F);
         ClientApi.RENDER_STATE.vanillaFogEnabled = false;
      } else {
         ClientApi.RENDER_STATE.vanillaFogEnabled = true;
      }
   }
}
