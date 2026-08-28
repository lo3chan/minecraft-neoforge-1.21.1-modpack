/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.search;

import java.util.Collection;
import java.util.function.Consumer;

public interface ISearchStorage<T> {
    public void getSearchResults(String var1, Consumer<Collection<T>> var2);

    public void getAllElements(Consumer<Collection<T>> var1);

    public void put(String var1, T var2);

    public String statistics();
}

