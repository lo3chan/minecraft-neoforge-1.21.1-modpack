/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.inventory.GrindstoneMenu
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.crafting.SmithingTransformRecipe
 *  net.minecraft.world.item.crafting.SmithingTrimRecipe
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package mezz.jei.neoforge.platform;

import mezz.jei.common.platform.IPlatformRecipeHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.enchantment.Enchantment;

public class RecipeHelper
implements IPlatformRecipeHelper {
    @Override
    public Ingredient getBase(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTransformRecipe) {
            SmithingTransformRecipe transformRecipe = (SmithingTransformRecipe)recipe;
            return transformRecipe.base;
        }
        if (recipe instanceof SmithingTrimRecipe) {
            SmithingTrimRecipe trimRecipe = (SmithingTrimRecipe)recipe;
            return trimRecipe.base;
        }
        return Ingredient.EMPTY;
    }

    @Override
    public Ingredient getAddition(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTransformRecipe) {
            SmithingTransformRecipe transformRecipe = (SmithingTransformRecipe)recipe;
            return transformRecipe.addition;
        }
        if (recipe instanceof SmithingTrimRecipe) {
            SmithingTrimRecipe trimRecipe = (SmithingTrimRecipe)recipe;
            return trimRecipe.addition;
        }
        return Ingredient.EMPTY;
    }

    @Override
    public Ingredient getTemplate(SmithingRecipe recipe) {
        if (recipe instanceof SmithingTransformRecipe) {
            SmithingTransformRecipe transformRecipe = (SmithingTransformRecipe)recipe;
            return transformRecipe.template;
        }
        if (recipe instanceof SmithingTrimRecipe) {
            SmithingTrimRecipe trimRecipe = (SmithingTrimRecipe)recipe;
            return trimRecipe.template;
        }
        return Ingredient.EMPTY;
    }

    @Override
    public ItemStack getGrindstoneResult(GrindstoneMenu grindstoneMenu, ItemStack input1, ItemStack input2) {
        return grindstoneMenu.computeResult(input1, input2);
    }

    @Override
    public boolean isItemEnchantable(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.getItem().isEnchantable(stack) && stack.supportsEnchantment(enchantment);
    }
}

