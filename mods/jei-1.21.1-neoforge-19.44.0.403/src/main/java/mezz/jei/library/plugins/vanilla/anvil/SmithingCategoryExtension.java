/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.crafting.SmithingRecipeInput
 */
package mezz.jei.library.plugins.vanilla.anvil;

import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

public abstract class SmithingCategoryExtension<R extends SmithingRecipe>
implements ISmithingCategoryExtension<R> {
    private final IPlatformRecipeHelper recipeHelper;

    public SmithingCategoryExtension(IPlatformRecipeHelper recipeHelper) {
        this.recipeHelper = recipeHelper;
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(R recipe, T ingredientAcceptor) {
        Ingredient ingredient = this.recipeHelper.getTemplate((SmithingRecipe)recipe);
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(R recipe, T ingredientAcceptor) {
        Ingredient ingredient = this.recipeHelper.getBase((SmithingRecipe)recipe);
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(R recipe, T ingredientAcceptor) {
        Ingredient ingredient = this.recipeHelper.getAddition((SmithingRecipe)recipe);
        ingredientAcceptor.addIngredients(ingredient);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(R recipe, T ingredientAcceptor) {
        List<ItemStack> baseStacks;
        Ingredient templateIngredient = this.recipeHelper.getTemplate((SmithingRecipe)recipe);
        Ingredient baseIngredient = this.recipeHelper.getBase((SmithingRecipe)recipe);
        Ingredient additionIngredient = this.recipeHelper.getAddition((SmithingRecipe)recipe);
        List<ItemStack> templateStacks = Arrays.asList(templateIngredient.getItems());
        if (templateStacks.isEmpty()) {
            templateStacks = List.of(ItemStack.EMPTY);
        }
        if ((baseStacks = Arrays.asList(baseIngredient.getItems())).isEmpty()) {
            baseStacks = List.of(ItemStack.EMPTY);
        }
        ItemStack addition = ItemStack.EMPTY;
        ItemStack[] additions = additionIngredient.getItems();
        if (additions.length > 0) {
            addition = additions[0];
        }
        for (ItemStack template : templateStacks) {
            for (ItemStack base : baseStacks) {
                SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
                ItemStack output = RecipeUtil.assembleResultItem(recipeInput, recipe);
                ingredientAcceptor.addItemStack(output);
            }
        }
    }
}

