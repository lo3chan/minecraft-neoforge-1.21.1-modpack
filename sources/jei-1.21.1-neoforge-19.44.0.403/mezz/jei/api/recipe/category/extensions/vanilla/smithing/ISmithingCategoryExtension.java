package mezz.jei.api.recipe.category.extensions.vanilla.smithing;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.world.item.crafting.SmithingRecipe;

public interface ISmithingCategoryExtension<R extends SmithingRecipe> {
   <T extends IIngredientAcceptor<T>> void setTemplate(R var1, T var2);

   <T extends IIngredientAcceptor<T>> void setBase(R var1, T var2);

   <T extends IIngredientAcceptor<T>> void setAddition(R var1, T var2);

   default <T extends IIngredientAcceptor<T>> void setOutput(R recipe, T ingredientAcceptor) {
   }

   default void onDisplayedIngredientsUpdate(
      R recipe,
      IRecipeSlotDrawable templateSlot,
      IRecipeSlotDrawable baseSlot,
      IRecipeSlotDrawable additionSlot,
      IRecipeSlotDrawable outputSlot,
      IFocusGroup focuses
   ) {
   }
}
