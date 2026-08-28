/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.recipe.category.extensions;

import java.util.function.Function;
import java.util.function.Predicate;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;

@Deprecated(since="16.0.0", forRemoval=true)
public interface IExtendableRecipeCategory<T, W extends IRecipeCategoryExtension<T>>
extends IRecipeCategory<T> {
    @Deprecated(since="16.0.0", forRemoval=true)
    public <R extends T> void addCategoryExtension(Class<? extends R> var1, Function<R, ? extends W> var2);

    @Deprecated(since="16.0.0", forRemoval=true)
    public <R extends T> void addCategoryExtension(Class<? extends R> var1, Predicate<R> var2, Function<R, ? extends W> var3);
}

