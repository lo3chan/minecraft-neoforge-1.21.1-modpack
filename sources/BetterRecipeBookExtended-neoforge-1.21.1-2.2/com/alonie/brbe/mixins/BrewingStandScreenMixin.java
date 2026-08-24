package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.brewingstand.BrewingRecipeBookComponent;
import com.alonie.brbe.interfaces.ExpandedBookScreen;
import com.alonie.brbe.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BrewingStandScreen.class})
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> implements ExpandedBookScreen {
   @Unique
   public final BrewingRecipeBookComponent _$recipeBookComponent = new BrewingRecipeBookComponent();
   @Unique
   private boolean _$widthNarrow;

   public BrewingStandScreenMixin(BrewingStandMenu handler, Inventory inventory, Component title) {
      super(handler, inventory, title);
   }

   @Inject(
      method = {"init"},
      at = {@At("RETURN")}
   )
   protected void init(CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().enableBook) {
         this._$widthNarrow = this.width < 379;

         assert this.minecraft != null;

         this._$recipeBookComponent
            .init(
               this.width,
               this.height,
               this.minecraft,
               this._$widthNarrow,
               (BrewingStandMenu)this.menu,
               Minecraft.getInstance().getConnection().registryAccess()
            );
         this._$recipeBookComponent.setContainerImageWidth(this.imageWidth);
         if (!BetterRecipeBook.ctx().config().keepCentered) {
            this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
         }

         this.addRenderableWidget(
            new ImageButton(
               this.leftPos - 12 + this._$recipeBookComponent.getCurrentBookWidth(),
               this.height / 2 - 50,
               20,
               18,
               BRBTextures.RECIPE_BOOK_BUTTON_SPRITES,
               button -> {
                  this._$recipeBookComponent.toggleVisibility();
                  if (!BetterRecipeBook.ctx().config().keepCentered) {
                     this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                  }

                  button.setPosition(this.leftPos - 12 + this._$recipeBookComponent.getCurrentBookWidth(), this.height / 2 - 50);
               }
            )
         );
         this.addWidget(this._$recipeBookComponent);
      }
   }

   public boolean keyPressed(int i, int j, int k) {
      return this._$recipeBookComponent.keyPressed(i, j, k) ? true : super.keyPressed(i, j, k);
   }

   public boolean keyReleased(int i, int j, int k) {
      return this._$recipeBookComponent.keyReleased(i, j, k) ? true : super.keyReleased(i, j, k);
   }

   public boolean charTyped(char c, int i) {
      return this._$recipeBookComponent.charTyped(c, i) ? true : super.charTyped(c, i);
   }

   protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
      if (slot != null && slot.index < 4 && ((Slot)((BrewingStandMenu)this.menu).slots.get(slot.index)).getItem().isEmpty()) {
         this._$recipeBookComponent.ghostRecipe.clear();
      }

      super.slotClicked(slot, x, y, clickType);
   }

   protected boolean hasClickedOutside(double d, double e, int i, int j, int k) {
      boolean bl = d < i || e < j || d >= i + this.imageWidth || e >= j + this.imageHeight;
      return this._$recipeBookComponent.hasClickedOutside(d, e, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, k) && bl;
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
      if (this._$recipeBookComponent.isVisible()) {
         this._$recipeBookComponent.render(guiGraphics, i, j, f);
         this._$recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, false, f);
         this._$recipeBookComponent.drawTooltip(guiGraphics, this.leftPos, this.topPos, i, j);
      }
   }

   @Override
   public boolean brbe$isExpandedBookOpen() {
      return this._$recipeBookComponent.isVisible() && this._$recipeBookComponent.isExpanded();
   }

   @ModifyVariable(
      method = {"renderBg"},
      index = 5,
      at = @At("STORE")
   )
   public int renderBg_width(int i) {
      return this._$recipeBookComponent.isVisible() && !BetterRecipeBook.ctx().config().keepCentered ? i + 77 : i;
   }
}
