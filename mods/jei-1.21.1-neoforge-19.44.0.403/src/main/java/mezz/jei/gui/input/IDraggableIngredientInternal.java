/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.input;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.overlay.elements.IElement;

public interface IDraggableIngredientInternal<T> {
    public ITypedIngredient<T> getTypedIngredient();

    public IElement<T> getElement();

    public ImmutableRect2i getArea();
}

