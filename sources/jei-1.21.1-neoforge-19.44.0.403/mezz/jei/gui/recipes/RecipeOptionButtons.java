package mezz.jei.gui.recipes;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nonnegative;
import mezz.jei.common.Internal;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RecipeOptionButtons {
   private static final int buttonSize = 16;
   private static final int buttonBorderSize = 1;
   private static final int borderSize = 5;
   private static final int overlapSize = 6;
   private final List<IconButton> buttons;
   private final ScalableDrawable backgroundTab;
   private ImmutableRect2i area = ImmutableRect2i.EMPTY;

   public RecipeOptionButtons(Runnable onValueChanged) {
      Textures textures = Internal.getTextures();
      IconButton bookmarksFirstButton = new IconButton(
         new RecipeSortStateButtonController(
            RecipeSorterStage.BOOKMARKED,
            textures.getBookmarksFirst(),
            textures.getBookmarksFirst(),
            Component.translatable("jei.tooltip.recipe.sort.bookmarks.first.disabled"),
            Component.translatable("jei.tooltip.recipe.sort.bookmarks.first.enabled"),
            onValueChanged
         )
      );
      IconButton craftableFirstButton = new IconButton(
         new RecipeSortStateButtonController(
            RecipeSorterStage.CRAFTABLE,
            textures.getCraftableFirst(),
            textures.getCraftableFirst(),
            Component.translatable("jei.tooltip.recipe.sort.craftable.first.disabled"),
            Component.translatable("jei.tooltip.recipe.sort.craftable.first.enabled"),
            onValueChanged
         )
      );
      this.buttons = List.of(bookmarksFirstButton, craftableFirstButton);
      this.backgroundTab = textures.getRecipeOptionsTab();
   }

   public void tick() {
      for (IconButton button : this.buttons) {
         button.tick();
      }
   }

   public void updateLayout(ImmutableRect2i recipeArea) {
      int width = 28;
      int height = 12 + this.buttons.size() * 16;
      int y = recipeArea.getY() + recipeArea.getHeight() - height;
      int x = recipeArea.getX() - width + 6;
      this.area = new ImmutableRect2i(x, y, width, height);
      int buttonX = x + 5 + 1;

      for (int i = 0; i < this.buttons.size(); i++) {
         IconButton button = this.buttons.get(i);
         int buttonY = y + 5 + i * 16 + 1;
         button.updateBounds(new ImmutableRect2i(buttonX, buttonY, 16, 16));
      }
   }

   public ImmutableRect2i getArea() {
      return this.area;
   }

   public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableDepthTest();
      this.backgroundTab.draw(guiGraphics, this.area);
      RenderSystem.enableDepthTest();

      for (IconButton button : this.buttons) {
         button.draw(guiGraphics, mouseX, mouseY, partialTicks);
      }
   }

   @Nonnegative
   public int getWidth() {
      return Math.max(0, this.area.getWidth() - 6);
   }

   public IUserInputHandler createInputHandler() {
      List<IUserInputHandler> handlers = this.buttons.stream().map(IconButton::createInputHandler).toList();
      return new CombinedInputHandler("RecipeOptionButtons", handlers);
   }

   public void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      for (IconButton button : this.buttons) {
         button.drawTooltips(guiGraphics, mouseX, mouseY);
      }
   }
}
