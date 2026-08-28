/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.input;

public interface IPaged {
    public boolean nextPage();

    public boolean previousPage();

    public boolean hasNext();

    public boolean hasPrevious();

    public int getPageCount();

    public int getPageNumber();
}

