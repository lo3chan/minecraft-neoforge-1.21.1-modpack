package cc.cosmetica.cosmetica.util;

import java.util.NavigableMap;
import java.util.TreeMap;

public final class Division<T> {
   private final NavigableMap<Double, T> redBlackTree;
   private T min;
   private double minKey = 1.7976931348623157E308;
   private int count = 0;

   public Division() {
      this.redBlackTree = new TreeMap<>();
   }

   public Division<T> addSection(double minBound, T value) {
      if (!this.redBlackTree.containsValue(value)) {
         this.count++;
      }

      this.redBlackTree.put(minBound, value);
      if (minBound < this.minKey) {
         this.minKey = minBound;
         this.min = value;
      }

      return this;
   }

   public int count() {
      return this.count;
   }

   public T get(double key) {
      return key <= this.minKey ? this.min : this.redBlackTree.floorEntry(key).getValue();
   }
}
