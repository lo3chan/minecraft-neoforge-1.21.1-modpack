package org.dimdev.limlib.mixin.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.dimension.DimensionType;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DimensionSpecialEffects.class})
public class DimensionVisualEffectsMixin {
   @Inject(
      method = {"forType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void limlib$byDimensionType(DimensionType dimensionType, CallbackInfoReturnable<DimensionSpecialEffects> ci) {
      LookupGrabber.<DimensionEffects>snatch(DimensionEffects.MIXIN_WORLD_LOOKUP.get(), dimensionType.effectsLocation()).ifPresent(dimensionEffects -> {
         DimensionSpecialEffects dimensionEffect = EffectRenderers.get(dimensionEffects);
         if (dimensionEffect != null) {
            ci.setReturnValue(dimensionEffect);
         }
      });
   }
}
