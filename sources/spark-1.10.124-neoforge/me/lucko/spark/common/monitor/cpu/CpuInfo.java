package me.lucko.spark.common.monitor.cpu;

import java.util.regex.Pattern;
import me.lucko.spark.common.monitor.LinuxProc;
import me.lucko.spark.common.monitor.MacosSysctl;
import me.lucko.spark.common.monitor.WindowsWmic;

public enum CpuInfo {
   private static final Pattern SPACE_COLON_SPACE_PATTERN = Pattern.compile("\\s+:\\s");

   public static String queryCpuModel() {
      for (String line : LinuxProc.CPUINFO.read()) {
         String[] splitLine = SPACE_COLON_SPACE_PATTERN.split(line);
         if (splitLine[0].equals("model name") || splitLine[0].equals("Processor")) {
            return splitLine[1];
         }
      }

      for (String linex : WindowsWmic.CPU_GET_NAME.read()) {
         if (linex.startsWith("Name")) {
            return linex.substring(5).trim();
         }
      }

      for (String linexx : MacosSysctl.SYSCTL.read()) {
         if (linexx.startsWith("machdep.cpu.brand_string:")) {
            return linexx.substring("machdep.cpu.brand_string:".length()).trim();
         }
      }

      return "";
   }
}
