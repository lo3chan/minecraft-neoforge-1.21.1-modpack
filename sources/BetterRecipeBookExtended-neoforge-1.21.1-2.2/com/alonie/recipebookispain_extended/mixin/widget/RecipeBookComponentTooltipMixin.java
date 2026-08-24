package com.alonie.recipebookispain_extended.mixin.widget;

import com.alonie.recipebookispain_extended.access.CreativeTabButtonAccess;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentTooltipMixin {
   @Shadow
   @Final
   private List<RecipeBookTabButton> tabButtons;
   @Shadow
   protected Minecraft minecraft;

   @Inject(
      method = {"renderTooltip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void rbip$renderCreativeTabTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
      if (this.minecraft != null && this.minecraft.screen != null) {
         for (RecipeBookTabButton btn : this.tabButtons) {
            if (btn.visible && btn.isMouseOver(mouseX, mouseY) && btn instanceof CreativeTabButtonAccess access) {
               CreativeModeTab tab = access.rbip$getCreativeTab();
               if (tab != null) {
                  gui.renderTooltip(this.minecraft.font, tab.getDisplayName(), mouseX, mouseY);
                  ci.cancel();
                  return;
               }
            }
         }
      }
   }
}
