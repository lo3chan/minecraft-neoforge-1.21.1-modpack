package mezz.jei.common.util;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
   public static String toHumanString(Duration duration) {
      TimeUnit unit = getSmallestUnit(duration);
      long nanos = duration.toNanos();
      double value = (double)nanos / TimeUnit.NANOSECONDS.convert(1L, unit);
      return String.format(Locale.ROOT, "%.4g %s", value, unitToString(unit));
   }

   private static TimeUnit getSmallestUnit(Duration duration) {
      if (duration.toDays() > 0L) {
         return TimeUnit.DAYS;
      } else if (duration.toHours() > 0L) {
         return TimeUnit.HOURS;
      } else if (duration.toMinutes() > 0L) {
         return TimeUnit.MINUTES;
      } else if (duration.toSeconds() > 0L) {
         return TimeUnit.SECONDS;
      } else if (duration.toMillis() > 0L) {
         return TimeUnit.MILLISECONDS;
      } else {
         return duration.toNanos() > 1000L ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
      }
   }

   private static String unitToString(TimeUnit unit) {
      return unit.name().toLowerCase(Locale.ROOT);
   }
}
