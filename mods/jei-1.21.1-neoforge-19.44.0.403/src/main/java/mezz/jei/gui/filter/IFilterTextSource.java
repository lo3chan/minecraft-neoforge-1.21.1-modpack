/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.filter;

public interface IFilterTextSource {
    public String getFilterText();

    public boolean setFilterText(String var1);

    public void addListener(Listener var1);

    @FunctionalInterface
    public static interface Listener {
        public void onChange(String var1, String var2);
    }
}

