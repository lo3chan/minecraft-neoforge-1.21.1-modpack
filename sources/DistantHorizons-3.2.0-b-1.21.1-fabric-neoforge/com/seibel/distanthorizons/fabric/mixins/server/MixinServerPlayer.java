package com.seibel.distanthorizons.fabric.mixins.server;

import com.seibel.distanthorizons.common.wrappers.misc.IMixinServerPlayer_fabric;
import net.minecraft.class_1297;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5454;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_3222.class})
public class MixinServerPlayer implements IMixinServerPlayer_fabric {
   @Unique
   @Nullable
   private class_3218 dimensionChangeDestination;

   @Nullable
   @Override
   public class_3218 distantHorizons$getDimensionChangeDestination() {
      return this.dimensionChangeDestination;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"changeDimension"}
   )
   public void changeDimension(class_5454 dimensionTransition, CallbackInfoReturnable<class_1297> cir) {
      this.dimensionChangeDestination = dimensionTransition.comp_2820();
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"setServerLevel"}
   )
   public void setServerLevel(class_3218 level, CallbackInfo ci) {
      this.dimensionChangeDestination = null;
   }
}
