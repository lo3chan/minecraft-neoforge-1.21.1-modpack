/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import java.util.stream.Stream;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.overlay.elements.IElement;

public interface IIngredientGrid
extends IRecipeFocusSource {
    public boolean isMouseOver(double var1, double var3);

    public int size();

    public int getColumnCount();

    public int getRowCount();

    public void set(int var1, List<IElement<?>> var2);

    default public void set(int firstItemIndex, int smoothScrollRowPixelOffset, List<IElement<?>> ingredientList) {
        this.set(firstItemIndex, ingredientList);
    }

    public Stream<IElement<?>> getVisibleElements();

    public void tick();
}

