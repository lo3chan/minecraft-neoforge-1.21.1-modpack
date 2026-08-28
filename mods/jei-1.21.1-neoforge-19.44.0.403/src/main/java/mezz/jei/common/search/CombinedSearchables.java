/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import mezz.jei.common.search.ISearchable;
import mezz.jei.common.search.SearchMode;

public class CombinedSearchables<T>
implements ISearchable<T> {
    private final List<ISearchable<T>> searchables = new ArrayList<ISearchable<T>>();

    @Override
    public void getSearchResults(String word, Consumer<Collection<T>> resultsConsumer) {
        for (ISearchable<T> searchable : this.searchables) {
            if (searchable.getMode() != SearchMode.ENABLED) continue;
            searchable.getSearchResults(word, resultsConsumer);
        }
    }

    @Override
    public void getAllElements(Consumer<Collection<T>> resultsConsumer) {
        for (ISearchable<T> searchable : this.searchables) {
            if (searchable.getMode() != SearchMode.ENABLED) continue;
            searchable.getAllElements(resultsConsumer);
        }
    }

    public void addSearchable(ISearchable<T> searchable) {
        this.searchables.add(searchable);
    }
}

