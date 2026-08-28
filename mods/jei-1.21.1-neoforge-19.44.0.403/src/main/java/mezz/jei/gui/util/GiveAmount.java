/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.gui.util;

import net.minecraft.world.item.ItemStack;

public enum GiveAmount {
    ONE,
    MAX;


    public int getAmountForStack(ItemStack itemStack) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> itemStack.getMaxStackSize();
            case 0 -> 1;
        };
    }
}

