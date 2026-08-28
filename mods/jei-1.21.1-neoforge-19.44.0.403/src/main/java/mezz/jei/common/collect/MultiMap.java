/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 */
package mezz.jei.common.collect;

import com.google.common.collect.ImmutableMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiMap<K, V, T extends Collection<V>> {
    protected final Map<K, T> map;
    private final Function<K, T> collectionMappingFunction;

    public MultiMap(Supplier<T> collectionSupplier) {
        this(new HashMap(), collectionSupplier);
    }

    public MultiMap(Map<K, T> map, Supplier<T> collectionSupplier) {
        this.map = map;
        this.collectionMappingFunction = k -> (Collection)collectionSupplier.get();
    }

    public Collection<V> get(K key) {
        Collection collection = (Collection)this.map.get(key);
        if (collection != null) {
            return Collections.unmodifiableCollection(collection);
        }
        return Collections.emptyList();
    }

    public boolean put(K key, V value) {
        Collection collection = (Collection)this.map.computeIfAbsent(key, this.collectionMappingFunction);
        return collection.add(value);
    }

    public boolean putAll(K key, Collection<V> values) {
        Collection collection = (Collection)this.map.computeIfAbsent(key, this.collectionMappingFunction);
        return collection.addAll(values);
    }

    public boolean remove(K key, V value) {
        Collection collection = (Collection)this.map.get(key);
        return collection != null && collection.remove(value);
    }

    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    public boolean contains(K key, V value) {
        Collection collection = (Collection)this.map.get(key);
        return collection != null && collection.contains(value);
    }

    public Set<Map.Entry<K, T>> entrySet() {
        return this.map.entrySet();
    }

    public Set<K> keySet() {
        return this.map.keySet();
    }

    public Collection<V> allValues() {
        ArrayList list = new ArrayList();
        for (Collection t : this.map.values()) {
            list.addAll(t);
        }
        return list;
    }

    public void clear() {
        this.map.clear();
    }

    public ImmutableMultimap<K, V> toImmutable() {
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        this.map.forEach((arg_0, arg_1) -> ((ImmutableMultimap.Builder)builder).putAll(arg_0, arg_1));
        return builder.build();
    }
}

