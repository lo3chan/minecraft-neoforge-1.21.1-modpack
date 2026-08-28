/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.registration;

import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.extensions.vanilla.brewing.IExtendableBrewingRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.IExtendableSmithingRecipeCategory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IVanillaCategoryExtensionRegistration {
    public IJeiHelpers getJeiHelpers();

    public IExtendableCraftingRecipeCategory getCraftingCategory();

    public IExtendableSmithingRecipeCategory getSmithingCategory();

    public IExtendableBrewingRecipeCategory getBrewingCategory();
}

