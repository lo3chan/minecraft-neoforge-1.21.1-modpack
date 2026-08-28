/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.ShapedRecipe
 */
package mezz.jei.library.plugins.vanilla.crafting;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.library.plugins.vanilla.crafting.JeiShapedRecipe;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CraftingCategoryExtension
implements ICraftingCategoryExtension<CraftingRecipe> {
    @Override
    public void setRecipe(RecipeHolder<CraftingRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
        ItemStack resultItem = RecipeUtil.getResultItem(recipe);
        int width = this.getWidth(recipeHolder);
        int height = this.getHeight(recipeHolder);
        craftingGridHelper.createAndSetOutputs(builder, List.of(resultItem));
        craftingGridHelper.createAndSetIngredients(builder, (List<Ingredient>)recipe.getIngredients(), width, height);
    }

    @Override
    public Optional<ResourceLocation> getRegistryName(RecipeHolder<CraftingRecipe> recipeHolder) {
        return Optional.of(recipeHolder.id());
    }

    @Override
    public int getWidth(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            return shapedRecipe.getWidth();
        }
        if (recipe instanceof JeiShapedRecipe) {
            JeiShapedRecipe shapedRecipe = (JeiShapedRecipe)recipe;
            return shapedRecipe.getWidth();
        }
        return 0;
    }

    @Override
    public int getHeight(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            return shapedRecipe.getHeight();
        }
        if (recipe instanceof JeiShapedRecipe) {
            JeiShapedRecipe shapedRecipe = (JeiShapedRecipe)recipe;
            return shapedRecipe.getHeight();
        }
        return 0;
    }

    @Override
    public boolean isHandled(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = (CraftingRecipe)recipeHolder.value();
        return !recipe.isSpecial();
    }
}

