/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.library.ingredients.itemStacks;

import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class NormalizedTypedItem
extends TypedItemStack {
    private final Holder<Item> itemHolder;

    NormalizedTypedItem(Holder<Item> itemHolder) {
        this.itemHolder = itemHolder;
    }

    @Override
    protected ItemStack createItemStackUncached() {
        return new ItemStack(this.itemHolder);
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
        return "NormalizedTypedItem{itemHolder=" + String.valueOf(this.itemHolder) + "}";
    }
}

