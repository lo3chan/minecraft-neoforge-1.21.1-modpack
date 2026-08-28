/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$ListBacked
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.SuspiciousStewEffects
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapelessRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FlowerBlock
 */
package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.List;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public final class SuspiciousStewRecipeMaker {
    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        String group = "jei.suspicious.stew";
        Ingredient brownMushroom = Ingredient.of((ItemLike[])new ItemLike[]{Blocks.BROWN_MUSHROOM.asItem()});
        Ingredient redMushroom = Ingredient.of((ItemLike[])new ItemLike[]{Blocks.RED_MUSHROOM.asItem()});
        Ingredient bowl = Ingredient.of((ItemLike[])new ItemLike[]{Items.BOWL});
        return RegistryUtil.getRegistry(Registries.ITEM).getTag(ItemTags.SMALL_FLOWERS).stream().flatMap(HolderSet.ListBacked::stream).map(Holder::value).filter(BlockItem.class::isInstance).map(item -> ((BlockItem)item).getBlock()).filter(FlowerBlock.class::isInstance).map(FlowerBlock.class::cast).map(flowerBlock -> {
            Ingredient flower = Ingredient.of((ItemLike[])new ItemLike[]{flowerBlock.asItem()});
            NonNullList inputs = NonNullList.of((Object)Ingredient.EMPTY, (Object[])new Ingredient[]{brownMushroom, redMushroom, bowl, flower});
            ItemStack output = new ItemStack((ItemLike)Items.SUSPICIOUS_STEW, 1);
            SuspiciousStewEffects effects = flowerBlock.getSuspiciousEffects();
            output.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, (Object)effects);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)("jei.suspicious.stew." + flowerBlock.getDescriptionId()));
            ShapelessRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
            return new RecipeHolder(id, (Recipe)recipe);
        }).toList();
    }

    private SuspiciousStewRecipeMaker() {
    }
}

