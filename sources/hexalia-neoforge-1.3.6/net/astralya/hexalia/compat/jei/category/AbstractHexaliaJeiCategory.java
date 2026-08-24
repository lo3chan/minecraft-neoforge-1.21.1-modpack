package net.astralya.hexalia.compat.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.astralya.hexalia.compat.HexaliaRecipeGuiLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

abstract class AbstractHexaliaJeiCategory<T> implements IRecipeCategory<T> {
   private final RecipeType<T> recipeType;
   private final Component title;
   private final IDrawable icon;
   private final HexaliaRecipeGuiLayout layout;

   protected AbstractHexaliaJeiCategory(IGuiHelper guiHelper, RecipeType<T> recipeType, String titleKey, ItemLike iconItem, HexaliaRecipeGuiLayout layout) {
      this.recipeType = recipeType;
      this.title = Component.translatable(titleKey);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack(iconItem));
      this.layout = layout;
   }

   public RecipeType<T> getRecipeType() {
      return this.recipeType;
   }

   public Component getTitle() {
      return this.title;
   }

   public int getWidth() {
      return this.layout.width();
   }

   public int getHeight() {
      return this.layout.height();
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      guiGraphics.blit(
         this.layout.texture(), 0, 0, this.layout.textureU(), this.layout.textureV(), this.layout.textureWidth(), this.layout.textureHeight(), 256, 256
      );
   }

   public boolean needsRecipeBorder() {
      return false;
   }
}
