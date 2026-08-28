/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientListOverlay;
import org.jetbrains.annotations.Nullable;

public class IngredientListOverlayDummy
implements IIngredientListOverlay {
    public static final IIngredientListOverlay INSTANCE = new IngredientListOverlayDummy();

    private IngredientListOverlayDummy() {
    }

    @Override
    public boolean isListDisplayed() {
        return false;
    }

    @Override
    public boolean hasKeyboardFocus() {
        return false;
    }

    @Override
    public Optional<ITypedIngredient<?>> getIngredientUnderMouse() {
        return Optional.empty();
    }

    @Override
    @Nullable
    public <T> T getIngredientUnderMouse(IIngredientType<T> ingredientType) {
        return null;
    }

    @Override
    public <T> List<T> getVisibleIngredients(IIngredientType<T> ingredientType) {
        return Collections.emptyList();
    }
}

