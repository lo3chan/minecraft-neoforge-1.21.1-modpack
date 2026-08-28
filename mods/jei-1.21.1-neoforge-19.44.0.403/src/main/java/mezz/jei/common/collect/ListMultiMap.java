/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.ImmutableListMultimap$Builder
 */
package mezz.jei.common.collect;

import com.google.common.collect.ImmutableListMultimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import mezz.jei.common.collect.MultiMap;

public class ListMultiMap<K, V>
extends MultiMap<K, V, List<V>> {
    public ListMultiMap() {
        this(ArrayList::new);
    }

    public ListMultiMap(Supplier<List<V>> collectionSupplier) {
        super(collectionSupplier);
    }

    public ListMultiMap(Map<K, List<V>> map, Supplier<List<V>> collectionSupplier) {
        super(map, collectionSupplier);
    }

    @Override
    public List<V> get(K key) {
        List list = (List)this.map.get(key);
        if (list != null) {
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    @Override
    public ImmutableListMultimap<K, V> toImmutable() {
        ImmutableListMultimap.Builder builder = ImmutableListMultimap.builder();
        this.map.forEach((arg_0, arg_1) -> ((ImmutableListMultimap.Builder)builder).putAll(arg_0, arg_1));
        return builder.build();
    }
}

