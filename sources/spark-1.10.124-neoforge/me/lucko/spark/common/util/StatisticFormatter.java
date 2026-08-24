package me.lucko.spark.common.util;

import com.google.common.base.Strings;
import java.lang.management.MemoryUsage;
import java.util.Locale;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.adventure.text.format.TextColor;

public enum StatisticFormatter {
   private static final String BAR_TRUE_CHARACTER = "┃";
   private static final String BAR_FALSE_CHARACTER = "╻";

   public static TextComponent formatTps(double tps) {
      TextColor color;
      if (tps > 18.0) {
         color = NamedTextColor.GREEN;
      } else if (tps > 16.0) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.RED;
      }

      return Component.text((tps > 20.0 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0), color);
   }

   public static TextComponent formatTickDurations(DoubleAverageInfo average) {
      return Component.text()
         .append(formatTickDuration(average.min()))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatTickDuration(average.median()))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatTickDuration(average.percentile95th()))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatTickDuration(average.max()))
         .build();
   }

   public static TextComponent formatTickDuration(double duration) {
      TextColor color;
      if (duration >= 50.0) {
         color = NamedTextColor.RED;
      } else if (duration >= 40.0) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.GREEN;
      }

      return Component.text(String.format(Locale.ENGLISH, "%.1f", duration), color);
   }

   public static TextComponent formatCpuUsage(double usage) {
      TextColor color;
      if (usage > 0.9) {
         color = NamedTextColor.RED;
      } else if (usage > 0.65) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.GREEN;
      }

      return Component.text(FormatUtil.percent(usage, 1.0), color);
   }

   public static TextComponent formatPingRtts(double min, double median, double percentile95th, double max) {
      return Component.text()
         .append(formatPingRtt(min))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatPingRtt(median))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatPingRtt(percentile95th))
         .append(Component.text('/', NamedTextColor.GRAY))
         .append(formatPingRtt(max))
         .build();
   }

   public static TextComponent formatPingRtt(double ping) {
      TextColor color;
      if (ping >= 200.0) {
         color = NamedTextColor.RED;
      } else if (ping >= 100.0) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.GREEN;
      }

      return Component.text((int)Math.ceil(ping), color);
   }

   public static TextComponent generateMemoryUsageDiagram(MemoryUsage usage, int length) {
      double used = usage.getUsed();
      double committed = usage.getCommitted();
      double max = usage.getMax();
      int usedChars = (int)(used * length / max);
      int committedChars = (int)(committed * length / max);
      TextComponent.Builder line = Component.text().content(Strings.repeat("┃", usedChars)).color(NamedTextColor.YELLOW);
      if (committedChars > usedChars) {
         line.append(Component.text(Strings.repeat("╻", committedChars - usedChars - 1), NamedTextColor.GRAY));
         line.append(Component.text("╻", NamedTextColor.RED));
      }

      if (length > committedChars) {
         line.append(Component.text(Strings.repeat("╻", length - committedChars), NamedTextColor.GRAY));
      }

      return Component.text()
         .append(Component.text("[", NamedTextColor.DARK_GRAY))
         .append(line.build())
         .append(Component.text("]", NamedTextColor.DARK_GRAY))
         .build();
   }

   public static TextComponent generateMemoryPoolDiagram(MemoryUsage usage, MemoryUsage collectionUsage, int length) {
      double used = usage.getUsed();
      double collectionUsed = used;
      if (collectionUsage != null) {
         collectionUsed = collectionUsage.getUsed();
      }

      double committed = usage.getCommitted();
      double max = usage.getMax();
      int usedChars = (int)(used * length / max);
      int collectionUsedChars = (int)(collectionUsed * length / max);
      int committedChars = (int)(committed * length / max);
      TextComponent.Builder line = Component.text().content(Strings.repeat("┃", collectionUsedChars)).color(NamedTextColor.YELLOW);
      if (usedChars > collectionUsedChars) {
         line.append(Component.text("┃", NamedTextColor.RED));
         line.append(Component.text(Strings.repeat("┃", usedChars - collectionUsedChars - 1), NamedTextColor.YELLOW));
      }

      if (committedChars > usedChars) {
         line.append(Component.text(Strings.repeat("╻", committedChars - usedChars - 1), NamedTextColor.GRAY));
         line.append(Component.text("╻", NamedTextColor.YELLOW));
      }

      if (length > committedChars) {
         line.append(Component.text(Strings.repeat("╻", length - committedChars), NamedTextColor.GRAY));
      }

      return Component.text()
         .append(Component.text("[", NamedTextColor.DARK_GRAY))
         .append(line.build())
         .append(Component.text("]", NamedTextColor.DARK_GRAY))
         .build();
   }

   public static TextComponent generateDiskUsageDiagram(double used, double max, int length) {
      int usedChars = (int)(used * length / max);
      int freeChars = length - usedChars;
      return Component.text()
         .append(Component.text("[", NamedTextColor.DARK_GRAY))
         .append(Component.text(Strings.repeat("┃", usedChars), NamedTextColor.YELLOW))
         .append(Component.text(Strings.repeat("╻", freeChars), NamedTextColor.GRAY))
         .append(Component.text("]", NamedTextColor.DARK_GRAY))
         .build();
   }
}
