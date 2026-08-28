/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.api.registration;

import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IRecipeCatalystRegistration {
    public IIngredientManager getIngredientManager();

    public IJeiHelpers getJeiHelpers();

    public void addRecipeCatalysts(RecipeType<?> var1, ItemLike ... var2);

    default public void addRecipeCatalysts(RecipeType<?> recipeType, ItemStack ... ingredients) {
        this.addRecipeCatalysts(recipeType, VanillaTypes.ITEM_STACK, List.of(ingredients));
    }

    public <T> void addRecipeCatalysts(RecipeType<?> var1, IIngredientType<T> var2, List<T> var3);

    default public void addRecipeCatalyst(ItemLike itemLike, RecipeType<?> ... recipeTypes) {
        this.addRecipeCatalyst(VanillaTypes.ITEM_STACK, itemLike.asItem().getDefaultInstance(), recipeTypes);
    }

    default public void addRecipeCatalyst(ItemStack ingredient, RecipeType<?> ... recipeTypes) {
        this.addRecipeCatalyst(VanillaTypes.ITEM_STACK, ingredient, recipeTypes);
    }

    public <T> void addRecipeCatalyst(IIngredientType<T> var1, T var2, RecipeType<?> ... var3);
}

