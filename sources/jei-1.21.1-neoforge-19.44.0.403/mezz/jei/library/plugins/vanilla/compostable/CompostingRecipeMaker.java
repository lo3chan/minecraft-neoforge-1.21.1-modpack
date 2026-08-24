package mezz.jei.library.plugins.vanilla.compostable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CompostingRecipeMaker {
   public static List<IJeiCompostingRecipe> getRecipes(IIngredientManager ingredientManager) {
      Collection<ItemStack> allIngredients = ingredientManager.getAllItemStacks();
      IIngredientHelper<ItemStack> ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
      IPlatformIngredientHelper platformIngredientHelper = Services.PLATFORM.getIngredientHelper();
      return allIngredients.stream().<IJeiCompostingRecipe>mapMulti((itemStack, consumer) -> {
         float compostValue = platformIngredientHelper.getCompostValue(itemStack);
         if (compostValue > 0.0F) {
            ResourceLocation resourceLocation = ingredientHelper.getResourceLocation(itemStack);
            String ingredientUidPath = resourceLocation.getPath();
            ResourceLocation recipeUid = ResourceLocation.fromNamespaceAndPath("jei", ingredientUidPath);
            CompostingRecipe recipe = new CompostingRecipe(itemStack, compostValue, recipeUid);
            consumer.accept(recipe);
         }
      }).sorted(Comparator.comparingDouble(IJeiCompostingRecipe::getChance)).toList();
   }
}
