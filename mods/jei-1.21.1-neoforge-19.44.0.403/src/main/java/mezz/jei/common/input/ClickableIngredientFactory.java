/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.Rect2i
 */
package mezz.jei.common.input;

import java.util.Optional;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.common.ingredients.ITypedIngredientFactory;
import mezz.jei.common.input.ClickableIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.renderer.Rect2i;

public class ClickableIngredientFactory
implements IClickableIngredientFactory {
    private final ITypedIngredientFactory typedIngredientFactory;

    public ClickableIngredientFactory(ITypedIngredientFactory typedIngredientFactory) {
        this.typedIngredientFactory = typedIngredientFactory;
    }

    @Override
    public <T> IClickableIngredientFactory.IBuilder<T> createBuilder(ITypedIngredient<T> value) {
        return new WithIngredient<T>(value);
    }

    @Override
    public <T> IClickableIngredientFactory.IBuilder<T> createBuilder(IIngredientType<T> ingredientType, T ingredient) {
        return this.typedIngredientFactory.createTypedIngredient(ingredientType, ingredient, false).map(WithIngredient::new).orElse(WithoutIngredient.getInstance());
    }

    private static class WithIngredient<T>
    implements IClickableIngredientFactory.IBuilder<T> {
        private final ITypedIngredient<T> ingredient;

        private WithIngredient(ITypedIngredient<T> ingredient) {
            this.ingredient = ingredient;
        }

        @Override
        public Optional<IClickableIngredient<T>> buildWithArea(int x, int y, int width, int height) {
            ImmutableRect2i area = new ImmutableRect2i(x, y, width, height);
            ClickableIngredient<T> result = new ClickableIngredient<T>(this.ingredient, area);
            return Optional.of(result);
        }

        @Override
        public Optional<IClickableIngredient<T>> buildWithArea(Rect2i area) {
            ImmutableRect2i immutableArea = new ImmutableRect2i(area);
            ClickableIngredient<T> result = new ClickableIngredient<T>(this.ingredient, immutableArea);
            return Optional.of(result);
        }
    }

    private static class WithoutIngredient<T>
    implements IClickableIngredientFactory.IBuilder<T> {
        public static final WithoutIngredient<?> INSTANCE = new WithoutIngredient();

        public static <T> IClickableIngredientFactory.IBuilder<T> getInstance() {
            WithoutIngredient<?> cast = INSTANCE;
            return cast;
        }

        private WithoutIngredient() {
        }

        @Override
        public Optional<IClickableIngredient<T>> buildWithArea(int x, int y, int width, int height) {
            return Optional.empty();
        }

        @Override
        public Optional<IClickableIngredient<T>> buildWithArea(Rect2i area) {
            return Optional.empty();
        }
    }
}

