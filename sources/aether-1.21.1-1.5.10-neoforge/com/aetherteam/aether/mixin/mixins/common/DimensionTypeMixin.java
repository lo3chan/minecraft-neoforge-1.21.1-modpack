package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DimensionType.class})
public class DimensionTypeMixin {
   @ModifyVariable(
      at = @At("STORE"),
      method = {"timeOfDay(J)F"},
      index = 3
   )
   private double modifyTimeOfDay(double d0, long dayTime) {
      DimensionType dimensionType = (DimensionType)this;
      return dimensionType.effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())
         ? Mth.frac((double)dayTime / AetherTimeAttachment.getTicksPerDay() - 0.25)
         : d0;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"moonPhase(J)I"},
      cancellable = true
   )
   private void moonPhase(long dayTime, CallbackInfoReturnable<Integer> cir) {
      DimensionType dimensionType = (DimensionType)this;
      if (dimensionType.effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())) {
         cir.setReturnValue((int)(dayTime / AetherTimeAttachment.getTicksPerDay() % 8L + 8L) % 8);
      }
   }
}
