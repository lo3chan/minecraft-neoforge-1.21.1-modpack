package com.alonie.brbe.mixins.instantcraft;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookPageAccessor;
import com.alonie.brbe.util.BRBTextures;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookComponent.class})
public abstract class RecipeBookComponentMixin {
   @Shadow
   protected Minecraft minecraft;
   @Shadow
   private int height;
   @Shadow
   private int width;
   @Shadow
   private int xOffset;
   @Shadow
   private boolean widthTooNarrow;
   @Shadow
   @Final
   private RecipeBookPage recipeBookPage;
   @Unique
   protected StateSwitchingButton brbe$instantCraftButton;
   @Unique
   private static final Component TOGGLE_INSTANT_CRAFT_ON_TEXT = Component.translatable("brb.gui.instantCraft.on");
   @Unique
   private static final Component TOGGLE_INSTANT_CRAFT_OFF_TEXT = Component.translatable("brb.gui.instantCraft.off");

   @Shadow
   public abstract boolean isVisible();

   @Unique
   private int brbe$getExpandedBookWidth() {
      if (BetterRecipeBook.ctx().config().expandedRecipeBook && !this.widthTooNarrow && this.isVisible()) {
         int leftPos = ((RecipeBookComponentAccessor)this).updateScreenPositionInvoker(this.width, 176);
         int bookLeft = (this.width - 147) / 2 - this.xOffset;
         return leftPos + 176 - bookLeft;
      } else {
         return 147;
      }
   }

   @Unique
   private boolean brbe$shouldSkip() {
      return !BetterRecipeBook.ctx().config().instantCraft.showButton ? true : this instanceof AbstractFurnaceRecipeBookComponent;
   }

   @Inject(
      method = {"initVisuals"},
      at = {@At("RETURN")}
   )
   public void reset(CallbackInfo ci) {
      if (!this.brbe$shouldSkip()) {
         RecipeBookPageAccessor pageAcc = (RecipeBookPageAccessor)this.recipeBookPage;
         StateSwitchingButton fwd = pageAcc.getForwardButton();
         int bw = this.brbe$getExpandedBookWidth();
         int bl = (this.width - bw) / 2 - this.xOffset;
         int gridRight = bl + bw - 11;
         int btnX = gridRight - 26;
         int btnY;
         if (fwd != null) {
            btnY = fwd.getY() + fwd.getHeight() - 18;
         } else {
            int bt = (this.height - 166) / 2;
            btnY = bt + 137 + 17 - 18;
         }

         this.brbe$instantCraftButton = new StateSwitchingButton(btnX, btnY, 26, 18, BetterRecipeBook.instantCraftingManager.isEnabled());
         BetterRecipeBook.instantCraftingManager.lastInstantCraftButton = this.brbe$instantCraftButton;
         this.brbe$instantCraftButton.initTextureValues(BRBTextures.RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"
      )}
   )
   public void render(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (!this.brbe$shouldSkip() && this.brbe$instantCraftButton != null && this.isVisible()) {
         this.brbe$instantCraftButton.render(gui, mouseX, mouseY, delta);
      }
   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (this.isVisible()
         && !this.brbe$shouldSkip()
         && this.brbe$instantCraftButton != null
         && this.brbe$instantCraftButton.mouseClicked(mouseX, mouseY, button)) {
         boolean bl = BetterRecipeBook.instantCraftingManager.toggleEnabled();
         this.brbe$instantCraftButton.setStateTriggered(bl);
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"renderTooltip"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"
      )}
   )
   public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
      if (!this.brbe$shouldSkip()) {
         if (this.brbe$instantCraftButton != null && this.brbe$instantCraftButton.isHoveredOrFocused()) {
            Component text = this.brbe$instantCraftButton.isStateTriggered() ? TOGGLE_INSTANT_CRAFT_ON_TEXT : TOGGLE_INSTANT_CRAFT_OFF_TEXT;
            if (this.minecraft.screen != null) {
               gui.renderComponentTooltip(this.minecraft.font, List.of(text), mouseX, mouseY);
            }
         }
      }
   }
}
