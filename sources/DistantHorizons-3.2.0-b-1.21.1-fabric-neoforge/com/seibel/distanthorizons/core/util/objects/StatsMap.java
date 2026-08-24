package com.seibel.distanthorizons.core.util.objects;

import com.seibel.distanthorizons.core.util.math.UnitBytes;
import java.util.TreeMap;

public class StatsMap {
   final TreeMap<String, Long> longMap = new TreeMap<>();
   final TreeMap<String, UnitBytes> bytesMap = new TreeMap<>();
   private static final long serialVersionUID = 1926219295516863173L;

   public void incStat(String key) {
      this.incStat(key, 1L);
   }

   public void incStat(String key, long value) {
      this.longMap.put(key, this.longMap.getOrDefault(key, 0L) + value);
   }

   public void incBytesStat(String key, long bytes) {
      long b = this.bytesMap.getOrDefault(key, new UnitBytes(0L)).value;
      this.bytesMap.put(key, new UnitBytes(b + bytes));
   }

   @Override
   public String toString() {
      return this.longMap.toString() + " " + this.bytesMap.toString();
   }
}
