package net.diebuddies.opengl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DataStorage {
   protected Map<Data, Object> data = new Object2ObjectOpenHashMap();
   protected Map<Data, Integer> sizes = new Object2ObjectOpenHashMap();

   public DataStorage(DataStorage storage) {
      for (Entry<Data, Object> entry : storage.data.entrySet()) {
         Object val = entry.getValue();
         if (val instanceof short[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         } else if (val instanceof byte[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         } else if (val instanceof int[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         } else if (val instanceof long[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         } else if (val instanceof float[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         } else if (val instanceof double[] values) {
            this.data.put(entry.getKey(), Arrays.copyOf(values, values.length));
         }
      }

      for (Entry<Data, Integer> entryx : storage.sizes.entrySet()) {
         this.sizes.put(entryx.getKey(), entryx.getValue());
      }
   }

   public DataStorage() {
   }

   public Object getNative(Data type) {
      return this.data.get(type);
   }

   public Set<Entry<Data, Object>> getEntrySet() {
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
      return size == null ? 0 : size;
   }

   public Map<Data, Object> getData() {
      return this.data;
   }

   public void setSize(Data type, int size) {
      this.sizes.put(type, size);
   }
}
