/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.item.crafting.BlastingRecipe
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SmeltingRecipe
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.item.crafting.StonecutterRecipe
 */
package mezz.jei.library.plugins.vanilla.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.plugins.vanilla.crafting.CategoryRecipeValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public final class VanillaRecipes {
    private final RecipeManager recipeManager;
    private final IIngredientManager ingredientManager;

    public VanillaRecipes(IIngredientManager ingredientManager) {
        Minecraft minecraft = Minecraft.getInstance();
        ErrorUtil.checkNotNull(minecraft, "minecraft");
        ClientLevel world = minecraft.level;
        ErrorUtil.checkNotNull(world, "minecraft world");
        this.recipeManager = world.getRecipeManager();
        this.ingredientManager = ingredientManager;
    }

    public Map<Boolean, List<RecipeHolder<CraftingRecipe>>> getCraftingRecipes(IRecipeCategory<RecipeHolder<CraftingRecipe>> craftingCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(craftingCategory, this.ingredientManager, 9);
        ArrayList<RecipeHolder> handled = new ArrayList<RecipeHolder>();
        ArrayList<RecipeHolder> unhandled = new ArrayList<RecipeHolder>();
        List allRecipes = this.recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
        for (RecipeHolder recipe : allRecipes) {
            if (!validator.isRecipeValid(recipe)) continue;
            if (validator.isRecipeHandled(recipe)) {
                handled.add(recipe);
                continue;
            }
            unhandled.add(recipe);
        }
        return Map.of(true, handled, false, unhandled);
    }

    public List<RecipeHolder<StonecutterRecipe>> getStonecuttingRecipes(IRecipeCategory<RecipeHolder<StonecutterRecipe>> stonecuttingCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(stonecuttingCategory, this.ingredientManager, 1);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.STONECUTTING, validator);
    }

    public List<RecipeHolder<SmeltingRecipe>> getFurnaceRecipes(IRecipeCategory<RecipeHolder<SmeltingRecipe>> furnaceCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(furnaceCategory, this.ingredientManager, 1);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.SMELTING, validator);
    }

    public List<RecipeHolder<SmokingRecipe>> getSmokingRecipes(IRecipeCategory<RecipeHolder<SmokingRecipe>> smokingCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(smokingCategory, this.ingredientManager, 1);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.SMOKING, validator);
    }

    public List<RecipeHolder<BlastingRecipe>> getBlastingRecipes(IRecipeCategory<RecipeHolder<BlastingRecipe>> blastingCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(blastingCategory, this.ingredientManager, 1);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.BLASTING, validator);
    }

    public List<RecipeHolder<CampfireCookingRecipe>> getCampfireCookingRecipes(IRecipeCategory<RecipeHolder<CampfireCookingRecipe>> campfireCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(campfireCategory, this.ingredientManager, 1);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.CAMPFIRE_COOKING, validator);
    }

    public List<RecipeHolder<SmithingRecipe>> getSmithingRecipes(IRecipeCategory<RecipeHolder<SmithingRecipe>> smithingCategory) {
        CategoryRecipeValidator validator = new CategoryRecipeValidator(smithingCategory, this.ingredientManager, 0);
        return VanillaRecipes.getValidHandledRecipes(this.recipeManager, RecipeType.SMITHING, validator);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getValidHandledRecipes(RecipeManager recipeManager, RecipeType<T> recipeType, CategoryRecipeValidator<T> validator) {
        return recipeManager.getAllRecipesFor(recipeType).stream().filter(r -> validator.isRecipeValid((RecipeHolder)r) && validator.isRecipeHandled((RecipeHolder)r)).toList();
    }
}

