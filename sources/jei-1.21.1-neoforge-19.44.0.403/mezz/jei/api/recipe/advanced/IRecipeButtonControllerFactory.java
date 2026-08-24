package mezz.jei.api.recipe.advanced;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.buttons.IIconButtonController;
import org.jetbrains.annotations.Nullable;

public interface IRecipeButtonControllerFactory {
   @Nullable
   <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> var1);
}
