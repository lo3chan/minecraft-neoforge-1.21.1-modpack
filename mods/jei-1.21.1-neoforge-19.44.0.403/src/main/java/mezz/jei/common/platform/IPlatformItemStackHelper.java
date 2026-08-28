/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemAttributeModifiers
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package mezz.jei.common.platform;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformItemStackHelper {
    public int getBurnTime(ItemStack var1);

    public boolean isBookEnchantable(ItemStack var1, ItemStack var2);

    public Optional<String> getCreatorModId(ItemStack var1);

    default public ItemAttributeModifiers getItemAttributeModifiers(ItemStack stack) {
        return (ItemAttributeModifiers)stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, (Object)ItemAttributeModifiers.EMPTY);
    }

    public boolean canEnchant(Holder<Enchantment> var1, ItemStack var2);
}

