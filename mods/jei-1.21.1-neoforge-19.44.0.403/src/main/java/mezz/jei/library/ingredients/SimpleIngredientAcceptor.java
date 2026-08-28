/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnmodifiableView
 */
package mezz.jei.library.ingredients;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.TypedIngredient;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class SimpleIngredientAcceptor
implements IIngredientAcceptor<SimpleIngredientAcceptor> {
    private final IIngredientManager ingredientManager;
    private final List<ITypedIngredient<?>> ingredients = new ArrayList();

    public SimpleIngredientAcceptor(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    @Override
    public SimpleIngredientAcceptor addItemLike(ItemLike itemLike) {
        Preconditions.checkNotNull((Object)itemLike, (Object)"itemLike");
        ITypedIngredient<ItemStack> ingredient = TypedItemStack.create(itemLike);
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addIngredientsUnsafe(List<?> ingredients) {
        Preconditions.checkNotNull(ingredients, (Object)"ingredients");
        for (Object ingredient : ingredients) {
            @Nullable ITypedIngredient<?> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredient, false);
            if (typedIngredient == null) continue;
            this.ingredients.add(typedIngredient);
        }
        return this;
    }

    @Override
    public <T> SimpleIngredientAcceptor addIngredients(IIngredientType<T> ingredientType, List<@Nullable T> ingredients) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        Preconditions.checkNotNull(ingredients, (Object)"ingredients");
        List<@Nullable ITypedIngredient<T>> typedIngredients = TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredientType, ingredients, false);
        for (ITypedIngredient<T> typedIngredientOptional : typedIngredients) {
            if (typedIngredientOptional == null) continue;
            this.ingredients.add(typedIngredientOptional);
        }
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addItemStack(ItemStack itemStack) {
        ErrorUtil.checkNotNull(itemStack, "itemStack");
        this.addIngredientInternal(VanillaTypes.ITEM_STACK, itemStack);
        return this;
    }

    @Override
    public <T> SimpleIngredientAcceptor addIngredient(IIngredientType<T> ingredientType, T ingredient) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        this.addIngredientInternal(ingredientType, ingredient);
        return this;
    }

    @Override
    public <I> SimpleIngredientAcceptor addTypedIngredient(ITypedIngredient<I> typedIngredient) {
        ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
        @Nullable ITypedIngredient<I> copy = TypedIngredient.defensivelyCopyTypedIngredientFromApi(this.ingredientManager, typedIngredient);
        if (copy != null) {
            this.ingredients.add(copy);
        }
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addFluidStack(Fluid fluid) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), fluidHelper.bucketVolume(), DataComponentPatch.EMPTY);
    }

    @Override
    public SimpleIngredientAcceptor addFluidStack(Fluid fluid, long amount) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), amount, DataComponentPatch.EMPTY);
    }

    @Override
    public SimpleIngredientAcceptor addFluidStack(Fluid fluid, long amount, DataComponentPatch component) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), amount, component);
    }

    private <T> SimpleIngredientAcceptor addFluidInternal(IPlatformFluidHelperInternal<T> fluidHelper, Holder<Fluid> fluidHolder, long amount, DataComponentPatch component) {
        Object fluidStack = fluidHelper.create(fluidHolder, amount, component);
        IIngredientTypeWithSubtypes fluidIngredientType = fluidHelper.getFluidIngredientType();
        this.addIngredientInternal(fluidIngredientType, fluidStack);
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        for (ITypedIngredient<?> typedIngredient : ingredients) {
            this.addTypedIngredient(typedIngredient);
        }
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        for (Optional<ITypedIngredient<?>> optionalTypedIngredient : ingredients) {
            if (!optionalTypedIngredient.isPresent()) continue;
            this.ingredients.add(optionalTypedIngredient.get());
        }
        return this;
    }

    @Override
    public SimpleIngredientAcceptor addItemStacks(List<ItemStack> itemStacks) {
        return this.addIngredients((IIngredientType)VanillaTypes.ITEM_STACK, itemStacks);
    }

    private <T> void addIngredientInternal(IIngredientType<T> ingredientType, @Nullable T ingredient) {
        if (ingredient == null) {
            return;
        }
        @Nullable ITypedIngredient<T> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredientType, ingredient, false);
        if (typedIngredient != null) {
            this.ingredients.add(typedIngredient);
        }
    }

    public @UnmodifiableView List<ITypedIngredient<?>> getAllIngredients() {
        return Collections.unmodifiableList(this.ingredients);
    }
}

