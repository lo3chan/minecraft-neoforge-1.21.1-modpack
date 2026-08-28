/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapelessRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ShulkerBoxBlock
 */
package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.Arrays;
import java.util.List;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class ShulkerBoxColoringRecipeMaker {
    private static final String group = "jei.shulker.color";

    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        Ingredient baseShulkerIngredient = Ingredient.of((ItemLike[])new ItemLike[]{Blocks.SHULKER_BOX});
        return Arrays.stream(DyeColor.values()).map(color -> ShulkerBoxColoringRecipeMaker.createRecipe(color, baseShulkerIngredient)).toList();
    }

    private static RecipeHolder<CraftingRecipe> createRecipe(DyeColor color, Ingredient baseShulkerIngredient) {
        IPlatformIngredientHelper ingredientHelper = Services.PLATFORM.getIngredientHelper();
        Ingredient colorIngredient = ingredientHelper.createShulkerDyeIngredient(color);
        NonNullList inputs = NonNullList.of((Object)Ingredient.EMPTY, (Object[])new Ingredient[]{baseShulkerIngredient, colorIngredient});
        Block coloredShulkerBox = ShulkerBoxBlock.getBlockByColor((DyeColor)color);
        ItemStack output = new ItemStack((ItemLike)coloredShulkerBox);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)("jei.shulker.color." + output.getDescriptionId()));
        ShapelessRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
        return new RecipeHolder(id, (Recipe)recipe);
    }

    private ShulkerBoxColoringRecipeMaker() {
    }
}

