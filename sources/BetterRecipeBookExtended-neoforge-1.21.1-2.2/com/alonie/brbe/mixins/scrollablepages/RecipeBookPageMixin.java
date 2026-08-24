package com.alonie.brbe.mixins.scrollablepages;

import com.alonie.brbe.BetterRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookPage.class})
public abstract class RecipeBookPageMixin {
   @Shadow
   private int currentPage;
   @Shadow
   private int totalPages;
   @Shadow
   private StateSwitchingButton forwardButton;
   @Shadow
   private StateSwitchingButton backButton;

   @Shadow
   protected abstract void updateButtonsForPage();

   @Inject(
      method = {"mouseClicked"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/StateSwitchingButton;mouseClicked(DDI)Z"
      )},
      cancellable = true
   )
   public void mouseClickedBtn(
      double mouseX, double mouseY, int button, int areaLeft, int areaTop, int areaWidth, int areaHeight, CallbackInfoReturnable<Boolean> cir
   ) {
      if (this.forwardButton.mouseClicked(mouseX, mouseY, button)) {
         cir.setReturnValue(true);
         cir.cancel();
         if (++this.currentPage >= this.totalPages) {
            this.currentPage = BetterRecipeBook.config.scrollAround ? 0 : this.totalPages - 1;
         }

         this.updateButtonsForPage();
      } else if (this.backButton.mouseClicked(mouseX, mouseY, button)) {
         cir.setReturnValue(true);
         cir.cancel();
         if (--this.currentPage < 0) {
            this.currentPage = BetterRecipeBook.config.scrollAround ? this.totalPages - 1 : 0;
         }

         this.updateButtonsForPage();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void render(GuiGraphics gui, int i, int j, int k, int l, float f, CallbackInfo ci) {
      if (BetterRecipeBook.getQueuedScroll() != 0) {
         if (isMouseOverRecipeBookPage(k, l, i, j) && this.totalPages > 1) {
            this.currentPage = this.currentPage + BetterRecipeBook.getQueuedScroll();
            if (this.currentPage >= this.totalPages) {
               this.currentPage = BetterRecipeBook.config.scrollAround ? this.currentPage % this.totalPages : this.totalPages - 1;
            } else if (this.currentPage < 0) {
               this.currentPage = BetterRecipeBook.config.scrollAround ? this.currentPage % this.totalPages + this.totalPages : 0;
            }

            this.updateButtonsForPage();
         }

         BetterRecipeBook.setQueuedScroll(0);
      }
   }

   private static boolean isMouseOverRecipeBookPage(int mouseX, int mouseY, int left, int top) {
      return mouseX >= left && mouseX < left + 147 && mouseY >= top && mouseY < top + 166;
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"init"}
   )
   public void init(Minecraft minecraftClient, int parentLeft, int parentTop, CallbackInfo ci) {
      BetterRecipeBook.setQueuedScroll(0);
   }

   @Inject(
      method = {"updateArrowButtons"},
      at = {@At("RETURN")}
   )
   private void updateArrowButtons(CallbackInfo ci) {
      if (BetterRecipeBook.config.scrollAround && this.totalPages > 1) {
         this.forwardButton.visible = true;
         this.backButton.visible = true;
      }
   }
}
