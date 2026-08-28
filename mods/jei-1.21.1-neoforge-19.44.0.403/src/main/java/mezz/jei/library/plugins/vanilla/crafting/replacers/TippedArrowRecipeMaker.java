/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.List;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

public final class TippedArrowRecipeMaker {
    public static List<RecipeHolder<CraftingRecipe>> createRecipes(IJeiHelpers jeiHelpers) {
        IVanillaRecipeFactory vanillaRecipeFactory = jeiHelpers.getVanillaRecipeFactory();
        String group = "jei.tipped.arrow";
        ItemStack arrowStack = new ItemStack((ItemLike)Items.ARROW);
        Ingredient arrowIngredient = Ingredient.of((ItemStack[])new ItemStack[]{arrowStack});
        Registry potionRegistry = RegistryUtil.getRegistry(Registries.POTION);
        return potionRegistry.holders().map(potion -> {
            ItemStack input = PotionContents.createItemStack((Item)Items.LINGERING_POTION, (Holder)potion);
            ItemStack output = PotionContents.createItemStack((Item)Items.TIPPED_ARROW, (Holder)potion);
            output.setCount(8);
            Ingredient potionIngredient = Ingredient.of((ItemStack[])new ItemStack[]{input});
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)("jei.tipped.arrow." + output.getDescriptionId()));
            CraftingRecipe recipe = vanillaRecipeFactory.createShapedRecipeBuilder(CraftingBookCategory.MISC, List.of(output)).group(group).define(Character.valueOf('a'), arrowIngredient).define(Character.valueOf('p'), potionIngredient).pattern("aaa").pattern("apa").pattern("aaa").build();
            return new RecipeHolder(id, (Recipe)recipe);
        }).toList();
    }

    private TippedArrowRecipeMaker() {
    }
}

