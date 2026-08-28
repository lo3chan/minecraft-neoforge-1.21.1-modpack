/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.overlay.bookmarks;

import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.bookmarks.IBookmark;

public interface IBookmarkDragTarget {
    public ImmutableRect2i getArea();

    public void accept(IBookmark var1);
}

