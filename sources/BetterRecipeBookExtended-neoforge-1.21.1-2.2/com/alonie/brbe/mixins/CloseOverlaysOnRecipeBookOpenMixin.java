package com.alonie.brbe.mixins;

import com.alonie.brbe.mixins.accessors.AbstractContainerScreenAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookComponent.class})
public abstract class CloseOverlaysOnRecipeBookOpenMixin {
   @Shadow
   private boolean widthTooNarrow;
   @Shadow
   protected Minecraft minecraft;

   @Inject(
      method = {"setVisible"},
      at = {@At("RETURN")}
   )
   private void brbe$repositionOnOpen(boolean becomingVisible, CallbackInfo ci) {
      if (becomingVisible) {
         if (this.minecraft != null && this.minecraft.screen != null) {
            if (this.minecraft.screen instanceof AbstractContainerScreen<?> containerScreen) {
               AbstractContainerScreenAccessor var6 = (AbstractContainerScreenAccessor)containerScreen;
               int newLeft = ((RecipeBookComponentAccessor)this).updateScreenPositionInvoker(containerScreen.width, var6.getImageWidth());
               var6.setLeftPos(newLeft);
            }
         }
      }
   }
}
