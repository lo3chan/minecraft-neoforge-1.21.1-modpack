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
import mezz.jei.api.gui.builder.IIngredientConsumer;
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
public interface IIngredientAcceptor<THIS extends IIngredientAcceptor<THIS>>
extends IIngredientConsumer {
    public <I> THIS addIngredients(IIngredientType<I> var1, List<@Nullable I> var2);

    public <I> THIS addIngredient(IIngredientType<I> var1, I var2);

    public THIS addIngredientsUnsafe(List<?> var1);

    default public THIS addIngredients(Ingredient ingredient) {
        return (THIS)this.addIngredients((IIngredientType)VanillaTypes.ITEM_STACK, List.of(ingredient.getItems()));
    }

    default public <I> THIS addTypedIngredient(ITypedIngredient<I> typedIngredient) {
        return (THIS)this.addIngredient((IIngredientType)typedIngredient.getType(), (Object)typedIngredient.getIngredient());
    }

    public THIS addTypedIngredients(List<ITypedIngredient<?>> var1);

    public THIS addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> var1);

    default public THIS addItemStacks(List<ItemStack> itemStacks) {
        return (THIS)this.addIngredients((IIngredientType)VanillaTypes.ITEM_STACK, itemStacks);
    }

    default public THIS addItemStack(ItemStack itemStack) {
        return (THIS)this.addIngredient((IIngredientType)VanillaTypes.ITEM_STACK, itemStack);
    }

    @Override
    default public IIngredientConsumer addItemLike(ItemLike itemLike) {
        return this.addItemStack(itemLike.asItem().getDefaultInstance());
    }

    public THIS addFluidStack(Fluid var1);

    public THIS addFluidStack(Fluid var1, long var2);

    public THIS addFluidStack(Fluid var1, long var2, DataComponentPatch var4);
}

