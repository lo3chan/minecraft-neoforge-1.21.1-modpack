/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.ItemAttributeModifiers
 *  net.minecraft.world.item.enchantment.Enchantment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.neoforge.platform;

import java.util.Optional;
import mezz.jei.common.platform.IPlatformItemStackHelper;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemStackHelper
implements IPlatformItemStackHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public int getBurnTime(ItemStack itemStack) {
        try {
            return itemStack.getBurnTime(null);
        }
        catch (LinkageError | RuntimeException e) {
            String itemStackInfo = ErrorUtil.getItemStackInfo(itemStack);
            LOGGER.error("Failed to check if item is fuel {}.", (Object)itemStackInfo, (Object)e);
            return 0;
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Item item = stack.getItem();
        return item.isBookEnchantable(stack, book);
    }

    @Override
    public Optional<String> getCreatorModId(ItemStack stack) {
        Item item = stack.getItem();
        String creatorModId = item.getCreatorModId(stack);
        return Optional.ofNullable(creatorModId);
    }

    @Override
    public ItemAttributeModifiers getItemAttributeModifiers(ItemStack stack) {
        return stack.getAttributeModifiers();
    }

    @Override
    public boolean canEnchant(Holder<Enchantment> enchantment, ItemStack ingredient) {
        return ingredient.supportsEnchantment(enchantment);
    }
}

