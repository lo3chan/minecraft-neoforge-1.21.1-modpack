/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.UnmodifiableView
 */
package mezz.jei.library.ingredients;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.TypedIngredient;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class DisplayIngredientAcceptor
implements IIngredientAcceptor<DisplayIngredientAcceptor> {
    private final IIngredientManager ingredientManager;
    private final List<@Nullable ITypedIngredient<?>> ingredients = new ArrayList();

    public DisplayIngredientAcceptor(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    @Override
    public DisplayIngredientAcceptor addIngredientsUnsafe(List<?> ingredients) {
        Preconditions.checkNotNull(ingredients, (Object)"ingredients");
        for (Object ingredient : ingredients) {
            @Nullable ITypedIngredient<?> typedIngredient = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredient, false);
            this.ingredients.add(typedIngredient);
        }
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addItemStack(ItemStack itemStack) {
        ErrorUtil.checkNotNull(itemStack, "itemStack");
        this.addIngredientInternal(VanillaTypes.ITEM_STACK, itemStack);
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addItemStacks(List<ItemStack> itemStacks) {
        return this.addIngredients((IIngredientType)VanillaTypes.ITEM_STACK, itemStacks);
    }

    @Override
    public <T> DisplayIngredientAcceptor addIngredients(IIngredientType<T> ingredientType, List<@Nullable T> ingredients) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        Preconditions.checkNotNull(ingredients, (Object)"ingredients");
        List<@Nullable ITypedIngredient<T>> typedIngredients = TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredientType, ingredients, false);
        this.ingredients.addAll(typedIngredients);
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addIngredients(Ingredient ingredient) {
        Preconditions.checkNotNull((Object)ingredient, (Object)"ingredient");
        List<@Nullable ITypedIngredient<ItemStack>> typedIngredients = TypedIngredient.createAndFilterInvalidList(this.ingredientManager, ingredient, false);
        this.ingredients.addAll(typedIngredients);
        return this;
    }

    @Override
    public <T> DisplayIngredientAcceptor addIngredient(IIngredientType<T> ingredientType, T ingredient) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        this.addIngredientInternal(ingredientType, ingredient);
        return this;
    }

    @Override
    public <I> DisplayIngredientAcceptor addTypedIngredient(ITypedIngredient<I> typedIngredient) {
        ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
        @Nullable ITypedIngredient<I> copy = TypedIngredient.defensivelyCopyTypedIngredientFromApi(this.ingredientManager, typedIngredient);
        this.ingredients.add(copy);
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addItemLike(ItemLike itemLike) {
        Preconditions.checkNotNull((Object)itemLike, (Object)"itemLike");
        ITypedIngredient<ItemStack> ingredient = TypedItemStack.create(itemLike);
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addFluidStack(Fluid fluid) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), fluidHelper.bucketVolume(), DataComponentPatch.EMPTY);
    }

    @Override
    public DisplayIngredientAcceptor addFluidStack(Fluid fluid, long amount) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), amount, DataComponentPatch.EMPTY);
    }

    @Override
    public DisplayIngredientAcceptor addFluidStack(Fluid fluid, long amount, DataComponentPatch componentPatch) {
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        return this.addFluidInternal(fluidHelper, (Holder<Fluid>)fluid.builtInRegistryHolder(), amount, componentPatch);
    }

    private <T> DisplayIngredientAcceptor addFluidInternal(IPlatformFluidHelperInternal<T> fluidHelper, Holder<Fluid> fluid, long amount, DataComponentPatch tag) {
        Object fluidStack = fluidHelper.create(fluid, amount, tag);
        IIngredientTypeWithSubtypes fluidIngredientType = fluidHelper.getFluidIngredientType();
        this.addIngredientInternal(fluidIngredientType, fluidStack);
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        for (ITypedIngredient<?> typedIngredient : ingredients) {
            this.addTypedIngredient(typedIngredient);
        }
        return this;
    }

    @Override
    public DisplayIngredientAcceptor addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        for (Optional<ITypedIngredient<?>> o : ingredients) {
            if (o.isPresent()) {
                this.addTypedIngredient(o.get());
                continue;
            }
            this.ingredients.add(null);
        }
        return this;
    }

    private <T> void addIngredientInternal(IIngredientType<T> ingredientType, @Nullable T ingredient) {
        @Nullable ITypedIngredient<T> result = TypedIngredient.createAndFilterInvalid(this.ingredientManager, ingredientType, ingredient, false);
        this.ingredients.add(result);
    }

    public @UnmodifiableView List<@Nullable ITypedIngredient<?>> getAllIngredients() {
        return Collections.unmodifiableList(this.ingredients);
    }

    public IntSet getMatches(IFocusGroup focusGroup, RecipeIngredientRole role) {
        List<IFocus<?>> focuses = focusGroup.getFocuses(role).toList();
        IntOpenHashSet results = new IntOpenHashSet();
        for (IFocus<?> focus : focuses) {
            this.getMatches(focus, (IntSet)results);
        }
        return results;
    }

    private <T> void getMatches(IFocus<T> focus, IntSet results) {
        List<@Nullable ITypedIngredient<?>> ingredients = this.getAllIngredients();
        if (ingredients.isEmpty()) {
            return;
        }
        ITypedIngredient<T> focusValue = focus.getTypedValue();
        IIngredientType<T> ingredientType = focusValue.getType();
        IIngredientHelper<T> ingredientHelper = this.ingredientManager.getIngredientHelper(ingredientType);
        Object focusUid = ingredientHelper.getUid(focusValue, UidContext.Ingredient);
        for (int i = 0; i < ingredients.size(); ++i) {
            Object uniqueId;
            ITypedIngredient<T> ingredient;
            @Nullable ITypedIngredient<?> typedIngredient = ingredients.get(i);
            if (typedIngredient == null || (ingredient = typedIngredient.cast(ingredientType)) == null || !focusUid.equals(uniqueId = ingredientHelper.getUid(ingredient, UidContext.Ingredient))) continue;
            results.add(i);
        }
    }
}

