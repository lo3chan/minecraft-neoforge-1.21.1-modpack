/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 */
package mezz.jei.common.collect;

import com.google.common.collect.ImmutableSetMultimap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import mezz.jei.common.collect.MultiMap;

public class SetMultiMap<K, V>
extends MultiMap<K, V, Set<V>> {
    public SetMultiMap() {
        this(HashSet::new);
    }

    public SetMultiMap(Supplier<Set<V>> collectionSupplier) {
        super(collectionSupplier);
    }

    public SetMultiMap(Map<K, Set<V>> map, Supplier<Set<V>> collectionSupplier) {
        super(map, collectionSupplier);
    }

    @Override
    public Set<V> get(K key) {
        Set collection = (Set)this.map.get(key);
        if (collection != null) {
            return Collections.unmodifiableSet(collection);
        }
        return Collections.emptySet();
    }

    @Override
    public ImmutableSetMultimap<K, V> toImmutable() {
        ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
        this.map.forEach((arg_0, arg_1) -> ((ImmutableSetMultimap.Builder)builder).putAll(arg_0, arg_1));
        return builder.build();
    }
}

