/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.library.gui.recipes;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.gui.screens.Screen;

public class RecipesGuiDummy
implements IRecipesGui {
    public static final IRecipesGui INSTANCE = new RecipesGuiDummy();

    @Override
    public void show(List<IFocus<?>> focuses) {
    }

    @Override
    public void showTypes(List<RecipeType<?>> recipeTypes) {
    }

    @Override
    public <T> void showRecipes(IRecipeCategory<T> recipeCategory, List<T> recipes, List<IFocus<?>> focuses) {
    }

    @Override
    public <T> Optional<T> getIngredientUnderMouse(IIngredientType<T> ingredientType) {
        return Optional.empty();
    }

    @Override
    public Optional<Screen> getParentScreen() {
        return Optional.empty();
    }
}

