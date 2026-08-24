package mezz.jei.common.platform;

import java.util.List;
import mezz.jei.api.recipe.category.extensions.vanilla.brewing.IExtendableBrewingRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.recipes.BrewingExtensionHelper;
import net.minecraft.world.item.alchemy.PotionBrewing;

public interface IPlatformBrewingHelper {
   default void registerCategoryExtensions(IExtendableBrewingRecipeCategory brewingCategory, IIngredientManager ingredientManager) {
   }

   List<IJeiBrewingRecipe> getBrewingRecipes(IIngredientManager var1, IVanillaRecipeFactory var2, PotionBrewing var3, BrewingExtensionHelper var4);
}
