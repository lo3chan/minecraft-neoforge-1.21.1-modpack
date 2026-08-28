/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.bookmarks;

import mezz.jei.gui.bookmarks.BookmarkType;
import mezz.jei.gui.overlay.elements.IElement;

public interface IBookmark {
    public BookmarkType getType();

    public IElement<?> getElement();

    public boolean isVisible();

    public void setVisible(boolean var1);
}

