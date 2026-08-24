package mezz.jei.api.recipe.category;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public interface IRecipeCategory<T> {
   RecipeType<T> getRecipeType();

   Component getTitle();

   @Deprecated(
      since = "19.19.0",
      forRemoval = true
   )
   @Nullable
   default IDrawable getBackground() {
      return null;
   }

   default int getWidth() {
      IDrawable background = this.getBackground();
      if (background == null) {
         throw new IllegalStateException("getWidth() and getHeight() must be overridden if background is null");
      } else {
         return background.getWidth();
      }
   }

   default int getHeight() {
      IDrawable background = this.getBackground();
      if (background == null) {
         throw new IllegalStateException("getWidth() and getHeight() must be overridden if background is null");
      } else {
         return background.getHeight();
      }
   }

   @Nullable
   IDrawable getIcon();

   void setRecipe(IRecipeLayoutBuilder var1, T var2, IFocusGroup var3);

   @Deprecated(
      since = "19.19.3",
      forRemoval = true
   )
   default void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IRecipeSlotsView recipeSlotsView, IFocusGroup focuses) {
   }

   default void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
      this.createRecipeExtras(builder, recipe, () -> Collections.unmodifiableList(builder.getRecipeSlots().getSlots()), focuses);
   }

   default void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
   }

   default void onDisplayedIngredientsUpdate(T recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
   }

   @Deprecated(
      since = "19.5.4",
      forRemoval = true
   )
   default List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      return List.of();
   }

   default void getTooltip(ITooltipBuilder tooltip, T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      List<Component> tooltipStrings = this.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
      tooltip.addAll(tooltipStrings);
   }

   @Deprecated(
      since = "19.6.0",
      forRemoval = true
   )
   default boolean handleInput(T recipe, double mouseX, double mouseY, Key input) {
      return false;
   }

   default boolean isHandled(T recipe) {
      return true;
   }

   @Nullable
   default ResourceLocation getRegistryName(T recipe) {
      return recipe instanceof RecipeHolder<?> recipeHolder ? recipeHolder.id() : null;
   }

   default Codec<T> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
      RecipeType<T> recipeType = this.getRecipeType();
      return RecipeHolder.class.isAssignableFrom(recipeType.getRecipeClass())
         ? codecHelper.getRecipeHolderCodec()
         : codecHelper.getSlowRecipeCategoryCodec(this, recipeManager);
   }

   default boolean needsRecipeBorder() {
      return true;
   }
}
