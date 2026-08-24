package com.alonie.brbe.mixins;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.interfaces.ExpandedBookScreen;
import com.alonie.brbe.interfaces.TopLayerOverlayProvider;
import com.alonie.brbe.smithingtable.SmithingRecipeBookComponent;
import com.alonie.brbe.smithingtable.SmithingRecipeBookPage;
import com.alonie.brbe.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SmithingScreen.class})
public abstract class SmithingScreenMixin extends ItemCombinerScreen<SmithingMenu> implements TopLayerOverlayProvider, ExpandedBookScreen {
   @Unique
   public final SmithingRecipeBookComponent _$recipeBookComponent = new SmithingRecipeBookComponent();
   @Unique
   private boolean _$widthNarrow;

   @Shadow
   protected abstract void updateArmorStandPreview(ItemStack var1);

   public SmithingScreenMixin(SmithingMenu itemCombinerMenu, Inventory inventory, Component component, ResourceLocation resourceLocation) {
      super(itemCombinerMenu, inventory, component, resourceLocation);
   }

   @Inject(
      method = {"subInit"},
      at = {@At("RETURN")}
   )
   void init(CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().enableBook) {
         this._$widthNarrow = this.width < 379;
         this._$recipeBookComponent
            .init(
               this.width,
               this.height,
               this.minecraft,
               this._$widthNarrow,
               (SmithingMenu)this.menu,
               this::updateArmorStandPreview,
               Minecraft.getInstance().getConnection().registryAccess(),
               Minecraft.getInstance().getConnection().getRecipeManager()
            );
         this._$recipeBookComponent.setContainerImageWidth(this.imageWidth);
         if (!BetterRecipeBook.ctx().config().keepCentered) {
            this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
         }

         this.addRenderableWidget(
            new ImageButton(
               this.leftPos + this._$recipeBookComponent.getCurrentBookWidth(),
               this.height / 2 - 75,
               20,
               18,
               BRBTextures.RECIPE_BOOK_BUTTON_SPRITES,
               button -> {
                  this._$recipeBookComponent.toggleVisibility();
                  if (!BetterRecipeBook.ctx().config().keepCentered) {
                     this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                  }

                  button.setPosition(this.leftPos + this._$recipeBookComponent.getCurrentBookWidth(), this.height / 2 - 75);
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

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.brbe$clickTopLayerOverlay(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
   }

   protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
      if (BetterRecipeBook.ctx().config().enableBook
         && slot != null
         && slot.index < 4
         && ((SmithingMenu)this.menu).getCarried().isEmpty()
         && ((Slot)((SmithingMenu)this.menu).slots.get(slot.index)).getItem().isEmpty()) {
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

   @Redirect(
      method = {"renderBg"},
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/CyclingSlotBackground;render(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/client/gui/GuiGraphics;FII)V"
      )
   )
   public void renderBg(CyclingSlotBackground instance, AbstractContainerMenu slot, GuiGraphics bl, float g, int k, int arg) {
      if (!BetterRecipeBook.ctx().config().enableBook || !this._$recipeBookComponent.isShowingGhostRecipe()) {
         instance.render(this.menu, bl, g, this.leftPos, this.topPos);
      }
   }

   @Inject(
      method = {"renderOnboardingTooltips"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderOnboardingTooltips(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().enableBook && this._$recipeBookComponent.isShowingGhostRecipe()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"slotChanged"},
      at = {@At("HEAD")}
   )
   public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack, CallbackInfo ci) {
      if (i == 1 || i == 2 || i == 0 || i == 3) {
         this._$recipeBookComponent.ghostRecipe.clear();
      }
   }

   @Override
   public boolean brbe$hasTopLayerOverlay() {
      return this._$recipeBookComponent.isVisible() && this._$recipeBookComponent.recipesPage instanceof SmithingRecipeBookPage page && page.overlayIsVisible();
   }

   @Override
   public void brbe$renderTopLayerOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.brbe$hasTopLayerOverlay()) {
         ((SmithingRecipeBookPage)this._$recipeBookComponent.recipesPage).overlay.render(guiGraphics, mouseX, mouseY, partialTick);
      }
   }

   @Override
   public boolean brbe$clickTopLayerOverlay(double mouseX, double mouseY, int button) {
      return this.brbe$hasTopLayerOverlay() && this._$recipeBookComponent.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean brbe$isExpandedBookOpen() {
      return this._$recipeBookComponent.isVisible() && this._$recipeBookComponent.isExpanded();
   }

   @Override
   public ScreenRectangle brbe$getTopLayerOverlayBounds() {
      return this.brbe$hasTopLayerOverlay() ? ((SmithingRecipeBookPage)this._$recipeBookComponent.recipesPage).overlay.getBounds() : null;
   }
}
