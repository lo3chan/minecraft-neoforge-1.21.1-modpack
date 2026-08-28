/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.search;

import mezz.jei.api.search.ISearchStorage;

public interface ISearchStorageBuilder<T> {
    public void put(String var1, T var2);

    public ISearchStorage<T> build();
}

