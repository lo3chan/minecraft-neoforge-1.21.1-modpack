/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes.layouts;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.common.config.RecipeSorterStage;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.gui.recipes.layouts.LazyRecipeLayoutList;
import mezz.jei.gui.recipes.lookups.IFocusedRecipes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public interface IRecipeLayoutList {
    public static IRecipeLayoutList create(Set<RecipeSorterStage> recipeSorterStages, @Nullable AbstractContainerMenu container, IFocusedRecipes<?> selectedRecipes, IFocusGroup focusGroup, BookmarkList bookmarkList, IRecipeManager recipeManager, RecipesGui recipesGui) {
        return new LazyRecipeLayoutList(recipeSorterStages, container, selectedRecipes, bookmarkList, recipeManager, recipesGui, focusGroup);
    }

    public int size();

    public List<IRecipeLayoutWithButtons<?>> subList(int var1, int var2);

    public Optional<IRecipeLayoutWithButtons<?>> findFirst();

    public void tick();
}

