package pl.skidam.automodpack_core.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimedSet<T> {
   private final Map<T, TimedSet.Entry> map = new ConcurrentHashMap<>();
   private final long lifetimeMillis;

   public TimedSet(long lifetimeMillis) {
      this.lifetimeMillis = lifetimeMillis;
   }

   public void add(T value) {
      this.cleanup();
      this.map.put(value, new TimedSet.Entry(System.currentTimeMillis() + this.lifetimeMillis));
   }

   public boolean contains(T value) {
      this.cleanup();
      TimedSet.Entry e = this.map.get(value);
      return e != null && e.expiryTime() > System.currentTimeMillis();
   }

   private void cleanup() {
      long now = System.currentTimeMillis();
      this.map.entrySet().removeIf(e -> e.getValue().expiryTime() <= now);
   }

   private record Entry(long expiryTime) {
   }
}
