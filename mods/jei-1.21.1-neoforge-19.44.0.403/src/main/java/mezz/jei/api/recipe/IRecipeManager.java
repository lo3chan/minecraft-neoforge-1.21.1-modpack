/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package mezz.jei.api.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeCatalystLookup;
import mezz.jei.api.recipe.IRecipeCategoriesLookup;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

public interface IRecipeManager {
    public <R> IRecipeLookup<R> createRecipeLookup(RecipeType<R> var1);

    public IRecipeCategoriesLookup createRecipeCategoryLookup();

    public <T> IRecipeCategory<T> getRecipeCategory(RecipeType<T> var1);

    public IRecipeCatalystLookup createRecipeCatalystLookup(RecipeType<?> var1);

    public <T> void hideRecipes(RecipeType<T> var1, Collection<T> var2);

    public <T> void unhideRecipes(RecipeType<T> var1, Collection<T> var2);

    public <T> void addRecipes(RecipeType<T> var1, List<T> var2);

    public void hideRecipeCategory(RecipeType<?> var1);

    public void unhideRecipeCategory(RecipeType<?> var1);

    public <T> IRecipeLayoutDrawable<T> createRecipeLayoutDrawableOrShowError(IRecipeCategory<T> var1, T var2, IFocusGroup var3);

    public <T> Optional<IRecipeLayoutDrawable<T>> createRecipeLayoutDrawable(IRecipeCategory<T> var1, T var2, IFocusGroup var3);

    public <T> Optional<IRecipeLayoutDrawable<T>> createRecipeLayoutDrawable(IRecipeCategory<T> var1, T var2, IFocusGroup var3, IScalableDrawable var4, int var5);

    public IRecipeSlotDrawable createRecipeSlotDrawable(RecipeIngredientRole var1, List<Optional<ITypedIngredient<?>>> var2, Set<Integer> var3, int var4);

    @Deprecated(since="19.19.1")
    default public IRecipeSlotDrawable createRecipeSlotDrawable(RecipeIngredientRole role, List<Optional<ITypedIngredient<?>>> ingredients, Set<Integer> focusedIngredients, int xPos, int yPos, int ingredientCycleOffset) {
        IRecipeSlotDrawable recipeSlotDrawable = this.createRecipeSlotDrawable(role, ingredients, focusedIngredients, ingredientCycleOffset);
        recipeSlotDrawable.setPosition(xPos, yPos);
        return recipeSlotDrawable;
    }

    public <T> IIngredientSupplier getRecipeIngredients(IRecipeCategory<T> var1, T var2);

    public <T> Optional<RecipeType<T>> getRecipeType(ResourceLocation var1, Class<? extends T> var2);

    public Optional<RecipeType<?>> getRecipeType(ResourceLocation var1);

    public List<IRecipeButtonControllerFactory> getRecipeButtonControllerFactories();
}

