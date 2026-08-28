/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.recipe.category.extensions.vanilla.brewing;

import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;

@FunctionalInterface
public interface IBrewingCategoryExtension<R> {
    public List<IJeiBrewingRecipe> getBrewingRecipes(R var1, IVanillaRecipeFactory var2);
}

