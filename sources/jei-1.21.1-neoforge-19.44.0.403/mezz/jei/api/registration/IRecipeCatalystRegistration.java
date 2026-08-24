package mezz.jei.api.registration;

import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IRecipeCatalystRegistration {
   IIngredientManager getIngredientManager();

   IJeiHelpers getJeiHelpers();

   void addRecipeCatalysts(RecipeType<?> var1, ItemLike... var2);

   default void addRecipeCatalysts(RecipeType<?> recipeType, ItemStack... ingredients) {
      this.addRecipeCatalysts(recipeType, VanillaTypes.ITEM_STACK, List.of(ingredients));
   }

   <T> void addRecipeCatalysts(RecipeType<?> var1, IIngredientType<T> var2, List<T> var3);

   default void addRecipeCatalyst(ItemLike itemLike, RecipeType<?>... recipeTypes) {
      this.addRecipeCatalyst(VanillaTypes.ITEM_STACK, itemLike.asItem().getDefaultInstance(), recipeTypes);
   }

   default void addRecipeCatalyst(ItemStack ingredient, RecipeType<?>... recipeTypes) {
      this.addRecipeCatalyst(VanillaTypes.ITEM_STACK, ingredient, recipeTypes);
   }

   <T> void addRecipeCatalyst(IIngredientType<T> var1, T var2, RecipeType<?>... var3);
}
