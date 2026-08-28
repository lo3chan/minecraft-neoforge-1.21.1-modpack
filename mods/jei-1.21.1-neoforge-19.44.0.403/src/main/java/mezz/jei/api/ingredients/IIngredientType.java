/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.ingredients;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IIngredientType<T> {
    public Class<? extends T> getIngredientClass();

    default public String getUid() {
        Class<T> ingredientClass = this.getIngredientClass();
        return ingredientClass.getName();
    }

    default public Optional<T> castIngredient(@Nullable Object ingredient) {
        Class<T> ingredientClass = this.getIngredientClass();
        if (ingredientClass.isInstance(ingredient)) {
            return Optional.of(ingredientClass.cast(ingredient));
        }
        return Optional.empty();
    }

    @Nullable
    default public T getCastIngredient(@Nullable Object ingredient) {
        Class<T> ingredientClass = this.getIngredientClass();
        if (ingredientClass.isInstance(ingredient)) {
            return ingredientClass.cast(ingredient);
        }
        return null;
    }
}

