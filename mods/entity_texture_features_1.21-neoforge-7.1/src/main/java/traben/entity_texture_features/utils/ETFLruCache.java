/*
 * Decompiled with CFR 0.152.
 */
package traben.entity_texture_features.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import traben.entity_texture_features.ETF;

public class ETFLruCache<X, Y>
extends LinkedHashMap<X, Y> {
    final int capacity;
    protected Y defaultReturn = null;

    public ETFLruCache() {
        super(2048, 0.75f, true);
        this.capacity = 2048;
    }

    public ETFLruCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<X, Y> eldest) {
        double sizeModifier = Math.max(1.0, ETF.config().getConfig().advanced_IncreaseCacheSizeModifier);
        return (double)this.size() >= (double)this.capacity * sizeModifier;
    }

    public void removeEntryOnly(X key) {
        this.remove(key);
    }

    @Override
    public Y get(Object key) {
        return super.getOrDefault(key, this.defaultReturn);
    }

    public void defaultReturnValue(Y value) {
        this.defaultReturn = value;
    }

    public static class UUIDInteger
    extends ETFLruCache<UUID, Integer> {
        public UUIDInteger() {
            this.defaultReturnValue(-1);
        }

        public UUIDInteger(int capacity) {
            super(capacity);
            this.defaultReturnValue(-1);
        }
    }

    public static class UUIDBoolean
    extends ETFLruCache<UUID, Boolean> {
        public UUIDBoolean() {
            this.defaultReturnValue(false);
        }

        public UUIDBoolean(int capacity) {
            super(capacity);
            this.defaultReturnValue(false);
        }
    }
}

