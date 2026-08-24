package mezz.jei.library.plugins.vanilla.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public final class VanillaRecipes {
   private final RecipeManager recipeManager;
   private final IIngredientManager ingredientManager;

   public VanillaRecipes(IIngredientManager ingredientManager) {
      Minecraft minecraft = Minecraft.getInstance();
      ErrorUtil.checkNotNull(minecraft, "minecraft");
      ClientLevel world = minecraft.level;
      ErrorUtil.checkNotNull(world, "minecraft world");
      this.recipeManager = world.getRecipeManager();
      this.ingredientManager = ingredientManager;
   }

   public Map<Boolean, List<RecipeHolder<CraftingRecipe>>> getCraftingRecipes(IRecipeCategory<RecipeHolder<CraftingRecipe>> craftingCategory) {
      CategoryRecipeValidator<CraftingRecipe> validator = new CategoryRecipeValidator(craftingCategory, this.ingredientManager, 9);
      List<RecipeHolder<CraftingRecipe>> handled = new ArrayList<>();
      List<RecipeHolder<CraftingRecipe>> unhandled = new ArrayList<>();

      for (RecipeHolder<CraftingRecipe> recipe : this.recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
         if (validator.isRecipeValid(recipe)) {
            if (validator.isRecipeHandled(recipe)) {
               handled.add(recipe);
            } else {
               unhandled.add(recipe);
            }
         }
      }

      return Map.of(true, handled, false, unhandled);
   }

   public List<RecipeHolder<StonecutterRecipe>> getStonecuttingRecipes(IRecipeCategory<RecipeHolder<StonecutterRecipe>> stonecuttingCategory) {
      CategoryRecipeValidator<StonecutterRecipe> validator = new CategoryRecipeValidator(stonecuttingCategory, this.ingredientManager, 1);
      return getValidHandledRecipes(this.recipeManager, RecipeType.STONECUTTING, validator);
   }

   public List<RecipeHolder<SmeltingRecipe>> getFurnaceRecipes(IRecipeCategory<RecipeHolder<SmeltingRecipe>> furnaceCategory) {
      CategoryRecipeValidator<SmeltingRecipe> validator = new CategoryRecipeValidator(furnaceCategory, this.ingredientManager, 1);
      return getValidHandledRecipes(this.recipeManager, RecipeType.SMELTING, validator);
   }

   public List<RecipeHolder<SmokingRecipe>> getSmokingRecipes(IRecipeCategory<RecipeHolder<SmokingRecipe>> smokingCategory) {
      CategoryRecipeValidator<SmokingRecipe> validator = new CategoryRecipeValidator(smokingCategory, this.ingredientManager, 1);
      return getValidHandledRecipes(this.recipeManager, RecipeType.SMOKING, validator);
   }

   public List<RecipeHolder<BlastingRecipe>> getBlastingRecipes(IRecipeCategory<RecipeHolder<BlastingRecipe>> blastingCategory) {
      CategoryRecipeValidator<BlastingRecipe> validator = new CategoryRecipeValidator(blastingCategory, this.ingredientManager, 1);
      return getValidHandledRecipes(this.recipeManager, RecipeType.BLASTING, validator);
   }

   public List<RecipeHolder<CampfireCookingRecipe>> getCampfireCookingRecipes(IRecipeCategory<RecipeHolder<CampfireCookingRecipe>> campfireCategory) {
      CategoryRecipeValidator<CampfireCookingRecipe> validator = new CategoryRecipeValidator(campfireCategory, this.ingredientManager, 1);
      return getValidHandledRecipes(this.recipeManager, RecipeType.CAMPFIRE_COOKING, validator);
   }

   public List<RecipeHolder<SmithingRecipe>> getSmithingRecipes(IRecipeCategory<RecipeHolder<SmithingRecipe>> smithingCategory) {
      CategoryRecipeValidator<SmithingRecipe> validator = new CategoryRecipeValidator(smithingCategory, this.ingredientManager, 0);
      return getValidHandledRecipes(this.recipeManager, RecipeType.SMITHING, validator);
   }

   private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getValidHandledRecipes(
      RecipeManager recipeManager, RecipeType<T> recipeType, CategoryRecipeValidator<T> validator
   ) {
      return recipeManager.getAllRecipesFor(recipeType)
         .stream()
         .filter(r -> validator.isRecipeValid((RecipeHolder<T>)r) && validator.isRecipeHandled((RecipeHolder<T>)r))
         .toList();
   }
}
