package mezz.jei.gui.overlay.elements;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.ingredients.IngredientGridTooltipHelper;
import mezz.jei.gui.util.FocusUtil;
import org.jetbrains.annotations.Nullable;

public interface IElement<T> {
   ITypedIngredient<T> getTypedIngredient();

   Optional<IBookmark> getBookmark();

   @Nullable
   IDrawable createRenderOverlay();

   void show(IRecipesGui var1, FocusUtil var2, List<RecipeIngredientRole> var3);

   void getTooltip(JeiTooltip var1, IngredientGridTooltipHelper var2, IIngredientRenderer<T> var3, IIngredientHelper<T> var4);

   boolean isVisible();

   void tick();

   default boolean handleClick(UserInput input, IInternalKeyMappings keyBindings) {
      return false;
   }
}
