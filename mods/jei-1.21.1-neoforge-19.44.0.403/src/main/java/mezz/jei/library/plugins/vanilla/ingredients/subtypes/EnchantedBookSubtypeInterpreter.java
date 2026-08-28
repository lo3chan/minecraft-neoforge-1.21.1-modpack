/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Optional;
import java.util.StringJoiner;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;

public class EnchantedBookSubtypeInterpreter
implements ISubtypeInterpreter<ItemStack> {
    public static final EnchantedBookSubtypeInterpreter INSTANCE = new EnchantedBookSubtypeInterpreter();

    private EnchantedBookSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return EnchantmentHelper.getEnchantmentsForCrafting((ItemStack)ingredient);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return this.getStringName(ingredient);
    }

    public String getStringName(ItemStack itemStack) {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting((ItemStack)itemStack);
        if (enchantments.isEmpty()) {
            return "";
        }
        ArrayList<CallSite> strings = new ArrayList<CallSite>();
        for (Holder e : enchantments.keySet()) {
            Optional optional = e.unwrapKey();
            if (!optional.isPresent()) continue;
            String s = String.valueOf(((ResourceKey)optional.orElseThrow()).location()) + ".lvl" + enchantments.getLevel(e);
            strings.add((CallSite)((Object)s));
        }
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        strings.sort(null);
        for (String string : strings) {
            joiner.add(string);
        }
        return joiner.toString();
    }
}

