/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.recipe.category.extensions.vanilla.brewing;

import mezz.jei.api.recipe.category.extensions.vanilla.brewing.IBrewingCategoryExtension;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IExtendableBrewingRecipeCategory {
    public <R> void addExtension(Class<? extends R> var1, IBrewingCategoryExtension<R> var2);
}

