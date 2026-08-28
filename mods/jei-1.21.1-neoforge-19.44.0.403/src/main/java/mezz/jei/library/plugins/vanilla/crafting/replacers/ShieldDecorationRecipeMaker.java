/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.item.BannerItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapelessRecipe
 *  net.minecraft.world.level.ItemLike
 */
package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.StreamSupport;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public final class ShieldDecorationRecipeMaker {
    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        Iterable banners = RegistryUtil.getRegistry(Registries.ITEM).getTagOrEmpty(ItemTags.BANNERS);
        EnumSet<DyeColor> colors = EnumSet.noneOf(DyeColor.class);
        return StreamSupport.stream(banners.spliterator(), false).filter(Holder::isBound).map(Holder::value).filter(BannerItem.class::isInstance).map(BannerItem.class::cast).filter(item -> colors.add(item.getColor())).map(ShieldDecorationRecipeMaker::createRecipe).toList();
    }

    private static RecipeHolder<CraftingRecipe> createRecipe(BannerItem banner) {
        NonNullList inputs = NonNullList.of((Object)Ingredient.EMPTY, (Object[])new Ingredient[]{Ingredient.of((ItemLike[])new ItemLike[]{Items.SHIELD}), Ingredient.of((ItemLike[])new ItemLike[]{banner})});
        ItemStack output = ShieldDecorationRecipeMaker.createOutput(banner);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)("jei.shield.decoration." + banner.getDescriptionId()));
        ShapelessRecipe recipe = new ShapelessRecipe("jei.shield.decoration", CraftingBookCategory.MISC, output, inputs);
        return new RecipeHolder(id, (Recipe)recipe);
    }

    private static ItemStack createOutput(BannerItem banner) {
        DyeColor color = banner.getColor();
        ItemStack output = new ItemStack((ItemLike)Items.SHIELD);
        output.set(DataComponents.BASE_COLOR, (Object)color);
        return output;
    }

    private ShieldDecorationRecipeMaker() {
    }
}

