package me.lucko.spark.common.util;

import java.util.Locale;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.format.TextColor;

public enum FormatUtil {
   private static final String[] SIZE_UNITS = new String[]{"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

   public static String percent(double value, double max) {
      double percent = value * 100.0 / max;
      return (int)percent + "%";
   }

   public static String formatBytes(long bytes) {
      if (bytes <= 0L) {
         return "0 bytes";
      } else {
         int sizeIndex = (int)(Math.log(bytes) / Math.log(1024.0));
         return String.format(Locale.ENGLISH, "%.1f", bytes / Math.pow(1024.0, sizeIndex)) + " " + SIZE_UNITS[sizeIndex];
      }
   }

   public static Component formatBytes(long bytes, TextColor color, String suffix) {
      String value;
      String unit;
      if (bytes <= 0L) {
         value = "0";
         unit = "KB" + suffix;
      } else {
         int sizeIndex = (int)(Math.log(bytes) / Math.log(1024.0));
         value = String.format(Locale.ENGLISH, "%.1f", bytes / Math.pow(1024.0, sizeIndex));
         unit = SIZE_UNITS[sizeIndex] + suffix;
      }

      return Component.text().append(Component.text(value, color)).append(Component.space()).append(Component.text(unit)).build();
   }

   public static String formatSeconds(long seconds) {
      if (seconds <= 0L) {
         return "0s";
      } else {
         long minute = seconds / 60L;
         long second = seconds % 60L;
         StringBuilder sb = new StringBuilder();
         if (minute != 0L) {
            sb.append(minute).append("m ");
         }

         if (second != 0L) {
            sb.append(second).append("s ");
         }

         return sb.toString().trim();
      }
   }
}
