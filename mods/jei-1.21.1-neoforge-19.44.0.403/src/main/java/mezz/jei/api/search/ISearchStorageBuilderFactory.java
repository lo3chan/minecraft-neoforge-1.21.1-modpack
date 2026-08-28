/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.api.search;

import java.util.Objects;
import mezz.jei.api.search.ISearchStorageBuilder;

@FunctionalInterface
public interface ISearchStorageBuilderFactory {
    public <T> ISearchStorageBuilder<T> create();

    default public <T> ISearchStorageBuilder<T> create(String id) {
        Objects.requireNonNull(id, "id");
        return this.create();
    }
}

