package mezz.jei.library.plugins.vanilla.crafting;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CraftingCategoryExtension implements ICraftingCategoryExtension<CraftingRecipe> {
   @Override
   public void setRecipe(RecipeHolder<CraftingRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
      CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
      ItemStack resultItem = RecipeUtil.getResultItem(recipe);
      int width = this.getWidth(recipeHolder);
      int height = this.getHeight(recipeHolder);
      craftingGridHelper.createAndSetOutputs(builder, List.of(resultItem));
      craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), width, height);
   }

   @Override
   public Optional<ResourceLocation> getRegistryName(RecipeHolder<CraftingRecipe> recipeHolder) {
      return Optional.of(recipeHolder.id());
   }

   @Override
   public int getWidth(RecipeHolder<CraftingRecipe> recipeHolder) {
      CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
      if (recipe instanceof ShapedRecipe shapedRecipe) {
         return shapedRecipe.getWidth();
      } else {
         return recipe instanceof JeiShapedRecipe shapedRecipe ? shapedRecipe.getWidth() : 0;
      }
   }

   @Override
   public int getHeight(RecipeHolder<CraftingRecipe> recipeHolder) {
      CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
      if (recipe instanceof ShapedRecipe shapedRecipe) {
         return shapedRecipe.getHeight();
      } else {
         return recipe instanceof JeiShapedRecipe shapedRecipe ? shapedRecipe.getHeight() : 0;
      }
   }

   public boolean isHandled(RecipeHolder<CraftingRecipe> recipeHolder) {
      CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
      return !recipe.isSpecial();
   }
}
