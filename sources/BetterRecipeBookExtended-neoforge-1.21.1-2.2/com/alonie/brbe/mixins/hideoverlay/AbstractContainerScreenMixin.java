package com.alonie.brbe.mixins.hideoverlay;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.compat.ItemViewCompat;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractContainerScreen.class})
public abstract class AbstractContainerScreenMixin {
   private static final int KEY_A = 65;
   @Shadow
   protected Slot hoveredSlot;

   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void brbe$handleKeysOnHiddenOverlay(int keyCode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      if (BetterRecipeBook.ctx().config().hideReiJeiOverlay) {
         if (keyCode == 65) {
            Screen screen = (Screen)this;
            if (!(screen.getFocused() instanceof EditBox)) {
               cir.setReturnValue(true);
            }
         } else if (ItemViewCompat.matchesShowRecipe(keyCode, scancode) || ItemViewCompat.matchesShowUses(keyCode, scancode)) {
            if (ItemViewCompat.isLoaded()) {
               Slot slot = this.hoveredSlot;
               if (slot != null && slot.hasItem()) {
                  ItemStack stack = slot.getItem();
                  boolean handled = ItemViewCompat.matchesShowRecipe(keyCode, scancode)
                     ? ItemViewCompat.openRecipeView(stack)
                     : ItemViewCompat.openUsageView(stack);
                  if (handled) {
                     cir.setReturnValue(true);
                  }
               }
            }
         }
      }
   }
}
