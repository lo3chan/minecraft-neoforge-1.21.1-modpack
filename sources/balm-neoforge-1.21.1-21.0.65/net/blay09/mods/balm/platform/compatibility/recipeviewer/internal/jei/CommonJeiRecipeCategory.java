package net.blay09.mods.balm.platform.compatibility.recipeviewer.internal.jei;

import java.util.function.BiConsumer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerDisplaySlotsBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class CommonJeiRecipeCategory<T> implements IRecipeCategory<T> {
   private final RecipeType<T> recipeType;
   private final Component title;
   @Nullable
   private final IDrawable icon;
   private final int width;
   private final int height;
   @Nullable
   private final IDrawable background;
   private final BiConsumer<T, RecipeViewerDisplaySlotsBuilder> slotsBuilder;

   public CommonJeiRecipeCategory(
      RecipeType<T> recipeType,
      Component title,
      @Nullable IDrawable icon,
      int width,
      int height,
      @Nullable IDrawable background,
      BiConsumer<T, RecipeViewerDisplaySlotsBuilder> slotsBuilder
   ) {
      this.recipeType = recipeType;
      this.title = title;
      this.icon = icon;
      this.width = width;
      this.height = height;
      this.background = background;
      this.slotsBuilder = slotsBuilder;
   }

   public RecipeType<T> getRecipeType() {
      return this.recipeType;
   }

   public Component getTitle() {
      return this.title;
   }

   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
      this.slotsBuilder.accept(recipe, new JeiRecipeViewerDisplaySlotsBuilder(builder));
   }

   public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      if (this.background != null) {
         this.background.draw(guiGraphics);
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
