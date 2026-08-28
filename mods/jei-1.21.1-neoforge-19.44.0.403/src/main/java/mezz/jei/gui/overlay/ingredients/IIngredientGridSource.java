/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.List;
import mezz.jei.gui.overlay.elements.IElement;
import org.jetbrains.annotations.Unmodifiable;

public interface IIngredientGridSource {
    public @Unmodifiable List<IElement<?>> getElements();

    public void addSourceListChangedListener(SourceListChangedListener var1);

    public static interface SourceListChangedListener {
        public void onSourceListChanged();
    }
}

