/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.recipes;

import java.util.List;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface IRecipeGuiLogic {
    public String getPageString();

    public boolean hasMultipleCategories();

    public boolean hasAllCategories();

    public boolean previousRecipeCategory();

    public int getRecipesPerPage();

    public boolean nextRecipeCategory();

    public void setRecipeCategory(IRecipeCategory<?> var1);

    public boolean hasMultiplePages();

    public void goToFirstPage();

    public boolean previousPage();

    public boolean nextPage();

    public void tick();

    public boolean showFocus(IFocusGroup var1);

    public boolean showRecipes(IFocusedRecipes<?> var1, IFocusGroup var2);

    public boolean back();

    public void clearHistory();

    public boolean showAllRecipes();

    public boolean showCategories(List<RecipeType<?>> var1);

    public IRecipeCategory<?> getSelectedRecipeCategory();

    public @Unmodifiable List<IRecipeCategory<?>> getRecipeCategories();

    public Stream<ITypedIngredient<?>> getRecipeCatalysts();

    public Stream<ITypedIngredient<?>> getRecipeCatalysts(IRecipeCategory<?> var1);

    public List<IRecipeLayoutWithButtons<?>> getVisibleRecipeLayoutsWithButtons(int var1, int var2, @Nullable AbstractContainerMenu var3, BookmarkList var4, RecipesGui var5);
}

