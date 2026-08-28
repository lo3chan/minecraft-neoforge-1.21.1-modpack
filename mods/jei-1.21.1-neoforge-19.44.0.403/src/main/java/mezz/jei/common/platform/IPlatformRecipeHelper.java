/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.inventory.GrindstoneMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package mezz.jei.common.platform;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformRecipeHelper {
    public Ingredient getBase(SmithingRecipe var1);

    public Ingredient getAddition(SmithingRecipe var1);

    public Ingredient getTemplate(SmithingRecipe var1);

    public ItemStack getGrindstoneResult(GrindstoneMenu var1, ItemStack var2, ItemStack var3);

    public boolean isItemEnchantable(ItemStack var1, Holder<Enchantment> var2);
}

