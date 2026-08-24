package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.mixins.accessors.RecipeBookPageAccessor;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookComponent.class})
public abstract class ExpandedBookMixin {
   @Shadow
   protected Minecraft minecraft;
   @Shadow
   private int xOffset;
   @Shadow
   private boolean widthTooNarrow;
   @Shadow
   private int width;
   @Shadow
   private int height;
   @Shadow
   private EditBox searchBox;
   @Shadow
   private List<RecipeBookTabButton> tabButtons;
   @Shadow
   private RecipeBookPage recipeBookPage;
   @Shadow
   private boolean visible;
   @Unique
   private static final ResourceLocation RECIPE_BOOK_BG = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
   @Unique
   private static final int BG_LEFT_CAP = 32;
   @Unique
   private static final int BG_RIGHT_CAP = 12;
   @Unique
   private static final int BG_BODY = 103;

   @Unique
   private boolean brbe$isExpanded() {
      return BetterRecipeBook.ctx().config().expandedRecipeBook && !this.widthTooNarrow && this.visible;
   }

   @Unique
   private int brbe$getBookWidth() {
      if (this.brbe$isExpanded()) {
         int invImageWidth = 176;
         int leftPos = ((RecipeBookComponentAccessor)this).updateScreenPositionInvoker(this.width, invImageWidth);
         int bookLeft = (this.width - 147) / 2 - this.xOffset;
         return leftPos + invImageWidth - bookLeft;
      } else {
         return 147;
      }
   }

   @Inject(
      method = {"initVisuals"},
      at = {@At("RETURN")}
   )
   private void brbe$expandInitVisuals(CallbackInfo ci) {
      if (this.brbe$isExpanded()) {
         int bookWidth = this.brbe$getBookWidth();
         int bookX = (this.width - 147) / 2 - this.xOffset;
         int bookY = (this.height - 166) / 2;
         if (this.searchBox != null) {
            this.searchBox.setWidth(bookWidth - 140);
         }

         int tabX = bookX - 30;
         int tabY = bookY + 3;
         int slot = 0;

         for (RecipeBookTabButton btn : this.tabButtons) {
            btn.setX(tabX);
            btn.setY(tabY + 27 * slot++);
         }

         int cols = Math.max(5, (bookWidth - 22) / 25);
         int gridWidth = cols * 25;
         int gridLeft = bookX + (bookWidth - gridWidth) / 2;
         List<RecipeButton> buttons = ((RecipeBookPageAccessor)this.recipeBookPage).getButtons();

         for (int k = 0; k < buttons.size(); k++) {
            buttons.get(k).setX(gridLeft + 25 * (k % cols));
            buttons.get(k).setY(bookY + 31 + 25 * (k / cols));
         }
      }
   }

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
      ),
      require = 0
   )
   private void brbe$expandBackground(GuiGraphics gui, ResourceLocation texture, int x, int y, int u0, int v0, int w, int h) {
      if (this.brbe$isExpanded() && RECIPE_BOOK_BG.equals(texture)) {
         int bookWidth = this.brbe$getBookWidth();
         int blitX = (this.width - 147) / 2 - this.xOffset;
         int blitY = (this.height - 166) / 2;
         gui.pose().popPose();
         gui.pose().pushPose();
         gui.pose().translate(0.0F, 0.0F, 200.0F);
         gui.blit(texture, blitX, blitY, 0.0F, 0.0F, 32, 166, 256, 256);
         int bodyStartX = blitX + 32;
         int bodyEndX = blitX + bookWidth - 12;

         for (int bx = 0; bx < bodyEndX - bodyStartX; bx += 103) {
            int segW = Math.min(103, bodyEndX - bodyStartX - bx);
            gui.blit(texture, bodyStartX + bx, blitY, 32.0F, 0.0F, segW, 166, 256, 256);
         }

         gui.blit(texture, bodyEndX, blitY, 135.0F, 0.0F, 12, 166, 256, 256);
         gui.pose().popPose();
         gui.pose().pushPose();
         gui.pose().translate(0.0F, 0.0F, 100.0F);
      } else {
         gui.blit(texture, x, y, u0, v0, w, h);
      }
   }

   @Inject(
      method = {"isOffsetNextToMainGUI"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void brbe$isOffsetNextToMainGui(CallbackInfoReturnable<Boolean> cir) {
      if (this.brbe$isExpanded()) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"hasClickedOutside"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void brbe$hasClickedOutside(double d, double e, int i, int j, int k, int l, int m, CallbackInfoReturnable<Boolean> cir) {
      if (this.brbe$isExpanded() && this.visible) {
         int bookWidth = this.brbe$getBookWidth();
         boolean bl = d < i || e < j || d >= i + k || e >= j + l;
         boolean bl2 = i - bookWidth < d && d < i && j < e && e < j + l;
         cir.setReturnValue(bl && !bl2);
      }
   }
}
