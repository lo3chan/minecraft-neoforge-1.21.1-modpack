/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.helpers;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IStackHelper {
    public Object getUidForStack(ItemStack var1, UidContext var2);

    public Object getUidForStack(ITypedIngredient<ItemStack> var1, UidContext var2);

    public boolean isEquivalent(@Nullable ItemStack var1, @Nullable ItemStack var2, UidContext var3);

    @Deprecated(since="19.9.0", forRemoval=true)
    public String getUniqueIdentifierForStack(ItemStack var1, UidContext var2);
}

