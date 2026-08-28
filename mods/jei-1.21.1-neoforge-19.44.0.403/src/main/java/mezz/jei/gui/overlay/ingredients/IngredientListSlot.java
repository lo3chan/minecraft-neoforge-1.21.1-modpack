/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.Optional;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.ClickableIngredientInternal;
import mezz.jei.gui.input.DraggableIngredientInternal;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Nullable;

public class IngredientListSlot {
    private final ImmutableRect2i area;
    private final int padding;
    private boolean blocked = false;
    @Nullable
    private IElement<?> element;

    public IngredientListSlot(int xPosition, int yPosition, int width, int height, int padding) {
        this.area = new ImmutableRect2i(xPosition, yPosition, width, height);
        this.padding = padding;
    }

    public Optional<IElement<?>> getOptionalElement() {
        return Optional.ofNullable(this.element);
    }

    @Nullable
    public IElement<?> getElement() {
        return this.element;
    }

    public Optional<IClickableIngredientInternal<?>> getClickableIngredient() {
        return Optional.ofNullable(this.element).map(element -> new ClickableIngredientInternal(element, this::isMouseOver, true, true));
    }

    public Optional<IDraggableIngredientInternal<?>> getDraggableIngredient() {
        return Optional.ofNullable(this.element).map(element -> new DraggableIngredientInternal(element, this.area));
    }

    public void clear() {
        this.element = null;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.element != null && this.area.contains(mouseX, mouseY);
    }

    public void setElement(IElement<?> element) {
        this.element = element;
    }

    public ImmutableRect2i getArea() {
        return this.area;
    }

    public ImmutableRect2i getRenderArea() {
        return this.area.insetBy(this.padding);
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public int getPadding() {
        return this.padding;
    }
}

