/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.filter;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.gui.filter.IFilterTextSource;

public class FilterTextSource
implements IFilterTextSource {
    private final List<IFilterTextSource.Listener> listeners = new ArrayList<IFilterTextSource.Listener>();
    private String filterText = "";

    @Override
    public String getFilterText() {
        return this.filterText;
    }

    @Override
    public boolean setFilterText(String filterText) {
        if (this.filterText.equals(filterText)) {
            return false;
        }
        String oldFilterText = this.filterText;
        this.filterText = filterText;
        for (IFilterTextSource.Listener listener : this.listeners) {
            listener.onChange(oldFilterText, filterText);
        }
        return true;
    }

    @Override
    public void addListener(IFilterTextSource.Listener listener) {
        this.listeners.add(listener);
    }
}

