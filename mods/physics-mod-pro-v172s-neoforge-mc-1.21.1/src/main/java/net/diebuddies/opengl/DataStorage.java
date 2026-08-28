/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import net.diebuddies.opengl.Data;

public class DataStorage {
    protected Map<Data, Object> data = new Object2ObjectOpenHashMap();
    protected Map<Data, Integer> sizes = new Object2ObjectOpenHashMap();

    public DataStorage(DataStorage storage) {
        for (Map.Entry<Data, Object> entry : storage.data.entrySet()) {
            Object[] values;
            Object val = entry.getValue();
            if (val instanceof short[]) {
                values = (short[])val;
                this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
                continue;
            }
            if (val instanceof byte[]) {
                values = (byte[])val;
                this.data.put(entry.getKey(), Arrays.copyOf((byte[])values, values.length));
                continue;
            }
            if (val instanceof int[]) {
                values = (int[])val;
                this.data.put(entry.getKey(), Arrays.copyOf((int[])values, values.length));
                continue;
            }
            if (val instanceof long[]) {
                values = (long[])val;
                this.data.put(entry.getKey(), Arrays.copyOf((long[])values, values.length));
                continue;
            }
            if (val instanceof float[]) {
                values = (float[])val;
                this.data.put(entry.getKey(), Arrays.copyOf((float[])values, values.length));
                continue;
            }
            if (!(val instanceof double[])) continue;
            values = (double[])val;
            this.data.put(entry.getKey(), Arrays.copyOf((double[])values, values.length));
        }
        for (Map.Entry<Data, Object> entry : storage.sizes.entrySet()) {
            this.sizes.put(entry.getKey(), (Integer)entry.getValue());
        }
    }

    public DataStorage() {
    }

    public Object getNative(Data type) {
        return this.data.get(type);
    }

    public Set<Map.Entry<Data, Object>> getEntrySet() {
        return this.data.entrySet();
    }

    public void set(byte[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public void set(float[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public void set(double[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public void set(int[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public void set(short[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public void set(long[] data, Data type) {
        this.sizes.put(type, data.length);
        this.data.put(type, data);
    }

    public int size(Data type) {
        Integer size = this.sizes.get(type);
        if (size == null) {
            return 0;
        }
        return size;
    }

    public Map<Data, Object> getData() {
        return this.data;
    }

    public void setSize(Data type, int size) {
        this.sizes.put(type, size);
    }
}

