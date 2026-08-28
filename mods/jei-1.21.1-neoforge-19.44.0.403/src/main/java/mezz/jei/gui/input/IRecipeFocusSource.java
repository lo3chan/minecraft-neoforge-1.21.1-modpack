/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.input;

import java.util.stream.Stream;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IDraggableIngredientInternal;

public interface IRecipeFocusSource {
    public Stream<IClickableIngredientInternal<?>> getIngredientUnderMouse(double var1, double var3);

    public Stream<IDraggableIngredientInternal<?>> getDraggableIngredientUnderMouse(double var1, double var3);
}

