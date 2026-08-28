/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.gui.builder;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface IIngredientConsumer {
    public <I> IIngredientConsumer addIngredients(IIngredientType<I> var1, List<@Nullable I> var2);

    public <I> IIngredientConsumer addIngredient(IIngredientType<I> var1, I var2);

    public IIngredientConsumer addIngredientsUnsafe(List<?> var1);

    default public IIngredientConsumer addIngredients(Ingredient ingredient) {
        return this.addIngredients(VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()));
    }

    default public <I> IIngredientConsumer addTypedIngredient(ITypedIngredient<I> typedIngredient) {
        return this.addIngredient(typedIngredient.getType(), typedIngredient.getIngredient());
    }

    public IIngredientConsumer addTypedIngredients(List<ITypedIngredient<?>> var1);

    public IIngredientConsumer addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> var1);

    default public IIngredientConsumer addItemStacks(List<ItemStack> itemStacks) {
        return this.addIngredients(VanillaTypes.ITEM_STACK, itemStacks);
    }

    default public IIngredientConsumer addItemStack(ItemStack itemStack) {
        return this.addIngredient(VanillaTypes.ITEM_STACK, itemStack);
    }

    default public IIngredientConsumer addItemLike(ItemLike itemLike) {
        return this.addItemStack(itemLike.asItem().getDefaultInstance());
    }

    public IIngredientConsumer addFluidStack(Fluid var1);

    public IIngredientConsumer addFluidStack(Fluid var1, long var2);

    public IIngredientConsumer addFluidStack(Fluid var1, long var2, DataComponentPatch var4);
}

