package mezz.jei.api.registration;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientFilter;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.api.search.ISearchStorageFactory;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IRuntimeRegistration {
   void setIngredientListOverlay(IIngredientListOverlay var1);

   void setBookmarkOverlay(IBookmarkOverlay var1);

   void setRecipesGui(IRecipesGui var1);

   void setIngredientFilter(IIngredientFilter var1);

   IRecipeManager getRecipeManager();

   IJeiHelpers getJeiHelpers();

   IIngredientManager getIngredientManager();

   @Deprecated(
      since = "19.18.4",
      forRemoval = true
   )
   default IIngredientVisibility getIngredientVisibility() {
      return this.getJeiHelpers().getIngredientVisibility();
   }

   IScreenHelper getScreenHelper();

   IRecipeTransferManager getRecipeTransferManager();

   IEditModeConfig getEditModeConfig();

   @Deprecated(
      since = "19.41.0",
      forRemoval = true
   )
   ISearchStorageFactory getSearchStorageFactory();

   ISearchStorageBuilderFactory getSearchStorageBuilderFactory();
}
