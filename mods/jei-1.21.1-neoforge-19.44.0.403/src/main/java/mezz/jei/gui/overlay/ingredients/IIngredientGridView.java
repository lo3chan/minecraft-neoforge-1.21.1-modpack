/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.ingredients;

import java.util.Set;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import org.jetbrains.annotations.Nullable;

public interface IIngredientGridView {
    public boolean hasRoom();

    public void close();

    public void updateBounds(ImmutableRect2i var1, Set<ImmutableRect2i> var2, @Nullable ImmutablePoint2i var3);

    public ImmutableRect2i getBackgroundArea();
}

