/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.UnmodifiableView
 */
package mezz.jei.library.recipes.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.recipes.collect.IngredientToRecipesMap;
import org.jetbrains.annotations.UnmodifiableView;

public class RecipeIngredientTable {
    private final Map<RecipeType<?>, IngredientToRecipesMap<?>> map = new HashMap();

    public <V> void add(V recipe, RecipeType<V> recipeType, Collection<Object> ingredientUids) {
        IngredientToRecipesMap ingredientToRecipesMap = this.map.computeIfAbsent(recipeType, k -> new IngredientToRecipesMap());
        ingredientToRecipesMap.add(recipe, ingredientUids);
    }

    public <V> @UnmodifiableView List<V> get(RecipeType<V> recipeType, Object ingredientUid) {
        IngredientToRecipesMap<?> ingredientToRecipesMap = this.map.get(recipeType);
        if (ingredientToRecipesMap == null) {
            return List.of();
        }
        return ingredientToRecipesMap.get(ingredientUid);
    }

    public void compact() {
        this.map.values().forEach(IngredientToRecipesMap::compact);
    }
}

