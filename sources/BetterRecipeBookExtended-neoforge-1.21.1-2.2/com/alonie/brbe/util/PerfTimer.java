package com.alonie.brbe.util;

import com.alonie.brbe.BetterRecipeBook;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PerfTimer {
   private static final Logger LOGGER = LogManager.getLogger("BRBE-Perf");
   private static final Object2LongOpenHashMap<String> accumNanos = new Object2LongOpenHashMap();
   private static final Object2LongOpenHashMap<String> counts = new Object2LongOpenHashMap();
   private static final ThreadLocal<Object2LongOpenHashMap<String>> active = ThreadLocal.withInitial(Object2LongOpenHashMap::new);
   private static boolean enabled;
   public static boolean logNextRenderFrame;

   private PerfTimer() {
   }

   public static void begin() {
      enabled = BetterRecipeBook.ctx().config() != null;
      if (enabled) {
         active.get().clear();
      }
   }

   public static void start(String section) {
      if (enabled) {
         active.get().put(section, System.nanoTime());
      }
   }

   public static void end(String section) {
      if (enabled) {
         long elapsed = System.nanoTime() - active.get().getLong(section);
         accumNanos.addTo(section, elapsed);
         counts.addTo(section, 1L);
      }
   }

   public static void logAndReset(String context) {
      if (enabled && !accumNanos.isEmpty()) {
         long total = 0L;
         ObjectIterator var3 = accumNanos.object2LongEntrySet().iterator();

         while (var3.hasNext()) {
            Entry<String> entry = (Entry<String>)var3.next();
            total += entry.getLongValue();
         }

         LOGGER.info("--- BRBE-Perf [{}] total={}ms ---", context, total / 1000000L);
         accumNanos.object2LongEntrySet().stream().sorted((a, b) -> Long.compare(b.getLongValue(), a.getLongValue())).forEach(e -> {
            String section = (String)e.getKey();
            long nanos = e.getLongValue();
            long cnt = counts.getLong(section);
            long avg = cnt > 0L ? nanos / cnt : nanos;
            LOGGER.info("  {} : {}ms ({}x, avg {}µs)", section, nanos / 1000000L, cnt, avg / 1000L);
         });
         accumNanos.clear();
         counts.clear();
         enabled = false;
      }
   }
}
