package me.lucko.spark.common.monitor.memory;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import me.lucko.spark.common.monitor.LinuxProc;

public enum MemoryInfo {
   private static final String OPERATING_SYSTEM_BEAN = "java.lang:type=OperatingSystem";
   private static final MemoryInfo.OperatingSystemMXBean BEAN;
   private static final Pattern PROC_MEMINFO_VALUE = Pattern.compile("^(\\w+):\\s*(\\d+) kB$");

   public static long getUsedSwap() {
      return BEAN.getTotalSwapSpaceSize() - BEAN.getFreeSwapSpaceSize();
   }

   public static long getTotalSwap() {
      return BEAN.getTotalSwapSpaceSize();
   }

   public static long getUsedPhysicalMemory() {
      return getTotalPhysicalMemory() - getAvailablePhysicalMemory();
   }

   public static long getTotalPhysicalMemory() {
      for (String line : LinuxProc.MEMINFO.read()) {
         Matcher matcher = PROC_MEMINFO_VALUE.matcher(line);
         if (matcher.matches()) {
            String label = matcher.group(1);
            long value = Long.parseLong(matcher.group(2)) * 1024L;
            if (label.equals("MemTotal")) {
               return value;
            }
         }
      }

      return BEAN.getTotalPhysicalMemorySize();
   }

   public static long getAvailablePhysicalMemory() {
      boolean present = false;
      long free = 0L;
      long buffers = 0L;
      long cached = 0L;
      long sReclaimable = 0L;

      for (String line : LinuxProc.MEMINFO.read()) {
         Matcher matcher = PROC_MEMINFO_VALUE.matcher(line);
         if (matcher.matches()) {
            present = true;
            String label = matcher.group(1);
            long value = Long.parseLong(matcher.group(2)) * 1024L;
            if (label.equals("MemAvailable")) {
               return value;
            }

            switch (label) {
               case "MemFree":
                  free = value;
                  break;
               case "Buffers":
                  buffers = value;
                  break;
               case "Cached":
                  cached = value;
                  break;
               case "SReclaimable":
                  sReclaimable = value;
            }
         }
      }

      return present ? free + buffers + cached + sReclaimable : BEAN.getFreePhysicalMemorySize();
   }

   public static long getTotalVirtualMemory() {
      return BEAN.getCommittedVirtualMemorySize();
   }

   static {
      try {
         MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
         ObjectName diagnosticBeanName = ObjectName.getInstance("java.lang:type=OperatingSystem");
         BEAN = JMX.newMXBeanProxy(beanServer, diagnosticBeanName, MemoryInfo.OperatingSystemMXBean.class);
      } catch (Exception var2) {
         throw new UnsupportedOperationException("OperatingSystemMXBean is not supported by the system", var2);
      }
   }

   public interface OperatingSystemMXBean {
      long getCommittedVirtualMemorySize();

      long getTotalSwapSpaceSize();

      long getFreeSwapSpaceSize();

      long getFreePhysicalMemorySize();

      long getTotalPhysicalMemorySize();
   }
}
