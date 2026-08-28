/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package mezz.jei.common.platform;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformIngredientHelper {
    public Ingredient createShulkerDyeIngredient(DyeColor var1);

    public List<Ingredient> getPotionContainers(PotionBrewing var1);

    public Stream<Ingredient> getPotionIngredients(PotionBrewing var1);

    public float getCompostValue(ItemStack var1);

    public HolderSet<Item> getSupportedItems(Holder<Enchantment> var1);
}

