/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.library.gui.recipes;

import java.util.List;
import java.util.Map;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.library.gui.recipes.supplier.builder.IngredientSlotBuilder;

public class RecipeLayoutIngredientSupplier
implements IIngredientSupplier {
    private final Map<RecipeIngredientRole, IngredientSlotBuilder> ingredientSlotBuilders;

    public RecipeLayoutIngredientSupplier(Map<RecipeIngredientRole, IngredientSlotBuilder> ingredientSlotBuilders) {
        this.ingredientSlotBuilders = ingredientSlotBuilders;
    }

    @Override
    public List<ITypedIngredient<?>> getIngredients(RecipeIngredientRole role) {
        IngredientSlotBuilder ingredientSlotBuilder = this.ingredientSlotBuilders.get((Object)role);
        if (ingredientSlotBuilder == null) {
            return List.of();
        }
        return ingredientSlotBuilder.getAllIngredients();
    }
}

