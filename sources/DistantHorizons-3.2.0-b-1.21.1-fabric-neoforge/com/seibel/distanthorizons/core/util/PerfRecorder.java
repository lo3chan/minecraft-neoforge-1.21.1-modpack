package com.seibel.distanthorizons.core.util;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class PerfRecorder {
   private static final int TOTAL_WIDTH = 7;
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");
   private final DhLogger logger;
   public final String name;
   public final ConcurrentHashMap<String, LongAdder> nanoPerId = new ConcurrentHashMap<>();
   public String lastPerfLog = "";

   public PerfRecorder(String name) {
      this.name = name;
      this.logger = new DhLoggerBuilder().name(PerfRecorder.class.getSimpleName() + "-" + this.name).maxCountPerSecond(1).build();
   }

   public PerfRecorder.Timer start(String id) {
      return new PerfRecorder.Timer(id);
   }

   public void clear() {
      this.nanoPerId.clear();
   }

   public void tryLog() {
      if (this.logger.canLog()) {
         String perfTime = this.toString();
         if (!perfTime.equals(this.lastPerfLog)) {
            this.lastPerfLog = perfTime;
            this.logger.info(perfTime);
         }
      }
   }

   @Override
   public String toString() {
      ArrayList<String> sortedKeys = new ArrayList<>();
      Enumeration<String> keyEnumerator = this.nanoPerId.keys();

      while (keyEnumerator.hasMoreElements()) {
         sortedKeys.add(keyEnumerator.nextElement());
      }

      sortedKeys.sort(Comparator.naturalOrder());
      StringBuilder builder = new StringBuilder();
      long totalNanoTime = 0L;

      for (String id : sortedKeys) {
         LongAdder nanoTimeAdder = this.nanoPerId.get(id);
         long nsTime = nanoTimeAdder.sum();
         long msTime = TimeUnit.MILLISECONDS.convert(nsTime, TimeUnit.NANOSECONDS);
         totalNanoTime += nsTime;
         String line = id + "[" + formatNumber(msTime) + "] ";
         builder.append(line);
      }

      long totalMsTime = TimeUnit.MILLISECONDS.convert(totalNanoTime, TimeUnit.NANOSECONDS);
      return "Total[" + formatNumber(totalMsTime) + "] " + builder.toString();
   }

   private static String formatNumber(long number) {
      String formattedNumber = DECIMAL_FORMAT.format(number);
      return String.format("%7s", formattedNumber);
   }

   public class Timer {
      protected final String id;
      protected final long startTime;

      private Timer(String id) {
         this.id = id;
         this.startTime = System.nanoTime();
      }

      public void end() {
         long endTime = System.nanoTime();
         long totalNano = endTime - this.startTime;
         LongAdder nsAdder = PerfRecorder.this.nanoPerId.get(this.id);
         if (nsAdder != null) {
            nsAdder.add(totalNano);
         } else {
            nsAdder = PerfRecorder.this.nanoPerId.computeIfAbsent(this.id, id -> new LongAdder());
            nsAdder.add(totalNano);
         }
      }
   }
}
