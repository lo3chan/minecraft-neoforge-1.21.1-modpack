package me.lucko.spark.common.monitor.cpu;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import me.lucko.spark.common.monitor.MonitoringExecutor;
import me.lucko.spark.common.util.RollingAverage;

public enum CpuMonitor {
   private static final String OPERATING_SYSTEM_BEAN = "java.lang:type=OperatingSystem";
   private static final CpuMonitor.OperatingSystemMXBean BEAN;
   private static final RollingAverage SYSTEM_AVERAGE_10_SEC = new RollingAverage(10);
   private static final RollingAverage SYSTEM_AVERAGE_1_MIN = new RollingAverage(60);
   private static final RollingAverage SYSTEM_AVERAGE_15_MIN = new RollingAverage(900);
   private static final RollingAverage PROCESS_AVERAGE_10_SEC = new RollingAverage(10);
   private static final RollingAverage PROCESS_AVERAGE_1_MIN = new RollingAverage(60);
   private static final RollingAverage PROCESS_AVERAGE_15_MIN = new RollingAverage(900);

   public static void ensureMonitoring() {
   }

   public static double systemLoad() {
      return BEAN.getSystemCpuLoad();
   }

   public static double systemLoad10SecAvg() {
      return SYSTEM_AVERAGE_10_SEC.mean();
   }

   public static double systemLoad1MinAvg() {
      return SYSTEM_AVERAGE_1_MIN.mean();
   }

   public static double systemLoad15MinAvg() {
      return SYSTEM_AVERAGE_15_MIN.mean();
   }

   public static double processLoad() {
      return BEAN.getProcessCpuLoad();
   }

   public static double processLoad10SecAvg() {
      return PROCESS_AVERAGE_10_SEC.mean();
   }

   public static double processLoad1MinAvg() {
      return PROCESS_AVERAGE_1_MIN.mean();
   }

   public static double processLoad15MinAvg() {
      return PROCESS_AVERAGE_15_MIN.mean();
   }

   static {
      try {
         MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
         ObjectName diagnosticBeanName = ObjectName.getInstance("java.lang:type=OperatingSystem");
         BEAN = JMX.newMXBeanProxy(beanServer, diagnosticBeanName, CpuMonitor.OperatingSystemMXBean.class);
      } catch (Exception var2) {
         throw new UnsupportedOperationException("OperatingSystemMXBean is not supported by the system", var2);
      }

      MonitoringExecutor.INSTANCE.scheduleAtFixedRate(new CpuMonitor.RollingAverageCollectionTask(), 1L, 1L, TimeUnit.SECONDS);
   }

   public interface OperatingSystemMXBean {
      double getSystemCpuLoad();

      double getProcessCpuLoad();
   }

   private static final class RollingAverageCollectionTask implements Runnable {
      private final RollingAverage[] systemAverages = new RollingAverage[]{
         CpuMonitor.SYSTEM_AVERAGE_10_SEC, CpuMonitor.SYSTEM_AVERAGE_1_MIN, CpuMonitor.SYSTEM_AVERAGE_15_MIN
      };
      private final RollingAverage[] processAverages = new RollingAverage[]{
         CpuMonitor.PROCESS_AVERAGE_10_SEC, CpuMonitor.PROCESS_AVERAGE_1_MIN, CpuMonitor.PROCESS_AVERAGE_15_MIN
      };

      private RollingAverageCollectionTask() {
      }

      @Override
      public void run() {
         BigDecimal systemCpuLoad = new BigDecimal(CpuMonitor.systemLoad());
         BigDecimal processCpuLoad = new BigDecimal(CpuMonitor.processLoad());
         if (systemCpuLoad.signum() != -1) {
            for (RollingAverage average : this.systemAverages) {
               average.add(systemCpuLoad);
            }
         }

         if (processCpuLoad.signum() != -1) {
            for (RollingAverage average : this.processAverages) {
               average.add(processCpuLoad);
            }
         }
      }
   }
}
