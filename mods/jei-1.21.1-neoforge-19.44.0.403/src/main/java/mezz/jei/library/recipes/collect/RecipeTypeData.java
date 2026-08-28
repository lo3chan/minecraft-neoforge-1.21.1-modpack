/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 *  org.jetbrains.annotations.UnmodifiableView
 */
package mezz.jei.library.recipes.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.category.IRecipeCategory;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

public class RecipeTypeData<T> {
    private final IRecipeCategory<T> recipeCategory;
    private final List<ITypedIngredient<?>> recipeCategoryCatalysts;
    private final List<T> recipes = new ArrayList<T>();
    private final Set<T> hiddenRecipes = Collections.newSetFromMap(new IdentityHashMap());

    public RecipeTypeData(IRecipeCategory<T> recipeCategory, List<ITypedIngredient<?>> recipeCategoryCatalysts) {
        this.recipeCategory = recipeCategory;
        this.recipeCategoryCatalysts = List.copyOf(recipeCategoryCatalysts);
    }

    public IRecipeCategory<T> getRecipeCategory() {
        return this.recipeCategory;
    }

    public @Unmodifiable List<ITypedIngredient<?>> getRecipeCategoryCatalysts() {
        return this.recipeCategoryCatalysts;
    }

    public @UnmodifiableView List<T> getRecipes() {
        return Collections.unmodifiableList(this.recipes);
    }

    public void addRecipes(Collection<T> recipes) {
        this.recipes.addAll(recipes);
    }

    public Set<T> getHiddenRecipes() {
        return this.hiddenRecipes;
    }
}

