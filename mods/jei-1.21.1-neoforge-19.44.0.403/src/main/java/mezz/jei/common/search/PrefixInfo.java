/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.common.search;

import java.util.Collection;
import mezz.jei.api.search.ISearchStorageBuilder;
import mezz.jei.api.search.ISearchStorageBuilderFactory;
import mezz.jei.common.search.SearchMode;
import org.jetbrains.annotations.Unmodifiable;

public class PrefixInfo<T, I> {
    private final String id;
    private final char prefix;
    private final IModeGetter modeGetter;
    private final IStringsGetter<T> stringsGetter;
    private final ISearchStorageBuilderFactory searchStorageBuilderFactory;

    public PrefixInfo(String id, char prefix, IModeGetter modeGetter, IStringsGetter<T> stringsGetter, ISearchStorageBuilderFactory searchStorageBuilderFactory) {
        this.id = id;
        this.prefix = prefix;
        this.modeGetter = modeGetter;
        this.stringsGetter = stringsGetter;
        this.searchStorageBuilderFactory = searchStorageBuilderFactory;
    }

    public char getPrefix() {
        return this.prefix;
    }

    public SearchMode getMode() {
        return this.modeGetter.getMode();
    }

    public ISearchStorageBuilder<I> createStorageBuilder() {
        return this.searchStorageBuilderFactory.create(this.id);
    }

    public @Unmodifiable Collection<String> getStrings(T element) {
        return this.stringsGetter.getStrings(element);
    }

    public String toString() {
        return "PrefixInfo{" + this.id + "}";
    }

    @FunctionalInterface
    public static interface IModeGetter {
        public SearchMode getMode();
    }

    @FunctionalInterface
    public static interface IStringsGetter<T> {
        public @Unmodifiable Collection<String> getStrings(T var1);
    }
}

