/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.ingredients;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public final class TypedIngredient<T>
implements ITypedIngredient<T> {
    private final IIngredientType<T> ingredientType;
    private final T ingredient;

    private static <T> void checkParameters(IIngredientType<T> ingredientType, T ingredient) {
        Preconditions.checkNotNull(ingredientType, (Object)"ingredientType");
        Preconditions.checkNotNull(ingredient, (Object)"ingredient");
        Class<T> ingredientClass = ingredientType.getIngredientClass();
        if (!ingredientClass.isInstance(ingredient)) {
            throw new IllegalArgumentException("Invalid ingredient found.  Should be an instance of: " + String.valueOf(ingredientClass) + " Instead got: " + String.valueOf(ingredient.getClass()));
        }
    }

    public static <T> ITypedIngredient<T> normalize(ITypedIngredient<T> typedIngredient, IIngredientHelper<T> ingredientHelper) {
        IIngredientType<T> type = typedIngredient.getType();
        if (type == VanillaTypes.ITEM_STACK) {
            ITypedIngredient<ItemStack> normalized;
            ITypedIngredient<ItemStack> cast = typedIngredient;
            ITypedIngredient<ItemStack> castNormalized = normalized = TypedItemStack.normalize(cast);
            return castNormalized;
        }
        T ingredient = typedIngredient.getIngredient();
        T normalized = ingredientHelper.normalizeIngredient(ingredient);
        return TypedIngredient.createUnvalidated(type, normalized);
    }

    public static <T> ITypedIngredient<T> createUnvalidated(IIngredientType<T> ingredientType, T ingredient) {
        if (ingredientType == VanillaTypes.ITEM_STACK) {
            ITypedIngredient<ItemStack> typedIngredient;
            ITypedIngredient<ItemStack> castIngredient = typedIngredient = TypedItemStack.create((ItemStack)ingredient);
            return castIngredient;
        }
        return new TypedIngredient<T>(ingredientType, ingredient);
    }

    @Nullable
    public static <T> ITypedIngredient<?> createAndFilterInvalid(IIngredientManager ingredientManager, @Nullable T ingredient, boolean normalize) {
        if (ingredient == null) {
            return null;
        }
        IIngredientType<T> type = ingredientManager.getIngredientType(ingredient);
        if (type == null) {
            return null;
        }
        return TypedIngredient.createAndFilterInvalid(ingredientManager, type, ingredient, normalize);
    }

    @Nullable
    public static <T> ITypedIngredient<T> createAndFilterInvalid(IIngredientManager ingredientManager, IIngredientType<T> ingredientType, @Nullable T ingredient, boolean normalize) {
        if (ingredient == null) {
            return null;
        }
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        return TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
    }

    public static <T> List<ITypedIngredient<T>> createAndFilterInvalidNonnullList(IIngredientManager ingredientManager, IIngredientType<T> ingredientType, Collection<T> ingredients, boolean normalize) {
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        ArrayList<ITypedIngredient<T>> results = new ArrayList<ITypedIngredient<T>>(ingredients.size());
        for (T ingredient : ingredients) {
            @Nullable ITypedIngredient<T> result = TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
            if (result == null) continue;
            results.add(result);
        }
        return results;
    }

    public static <T> List<@Nullable ITypedIngredient<T>> createAndFilterInvalidList(IIngredientManager ingredientManager, IIngredientType<T> ingredientType, List<@Nullable T> ingredients, boolean normalize) {
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        ArrayList<@Nullable ITypedIngredient<T>> results = new ArrayList<ITypedIngredient<T>>(ingredients.size());
        for (T ingredient : ingredients) {
            @Nullable ITypedIngredient<T> result = TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, normalize);
            results.add(result);
        }
        return results;
    }

    public static List<@Nullable ITypedIngredient<ItemStack>> createAndFilterInvalidList(IIngredientManager ingredientManager, Ingredient ingredient, boolean normalize) {
        ItemStack[] itemStacks = ingredient.getItems();
        IIngredientHelper ingredientHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
        ArrayList<@Nullable ITypedIngredient<ItemStack>> results = new ArrayList<ITypedIngredient<ItemStack>>(itemStacks.length);
        for (ItemStack itemStack : itemStacks) {
            ITypedIngredient<ItemStack> result = TypedIngredient.createAndFilterInvalid(ingredientHelper, VanillaTypes.ITEM_STACK, itemStack, normalize);
            results.add(result);
        }
        return results;
    }

    @Nullable
    public static <T> ITypedIngredient<T> createAndFilterInvalid(IIngredientHelper<T> ingredientHelper, IIngredientType<T> ingredientType, @Nullable T ingredient, boolean normalize) {
        if (ingredient == null) {
            return null;
        }
        try {
            if (normalize) {
                ingredient = ingredientHelper.normalizeIngredient(ingredient);
            }
            if (!ingredientHelper.isValidIngredient(ingredient)) {
                return null;
            }
        }
        catch (RuntimeException e) {
            String ingredientInfo = ingredientHelper.getErrorInfo(ingredient);
            throw new IllegalArgumentException("Crashed when checking if ingredient is valid. Ingredient Info: " + ingredientInfo, e);
        }
        return TypedIngredient.createUnvalidated(ingredientType, ingredient);
    }

    @Nullable
    public static <T> ITypedIngredient<T> defensivelyCopyTypedIngredientFromApi(IIngredientManager ingredientManager, ITypedIngredient<T> value) {
        if (value instanceof TypedItemStack || value instanceof TypedIngredient) {
            return value;
        }
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(value.getType());
        T ingredient = ingredientHelper.copyIngredient(value.getIngredient());
        return TypedIngredient.createAndFilterInvalid(ingredientManager, value.getType(), ingredient, false);
    }

    private TypedIngredient(IIngredientType<T> ingredientType, T ingredient) {
        TypedIngredient.checkParameters(ingredientType, ingredient);
        this.ingredientType = ingredientType;
        this.ingredient = ingredient;
    }

    @Override
    public T getIngredient() {
        return this.ingredient;
    }

    @Override
    public IIngredientType<T> getType() {
        return this.ingredientType;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("type", (Object)this.ingredientType.getUid()).add("ingredient", this.ingredient).toString();
    }
}

