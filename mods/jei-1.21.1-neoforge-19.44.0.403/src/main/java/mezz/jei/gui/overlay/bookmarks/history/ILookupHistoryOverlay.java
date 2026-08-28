/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.bookmarks.history;

import java.util.Set;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import org.jetbrains.annotations.Nullable;

public interface ILookupHistoryOverlay {
    public boolean isDisplayedOnThisSide();

    public int getDisplayHeight();

    public void close();

    public void updateBounds(ImmutableRect2i var1, Set<ImmutableRect2i> var2, @Nullable ImmutablePoint2i var3);

    public void updateLayout();
}

