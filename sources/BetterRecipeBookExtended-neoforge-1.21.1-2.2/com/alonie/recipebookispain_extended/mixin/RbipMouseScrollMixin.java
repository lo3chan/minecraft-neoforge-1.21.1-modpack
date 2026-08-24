package com.alonie.recipebookispain_extended.mixin;

import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MouseHandler.class})
public abstract class RbipMouseScrollMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      at = {@At("HEAD")},
      method = {"onScroll"}
   )
   private void rbip$captureScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      if (window == this.minecraft.getWindow().getWindow()) {
         if (vertical != 0.0) {
            RecipeBookIsPain.rbip$queueScroll(vertical > 0.0 ? 1 : -1);
         }
      }
   }
}
