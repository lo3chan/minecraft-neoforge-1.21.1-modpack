/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.recipes.lookups;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.util.MathUtil;
import mezz.jei.gui.recipes.RecipeSortUtil;
import mezz.jei.gui.recipes.lookups.FocusedRecipes;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import mezz.jei.gui.recipes.lookups.ILookupState;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class IngredientLookupState
implements ILookupState {
    private final IRecipeManager recipeManager;
    private final IFocusGroup focuses;
    private final @Unmodifiable List<IRecipeCategory<?>> recipeCategories;
    private int recipeCategoryIndex = 0;
    private int recipeIndex = 0;
    private int recipesPerPage = 1;
    @Nullable
    private IFocusedRecipes<?> focusedRecipes;

    public static ILookupState create(IRecipeManager recipeManager, IFocusGroup focusGroup, List<IRecipeCategory<?>> recipeCategories, IRecipeTransferManager recipeTransferManager) {
        recipeCategories = RecipeSortUtil.sortRecipeCategories(recipeCategories, recipeTransferManager);
        return new IngredientLookupState(recipeManager, focusGroup, recipeCategories);
    }

    private IngredientLookupState(IRecipeManager recipeManager, IFocusGroup focuses, List<IRecipeCategory<?>> recipeCategories) {
        this.recipeManager = recipeManager;
        this.focuses = focuses;
        this.recipeCategories = Collections.unmodifiableList(recipeCategories);
    }

    @Override
    public IFocusGroup getFocuses() {
        return this.focuses;
    }

    @Override
    public @Unmodifiable List<IRecipeCategory<?>> getRecipeCategories() {
        return this.recipeCategories;
    }

    public int getRecipeCategoryIndex() {
        return this.recipeCategoryIndex;
    }

    @Override
    public boolean moveToRecipeCategory(IRecipeCategory<?> recipeCategory) {
        int recipeCategoryIndex = this.recipeCategories.indexOf(recipeCategory);
        if (recipeCategoryIndex >= 0) {
            this.moveToRecipeCategoryIndex(recipeCategoryIndex);
            return true;
        }
        return false;
    }

    private boolean moveToRecipeCategoryIndex(int recipeCategoryIndex) {
        Preconditions.checkArgument((recipeCategoryIndex >= 0 ? 1 : 0) != 0, (Object)"Recipe category index cannot be negative.");
        if (this.recipeCategoryIndex != recipeCategoryIndex) {
            this.recipeCategoryIndex = recipeCategoryIndex;
            this.recipeIndex = 0;
            this.focusedRecipes = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean nextRecipeCategory() {
        int recipesTypesCount = this.getRecipeCategories().size();
        return this.moveToRecipeCategoryIndex((this.getRecipeCategoryIndex() + 1) % recipesTypesCount);
    }

    @Override
    public boolean previousRecipeCategory() {
        int recipesTypesCount = this.getRecipeCategories().size();
        return this.moveToRecipeCategoryIndex((recipesTypesCount + this.getRecipeCategoryIndex() - 1) % recipesTypesCount);
    }

    @Override
    public void goToFirstPage() {
        this.recipeIndex = 0;
    }

    @Override
    public boolean nextPage() {
        int originalIndex = this.recipeIndex;
        int recipeCount = this.recipeCount();
        this.recipeIndex += this.recipesPerPage;
        if (this.recipeIndex >= recipeCount) {
            this.recipeIndex = 0;
        }
        return this.recipeIndex != originalIndex;
    }

    @Override
    public boolean previousPage() {
        int originalIndex = this.recipeIndex;
        this.recipeIndex -= this.recipesPerPage;
        if (this.recipeIndex < 0) {
            int pageCount = this.pageCount();
            this.recipeIndex = (pageCount - 1) * this.recipesPerPage;
        }
        return this.recipeIndex != originalIndex;
    }

    public int recipeCount() {
        return this.getFocusedRecipes().getRecipes().size();
    }

    @Override
    public int pageCount() {
        int recipeCount = this.recipeCount();
        if (recipeCount <= 1) {
            return 1;
        }
        return MathUtil.divideCeil(recipeCount, this.recipesPerPage);
    }

    @Override
    public int getRecipeIndex() {
        return this.recipeIndex;
    }

    @Override
    public int getRecipesPerPage() {
        return this.recipesPerPage;
    }

    @Override
    public void setRecipesPerPage(int recipesPerPage) {
        this.recipesPerPage = recipesPerPage;
    }

    @Override
    public IFocusedRecipes<?> getFocusedRecipes() {
        if (this.focusedRecipes == null) {
            IRecipeCategory<?> recipeCategory = this.recipeCategories.get(this.recipeCategoryIndex);
            this.focusedRecipes = FocusedRecipes.create(this.focuses, this.recipeManager, recipeCategory);
        }
        return this.focusedRecipes;
    }
}

