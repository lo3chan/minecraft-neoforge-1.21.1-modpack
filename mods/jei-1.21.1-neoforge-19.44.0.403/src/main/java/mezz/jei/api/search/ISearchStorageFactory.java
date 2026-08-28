/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.search;

import mezz.jei.api.search.ISearchStorage;

@FunctionalInterface
public interface ISearchStorageFactory {
    public <T> ISearchStorage<T> createSearchStorage();
}

