/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.plugins.vanilla.crafting;

import java.util.List;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.util.RecipeDebugUtil;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CategoryRecipeValidator<T extends Recipe<?>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int INVALID_COUNT = -1;
    private final IRecipeCategory<RecipeHolder<T>> recipeCategory;
    private final IIngredientManager ingredientManager;
    private final int maxInputs;

    public CategoryRecipeValidator(IRecipeCategory<RecipeHolder<T>> recipeCategory, IIngredientManager ingredientManager, int maxInputs) {
        this.recipeCategory = recipeCategory;
        this.ingredientManager = ingredientManager;
        this.maxInputs = maxInputs;
    }

    public boolean isRecipeValid(RecipeHolder<T> recipeHolder) {
        return this.hasValidInputsAndOutputs(recipeHolder);
    }

    public boolean isRecipeHandled(RecipeHolder<T> recipeHolder) {
        return this.recipeCategory.isHandled(recipeHolder);
    }

    private boolean hasValidInputsAndOutputs(RecipeHolder<T> recipeHolder) {
        Recipe recipe = recipeHolder.value();
        if (recipe.isSpecial()) {
            return true;
        }
        ItemStack recipeOutput = RecipeUtil.getResultItem(recipe);
        if (recipeOutput == null || recipeOutput.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipeHolder, this.recipeCategory, this.ingredientManager);
                LOGGER.debug("Skipping Recipe because it has no output. {}", (Object)recipeInfo);
            }
            return false;
        }
        NonNullList ingredients = recipe.getIngredients();
        if (ingredients == null) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipeHolder, this.recipeCategory, this.ingredientManager);
                LOGGER.debug("Skipping Recipe because it has no input Ingredients. {}", (Object)recipeInfo);
            }
            return false;
        }
        int inputCount = CategoryRecipeValidator.getInputCount((List<Ingredient>)ingredients);
        if (inputCount == -1) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipeHolder, this.recipeCategory, this.ingredientManager);
                LOGGER.debug("Skipping Recipe because it contains invalid inputs. {}", (Object)recipeInfo);
            }
            return false;
        }
        if (inputCount > this.maxInputs) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipeHolder, this.recipeCategory, this.ingredientManager);
                LOGGER.debug("Skipping Recipe because it has too many inputs. {}", (Object)recipeInfo);
            }
            return false;
        }
        if (inputCount == 0 && this.maxInputs > 0) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipeHolder, this.recipeCategory, this.ingredientManager);
                LOGGER.debug("Skipping Recipe because it has no inputs. {}", (Object)recipeInfo);
            }
            return false;
        }
        return true;
    }

    private static int getInputCount(List<Ingredient> ingredientList) {
        int inputCount = 0;
        for (Ingredient ingredient : ingredientList) {
            ItemStack[] input = ingredient.getItems();
            if (input == null) {
                return -1;
            }
            ++inputCount;
        }
        return inputCount;
    }
}

