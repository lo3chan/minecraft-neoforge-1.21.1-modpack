/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.api.registration;

import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IRecipeRegistration {
    public IJeiHelpers getJeiHelpers();

    public IIngredientManager getIngredientManager();

    public IVanillaRecipeFactory getVanillaRecipeFactory();

    @Deprecated(since="19.18.4", forRemoval=true)
    default public IIngredientVisibility getIngredientVisibility() {
        return this.getJeiHelpers().getIngredientVisibility();
    }

    public <T> void addRecipes(RecipeType<T> var1, List<T> var2);

    public <T> void addIngredientInfo(T var1, IIngredientType<T> var2, Component ... var3);

    public <T> void addIngredientInfo(List<T> var1, IIngredientType<T> var2, Component ... var3);

    default public void addIngredientInfo(ItemLike itemLike, Component ... descriptionComponents) {
        this.addIngredientInfo(itemLike.asItem().getDefaultInstance(), VanillaTypes.ITEM_STACK, descriptionComponents);
    }

    default public void addItemStackInfo(ItemStack ingredient, Component ... descriptionComponents) {
        this.addIngredientInfo(ingredient, VanillaTypes.ITEM_STACK, descriptionComponents);
    }

    default public void addItemStackInfo(List<ItemStack> ingredients, Component ... descriptionComponents) {
        this.addIngredientInfo((List)ingredients, (IIngredientType)VanillaTypes.ITEM_STACK, descriptionComponents);
    }
}

