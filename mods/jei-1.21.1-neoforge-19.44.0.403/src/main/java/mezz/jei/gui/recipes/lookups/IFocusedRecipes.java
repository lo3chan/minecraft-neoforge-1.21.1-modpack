/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.recipes.lookups;

import java.util.List;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Unmodifiable;

public interface IFocusedRecipes<T> {
    public IRecipeCategory<T> getRecipeCategory();

    public @Unmodifiable List<T> getRecipes();
}

