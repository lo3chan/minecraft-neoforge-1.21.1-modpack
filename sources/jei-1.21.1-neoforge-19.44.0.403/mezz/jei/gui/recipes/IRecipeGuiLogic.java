package mezz.jei.gui.recipes;

import java.util.List;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IRecipeGuiLogic {
   String getPageString();

   boolean hasMultipleCategories();

   boolean hasAllCategories();

   boolean previousRecipeCategory();

   int getRecipesPerPage();

   boolean nextRecipeCategory();

   void setRecipeCategory(IRecipeCategory<?> var1);

   boolean hasMultiplePages();

   void goToFirstPage();

   boolean previousPage();

   boolean nextPage();

   void tick();

   boolean showFocus(IFocusGroup var1);

   boolean showRecipes(IFocusedRecipes<?> var1, IFocusGroup var2);

   boolean back();

   void clearHistory();

   boolean showAllRecipes();

   boolean showCategories(List<RecipeType<?>> var1);

   IRecipeCategory<?> getSelectedRecipeCategory();

   @Unmodifiable
   List<IRecipeCategory<?>> getRecipeCategories();

   Stream<ITypedIngredient<?>> getRecipeCatalysts();

   Stream<ITypedIngredient<?>> getRecipeCatalysts(IRecipeCategory<?> var1);

   List<IRecipeLayoutWithButtons<?>> getVisibleRecipeLayoutsWithButtons(
      int var1, int var2, @Nullable AbstractContainerMenu var3, BookmarkList var4, RecipesGui var5
   );
}
