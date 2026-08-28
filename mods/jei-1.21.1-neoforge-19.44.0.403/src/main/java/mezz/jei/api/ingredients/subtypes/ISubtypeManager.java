/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.ingredients.subtypes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ISubtypeManager {
    @Nullable
    default public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return this.getSubtypeData(VanillaTypes.ITEM_STACK, ingredient, context);
    }

    @Nullable
    public <T> Object getSubtypeData(IIngredientTypeWithSubtypes<?, T> var1, T var2, UidContext var3);

    @Nullable
    public <B, T> Object getSubtypeData(IIngredientTypeWithSubtypes<B, T> var1, ITypedIngredient<T> var2, UidContext var3);

    @Deprecated(since="19.9.0", forRemoval=true)
    default public String getSubtypeInfo(ItemStack ingredient, UidContext context) {
        return this.getSubtypeInfo(VanillaTypes.ITEM_STACK, ingredient, context);
    }

    @Deprecated(since="19.9.0", forRemoval=true)
    public <T> String getSubtypeInfo(IIngredientTypeWithSubtypes<?, T> var1, T var2, UidContext var3);

    default public boolean hasSubtypes(ItemStack ingredient) {
        return this.hasSubtypes(VanillaTypes.ITEM_STACK, ingredient);
    }

    public <T, B> boolean hasSubtypes(IIngredientTypeWithSubtypes<B, T> var1, T var2);
}

