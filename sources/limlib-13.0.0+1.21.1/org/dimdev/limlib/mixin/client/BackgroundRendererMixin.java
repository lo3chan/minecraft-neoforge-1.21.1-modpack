package org.dimdev.limlib.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({FogRenderer.class})
public abstract class BackgroundRendererMixin {
   @ModifyVariable(
      method = {"setupColor"},
      at = @At(
         value = "STORE",
         ordinal = 3
      ),
      ordinal = 2
   )
   private static float limlib$modifySkyColor(float in) {
      return LookupGrabber.<DimensionEffects>snatchFromLevel(Minecraft.getInstance().level, LimLibRegistryKeys.DIMENSION_EFFECTS)
         .map(DimensionEffects::skyShading)
         .orElse(in);
   }
}
