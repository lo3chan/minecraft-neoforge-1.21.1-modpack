/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
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
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IRuntimeRegistration {
    public void setIngredientListOverlay(IIngredientListOverlay var1);

    public void setBookmarkOverlay(IBookmarkOverlay var1);

    public void setRecipesGui(IRecipesGui var1);

    public void setIngredientFilter(IIngredientFilter var1);

    public IRecipeManager getRecipeManager();

    public IJeiHelpers getJeiHelpers();

    public IIngredientManager getIngredientManager();

    @Deprecated(since="19.18.4", forRemoval=true)
    default public IIngredientVisibility getIngredientVisibility() {
        return this.getJeiHelpers().getIngredientVisibility();
    }

    public IScreenHelper getScreenHelper();

    public IRecipeTransferManager getRecipeTransferManager();

    public IEditModeConfig getEditModeConfig();

    @Deprecated(since="19.41.0", forRemoval=true)
    public ISearchStorageFactory getSearchStorageFactory();

    public ISearchStorageBuilderFactory getSearchStorageBuilderFactory();
}

