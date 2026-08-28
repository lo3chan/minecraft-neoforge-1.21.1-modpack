/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableTable
 *  com.google.common.collect.ImmutableTable$Builder
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.collect;

import com.google.common.collect.ImmutableTable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class Table<R, C, V> {
    private final Map<R, Map<C, V>> table;
    private final Function<R, Map<C, V>> rowMappingFunction;

    public static <R, C, V> Table<R, C, V> hashBasedTable() {
        return new Table(new HashMap(), HashMap::new);
    }

    public static <R, C, V> Table<R, C, V> identityHashBasedTable() {
        return new Table(new IdentityHashMap(), IdentityHashMap::new);
    }

    public Table(Map<R, Map<C, V>> table, Supplier<Map<C, V>> rowSupplier) {
        this.table = table;
        this.rowMappingFunction = k -> (Map)rowSupplier.get();
    }

    @Nullable
    public V get(R row, C col) {
        Map<C, V> rowMap = this.getRow(row);
        return rowMap.get(col);
    }

    public V computeIfAbsent(R row, C col, Supplier<V> valueSupplier) {
        Map<C, Object> rowMap = this.getRow(row);
        return (V)rowMap.computeIfAbsent(col, k -> valueSupplier.get());
    }

    @Nullable
    public V put(R row, C col, V val) {
        Map<C, V> rowMap = this.getRow(row);
        return rowMap.put(col, val);
    }

    public Map<C, V> getRow(R row) {
        return this.table.computeIfAbsent(row, this.rowMappingFunction);
    }

    public boolean contains(R row, C col) {
        Map<C, V> map = this.table.get(row);
        return map != null && map.containsKey(col);
    }

    public void clear() {
        this.table.clear();
    }

    public ImmutableTable<R, C, V> toImmutable() {
        ImmutableTable.Builder builder = ImmutableTable.builder();
        for (Map.Entry<R, Map<C, V>> entry : this.table.entrySet()) {
            R row = entry.getKey();
            for (Map.Entry<C, V> rowEntry : entry.getValue().entrySet()) {
                C col = rowEntry.getKey();
                V val = rowEntry.getValue();
                builder.put(row, col, val);
            }
        }
        return builder.build();
    }
}

