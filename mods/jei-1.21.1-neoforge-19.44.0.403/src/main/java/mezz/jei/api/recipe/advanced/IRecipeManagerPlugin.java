/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.recipe.advanced;

import java.util.List;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public interface IRecipeManagerPlugin {
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> var1);

    public <T, V> List<T> getRecipes(IRecipeCategory<T> var1, IFocus<V> var2);

    public <T> List<T> getRecipes(IRecipeCategory<T> var1);
}

