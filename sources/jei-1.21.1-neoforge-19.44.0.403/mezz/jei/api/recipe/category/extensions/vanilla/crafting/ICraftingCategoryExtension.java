package mezz.jei.api.recipe.category.extensions.vanilla.crafting;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public interface ICraftingCategoryExtension<R extends CraftingRecipe> extends IRecipeCategoryExtension<RecipeHolder<R>> {
   default void setRecipe(RecipeHolder<R> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
      this.setRecipe(builder, craftingGridHelper, focuses);
   }

   default void onDisplayedIngredientsUpdate(RecipeHolder<R> recipeHolder, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
   }

   @Deprecated(
      since = "19.4.1",
      forRemoval = true
   )
   default Optional<ResourceLocation> getRegistryName(RecipeHolder<R> recipeHolder) {
      return Optional.ofNullable(this.getRegistryName()).or(() -> Optional.of(recipeHolder.id()));
   }

   default int getWidth(RecipeHolder<R> recipeHolder) {
      return this.getWidth();
   }

   default int getHeight(RecipeHolder<R> recipeHolder) {
      return this.getHeight();
   }

   @Deprecated(
      since = "16.0.0",
      forRemoval = true
   )
   default void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
   }

   @Deprecated(
      since = "16.0.0",
      forRemoval = true
   )
   @Nullable
   default ResourceLocation getRegistryName() {
      return null;
   }

   @Deprecated(
      since = "16.0.0",
      forRemoval = true
   )
   default int getWidth() {
      return 0;
   }

   @Deprecated(
      since = "16.0.0",
      forRemoval = true
   )
   default int getHeight() {
      return 0;
   }
}
