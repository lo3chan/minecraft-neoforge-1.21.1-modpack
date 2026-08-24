package me.lucko.spark.common.monitor.net;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.lucko.spark.common.monitor.MonitoringExecutor;

public enum NetworkMonitor {
   private static final AtomicReference<Map<String, NetworkInterfaceInfo>> SYSTEM = new AtomicReference<>();
   private static final Pattern INTERFACES_TO_IGNORE = Pattern.compile("^(veth\\w+)|(br-\\w+)$");
   private static final Map<String, NetworkInterfaceAverages> SYSTEM_AVERAGES = new ConcurrentHashMap<>();
   private static final int POLL_INTERVAL = 60;
   private static final int WINDOW_SIZE_SECONDS = (int)TimeUnit.MINUTES.toSeconds(15L);
   private static final int WINDOW_SIZE = WINDOW_SIZE_SECONDS / 60;

   public static void ensureMonitoring() {
   }

   public static Map<String, NetworkInterfaceInfo> systemTotals() {
      Map<String, NetworkInterfaceInfo> values = SYSTEM.get();
      return values == null ? Collections.emptyMap() : values;
   }

   public static Map<String, NetworkInterfaceAverages> systemAverages() {
      return Collections.unmodifiableMap(SYSTEM_AVERAGES);
   }

   static {
      MonitoringExecutor.INSTANCE.scheduleAtFixedRate(new NetworkMonitor.RollingAverageCollectionTask(), 1L, 60L, TimeUnit.SECONDS);
   }

   private static final class RollingAverageCollectionTask implements Runnable {
      private static final BigDecimal POLL_INTERVAL_DECIMAL = BigDecimal.valueOf(60L);

      private RollingAverageCollectionTask() {
      }

      @Override
      public void run() {
         Map<String, NetworkInterfaceInfo> values = pollAndDiff(NetworkInterfaceInfo::pollSystem, NetworkMonitor.SYSTEM);
         if (values != null) {
            submit(NetworkMonitor.SYSTEM_AVERAGES, values);
         }
      }

      private static void submit(Map<String, NetworkInterfaceAverages> rollingAveragesMap, Map<String, NetworkInterfaceInfo> values) {
         for (String key : values.keySet()) {
            if (!NetworkMonitor.INTERFACES_TO_IGNORE.matcher(key).matches()) {
               rollingAveragesMap.computeIfAbsent(key, k -> new NetworkInterfaceAverages(NetworkMonitor.WINDOW_SIZE));
            }
         }

         for (Entry<String, NetworkInterfaceAverages> entry : rollingAveragesMap.entrySet()) {
            String interfaceName = entry.getKey();
            NetworkInterfaceAverages rollingAvgs = entry.getValue();
            NetworkInterfaceInfo info = values.getOrDefault(interfaceName, NetworkInterfaceInfo.ZERO);
            rollingAvgs.accept(info, NetworkMonitor.RollingAverageCollectionTask::calculateRate);
         }
      }

      private static BigDecimal calculateRate(long value) {
         return BigDecimal.valueOf(value).divide(POLL_INTERVAL_DECIMAL, RoundingMode.HALF_UP);
      }

      private static Map<String, NetworkInterfaceInfo> pollAndDiff(
         Supplier<Map<String, NetworkInterfaceInfo>> poller, AtomicReference<Map<String, NetworkInterfaceInfo>> valueReference
      ) {
         Map<String, NetworkInterfaceInfo> latest = poller.get();
         Map<String, NetworkInterfaceInfo> previous = valueReference.getAndUpdate(prev -> prev == null && latest.isEmpty() ? null : latest);
         return previous == null ? null : NetworkInterfaceInfo.difference(latest, previous);
      }
   }
}
