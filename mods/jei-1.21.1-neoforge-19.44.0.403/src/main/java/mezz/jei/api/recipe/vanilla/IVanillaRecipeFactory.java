/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.recipe.vanilla;

import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.jetbrains.annotations.Nullable;

public interface IVanillaRecipeFactory {
    public IJeiAnvilRecipe createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3, @Nullable ResourceLocation var4);

    public IJeiAnvilRecipe createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3, ResourceLocation var4);

    public IJeiGrindstoneRecipe createGrindstoneRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3, int var4, int var5, ResourceLocation var6);

    public IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3, ResourceLocation var4);

    public IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3, ResourceLocation var4);

    public IJeiShapedRecipeBuilder createShapedRecipeBuilder(CraftingBookCategory var1, List<ItemStack> var2);

    @Deprecated(since="19.1.0")
    public IJeiAnvilRecipe createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);

    @Deprecated(since="19.1.0")
    public IJeiAnvilRecipe createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3);

    @Deprecated(since="19.1.0")
    public IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3);

    @Deprecated(since="19.1.0")
    public IJeiBrewingRecipe createBrewingRecipe(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3);
}

