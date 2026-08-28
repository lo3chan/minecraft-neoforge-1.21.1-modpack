/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.search;

import java.util.Collection;
import java.util.function.Consumer;
import mezz.jei.common.search.SearchMode;

public interface ISearchable<T> {
    public void getSearchResults(String var1, Consumer<Collection<T>> var2);

    public void getAllElements(Consumer<Collection<T>> var1);

    default public SearchMode getMode() {
        return SearchMode.ENABLED;
    }
}

