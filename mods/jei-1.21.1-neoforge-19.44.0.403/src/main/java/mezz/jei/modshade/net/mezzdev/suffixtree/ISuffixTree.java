/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.modshade.net.mezzdev.suffixtree;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

public interface ISuffixTree<T> {
    public void put(String var1, T var2);

    public void getSearchResults(String var1, Consumer<Collection<T>> var2);

    default public Set<T> getSearchResults(String token) {
        Set results = Collections.newSetFromMap(new IdentityHashMap());
        this.getSearchResults(token, results::addAll);
        return results;
    }

    public void getAllElements(Consumer<Collection<T>> var1);

    default public Set<T> getAllElements() {
        Set results = Collections.newSetFromMap(new IdentityHashMap());
        this.getAllElements(results::addAll);
        return results;
    }

    public String statistics();
}

