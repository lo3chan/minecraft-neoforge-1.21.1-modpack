/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.registration;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.advanced.IRecipeManagerPluginHelper;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IJeiFeatures;

public interface IAdvancedRegistration {
    public IJeiHelpers getJeiHelpers();

    public IRecipeManagerPluginHelper getRecipeManagerPluginHelper();

    public void addRecipeManagerPlugin(IRecipeManagerPlugin var1);

    public <T> void addTypedRecipeManagerPlugin(RecipeType<T> var1, ISimpleRecipeManagerPlugin<T> var2);

    public <T> void addRecipeCategoryDecorator(RecipeType<T> var1, IRecipeCategoryDecorator<T> var2);

    public void addRecipeButtonFactory(IRecipeButtonControllerFactory var1);

    @Deprecated(forRemoval=true, since="19.42.0")
    public IJeiFeatures getJeiFeatures();
}

