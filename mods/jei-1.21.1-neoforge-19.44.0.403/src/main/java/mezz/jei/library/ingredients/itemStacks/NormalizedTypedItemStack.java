/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.ingredients.itemStacks;

import mezz.jei.library.ingredients.itemStacks.NormalizedTypedItem;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class NormalizedTypedItemStack
extends TypedItemStack {
    private final Holder<Item> itemHolder;
    private final DataComponentPatch dataComponentPatch;

    public NormalizedTypedItemStack(Holder<Item> itemHolder, DataComponentPatch dataComponentPatch) {
        this.itemHolder = itemHolder;
        this.dataComponentPatch = dataComponentPatch;
    }

    static TypedItemStack create(Holder<Item> itemHolder, DataComponentPatch dataComponentPatch) {
        if (dataComponentPatch.isEmpty()) {
            return new NormalizedTypedItem(itemHolder);
        }
        return new NormalizedTypedItemStack(itemHolder, dataComponentPatch);
    }

    @Override
    protected ItemStack createItemStackUncached() {
        return new ItemStack(this.itemHolder, 1, this.dataComponentPatch);
    }

    @Override
    public TypedItemStack getNormalized() {
        return this;
    }

    @Override
    protected Item getItem() {
        return (Item)this.itemHolder.value();
    }

    public String toString() {
        return "NormalizedTypedItemStack{itemHolder=" + String.valueOf(this.itemHolder) + ", dataComponentPatch=" + String.valueOf(this.dataComponentPatch) + "}";
    }
}

