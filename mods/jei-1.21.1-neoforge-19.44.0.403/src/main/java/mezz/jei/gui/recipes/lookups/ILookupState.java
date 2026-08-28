/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.layouts.IRecipeLayoutList;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;

public interface ILookupState {
    public List<IRecipeCategory<?>> getRecipeCategories();

    public boolean moveToRecipeCategory(IRecipeCategory<?> var1);

    public int getRecipesPerPage();

    public void setRecipesPerPage(int var1);

    public int getRecipeIndex();

    public IFocusGroup getFocuses();

    public IFocusedRecipes<?> getFocusedRecipes();

    public boolean nextRecipeCategory();

    public boolean previousRecipeCategory();

    public void goToFirstPage();

    public boolean nextPage();

    public boolean previousPage();

    public int pageCount();

    default public List<IRecipeLayoutWithButtons<?>> getVisible(IRecipeLayoutList recipes) {
        int maxIndex;
        int recipesPerPage = this.getRecipesPerPage();
        int firstRecipeIndex = this.getRecipeIndex() - this.getRecipeIndex() % recipesPerPage;
        if (firstRecipeIndex >= (maxIndex = Math.min(recipes.size(), firstRecipeIndex + recipesPerPage))) {
            return List.of();
        }
        return recipes.subList(firstRecipeIndex, maxIndex);
    }
}

