package mezz.jei.gui.input;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.world.item.ItemStack;

public interface IClickableIngredientInternal<T> {
   ITypedIngredient<T> getTypedIngredient();

   IElement<T> getElement();

   boolean isMouseOver(double var1, double var3);

   ItemStack getCheatItemStack(IIngredientManager var1);

   boolean canClickToFocus();

   default void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
      this.getElement().show(recipesGui, focusUtil, roles);
   }
}
