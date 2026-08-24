package net.joefoxe.hexerei.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.joefoxe.hexerei.container.WoodcutterContainer;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WoodcutterScreen extends AbstractContainerScreen<WoodcutterContainer> {
   private final ResourceLocation GUI = HexereiUtil.getResource("textures/gui/woodcutter_gui.png");
   private final ResourceLocation INVENTORY = HexereiUtil.getResource("textures/gui/inventory.png");
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 18;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int SCROLLER_FULL_HEIGHT = 54;
   private static final int RECIPES_X = 48;
   private static final int RECIPES_Y = 23;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private boolean displayRecipes;

   public WoodcutterScreen(WoodcutterContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
      pMenu.registerUpdateListener(this::containerChanged);
      this.titleLabelY = 4;
      this.titleLabelX = 4;
      this.inventoryLabelY = 103;
      this.inventoryLabelX = 9;
   }

   public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
      this.renderTooltip(guiGraphics, pMouseX, pMouseY);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.leftPos;
      int j = this.topPos;
      guiGraphics.blit(this.GUI, i, j, 0, 0, 184, 122);
      int k = (int)(39.0F * this.scrollOffs);
      guiGraphics.blit(this.GUI, i + 123, j + 26 + k, 220 + (!this.isScrollBarActive() ? 24 : (this.scrolling ? 12 : 0)), 0, 12, 15);
      int l = this.leftPos + 48;
      int i1 = this.topPos + 23;
      int j1 = this.startIndex + 12;
      this.renderButtons(guiGraphics, mouseX, mouseY, l, i1, j1, false);
      this.renderRecipes(guiGraphics, l, i1, j1);
      RenderSystem.setShaderTexture(0, this.GUI);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (!((WoodcutterContainer)this.menu).resultContainer.isEmpty()) {
         guiGraphics.blit(this.GUI, i + 146, j + 46, 211, 0, 8, 11);
      }

      this.renderButtons(guiGraphics, mouseX, mouseY, l, i1, j1, true);
      guiGraphics.blit(this.INVENTORY, i + 4, j + 97, 0, 0, 176, 100);
   }

   protected void renderTooltip(GuiGraphics guiGraphics, int pX, int pY) {
      super.renderTooltip(guiGraphics, pX, pY);
      if (this.displayRecipes) {
         int i = this.leftPos + 48;
         int j = this.topPos + 23;
         int k = this.startIndex + 12;
         List<RecipeHolder<WoodcutterRecipe>> list = ((WoodcutterContainer)this.menu).getRecipes();

         for (int l = this.startIndex; l < k && l < ((WoodcutterContainer)this.menu).getNumRecipes(); l++) {
            int i1 = l - this.startIndex;
            int j1 = i + i1 % 4 * 18 + 2;
            int k1 = j + i1 / 4 * 18 + 2;
            if (pX >= j1 && pX < j1 + 18 && pY >= k1 && pY < k1 + 18) {
               guiGraphics.renderTooltip(
                  Minecraft.getInstance().font, ((WoodcutterRecipe)list.get(l).value()).getResultItem(this.minecraft.level.registryAccess()), pX, pY
               );
            }
         }
      }

      if (isHovering(pX, pY, this.leftPos + 142, this.topPos + 32, 16.0, 16.0)) {
         guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("Cost per craft."), pX, pY);
      }
   }

   protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
   }

   public static boolean isHovering(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
   }

   private void renderButtons(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex, boolean overlay) {
      for (int i = this.startIndex; i < pLastVisibleElementIndex && i < ((WoodcutterContainer)this.menu).getNumRecipes(); i++) {
         int j = i - this.startIndex;
         int k = pX + j % 4 * 18;
         int l = j / 4;
         int i1 = pY + l * 18 + 2;
         int j1 = 0;
         int xOffset = overlay ? 22 : 0;
         if (i == ((WoodcutterContainer)this.menu).getSelectedRecipeIndex()) {
            j1 += 22;
         } else if (pMouseX >= k + 2 && pMouseY >= i1 + 2 && pMouseX < k + 2 + 18 && pMouseY < i1 + 2 + 18) {
            j1 += 44;
         }

         guiGraphics.pose().pushPose();
         if (overlay) {
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
         }

         guiGraphics.blit(this.GUI, k, i1 - 1, xOffset, 122 + j1, 22, 22);
         guiGraphics.pose().popPose();
      }
   }

   private void renderRecipes(GuiGraphics guiGraphics, int pLeft, int pTop, int pRecipeIndexOffsetMax) {
      List<RecipeHolder<WoodcutterRecipe>> list = ((WoodcutterContainer)this.menu).getRecipes();

      for (int i = this.startIndex; i < pRecipeIndexOffsetMax && i < ((WoodcutterContainer)this.menu).getNumRecipes(); i++) {
         int j = i - this.startIndex;
         int k = pLeft + j % 4 * 18 + 3;
         int l = j / 4;
         int i1 = pTop + l * 18 + 4;
         ItemStack result = ((WoodcutterRecipe)list.get(i).value()).getResultItem(this.minecraft.level.registryAccess());
         guiGraphics.renderItem(result, k, i1);
         guiGraphics.renderItemDecorations(this.font, result, k, i1);
      }

      if (((WoodcutterContainer)this.menu).getSelectedRecipeIndex() != -1
         && ((WoodcutterContainer)this.menu).getRecipes().size() >= ((WoodcutterContainer)this.menu).getSelectedRecipeIndex() + 1) {
         WoodcutterRecipe selectedRecipe = (WoodcutterRecipe)((WoodcutterContainer)this.menu)
            .getRecipes()
            .get(((WoodcutterContainer)this.menu).getSelectedRecipeIndex())
            .value();
         ItemStack stack = ((Ingredient)selectedRecipe.getIngredients().get(0)).getItems()[0];
         stack.setCount(selectedRecipe.ingredientCount);
         guiGraphics.renderItem(stack, this.leftPos + 142, this.topPos + 32);
         guiGraphics.renderItemDecorations(this.font, stack, this.leftPos + 142, this.topPos + 32);
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      this.scrolling = false;
      return super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      this.scrolling = false;
      if (this.displayRecipes) {
         int i = this.leftPos + 48;
         int j = this.topPos + 23;
         int k = this.startIndex + 12;

         for (int l = this.startIndex; l < k; l++) {
            int i1 = l - this.startIndex;
            double d0 = pMouseX - (i + i1 % 4 * 18 + 2);
            double d1 = pMouseY - (j + i1 / 4 * 18 + 3);
            if (d0 >= 0.0 && d1 >= 0.0 && d0 < 18.0 && d1 < 18.0 && ((WoodcutterContainer)this.menu).clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick(((WoodcutterContainer)this.menu).containerId, l);
               return true;
            }
         }

         i = this.leftPos + 123;
         j = this.topPos + 26;
         if (pMouseX >= i && pMouseX < i + 12 && pMouseY >= j && pMouseY < j + 54) {
            if (this.isScrollBarActive()) {
               this.scrolling = true;
               i = this.topPos + 26;
               j = i + 54;
               this.scrollOffs = ((float)pMouseY - i - 7.5F) / (j - i - 15.0F);
               this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
               this.startIndex = Math.max(0, (int)(this.scrollOffs * this.getOffscreenRows() + 0.5) * 4);
            } else {
               this.scrollOffs = 0.0F;
               this.startIndex = 0;
            }
         }
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (this.scrolling && this.isScrollBarActive()) {
         int i = this.topPos + 26;
         int j = i + 54;
         this.scrollOffs = ((float)pMouseY - i - 7.5F) / (j - i - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = Math.max(0, (int)(this.scrollOffs * this.getOffscreenRows() + 0.5) * 4);
         return true;
      } else {
         return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.isScrollBarActive()) {
         int i = this.getOffscreenRows();
         float f = (float)scrollY / i;
         this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
         this.startIndex = Math.max(0, (int)(this.scrollOffs * i + 0.5) * 4);
      }

      return true;
   }

   private boolean isScrollBarActive() {
      return this.displayRecipes && ((WoodcutterContainer)this.menu).getNumRecipes() > 12;
   }

   protected int getOffscreenRows() {
      return (((WoodcutterContainer)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
   }

   private void containerChanged() {
      this.displayRecipes = ((WoodcutterContainer)this.menu).hasInputItem();
      if (!this.displayRecipes) {
         this.scrollOffs = 0.0F;
         this.startIndex = 0;
      }

      if (!this.isScrollBarActive()) {
         this.scrollOffs = 0.0F;
         this.startIndex = 0;
      }
   }
}
