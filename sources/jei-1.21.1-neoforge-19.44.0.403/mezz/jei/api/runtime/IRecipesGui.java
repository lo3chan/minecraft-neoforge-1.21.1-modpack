package mezz.jei.api.runtime;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.screens.Screen;

public interface IRecipesGui {
   default <V> void show(IFocus<V> focus) {
      this.show(List.of(focus));
   }

   void show(List<IFocus<?>> var1);

   void showTypes(List<RecipeType<?>> var1);

   <T> void showRecipes(IRecipeCategory<T> var1, List<T> var2, List<IFocus<?>> var3);

   <T> Optional<T> getIngredientUnderMouse(IIngredientType<T> var1);

   Optional<Screen> getParentScreen();
}
