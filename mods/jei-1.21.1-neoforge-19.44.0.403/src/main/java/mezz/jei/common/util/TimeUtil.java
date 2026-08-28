/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.util;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String toHumanString(Duration duration) {
        TimeUnit unit = TimeUtil.getSmallestUnit(duration);
        long nanos = duration.toNanos();
        double value = (double)nanos / (double)TimeUnit.NANOSECONDS.convert(1L, unit);
        return String.format(Locale.ROOT, "%.4g %s", value, TimeUtil.unitToString(unit));
    }

    private static TimeUnit getSmallestUnit(Duration duration) {
        if (duration.toDays() > 0L) {
            return TimeUnit.DAYS;
        }
        if (duration.toHours() > 0L) {
            return TimeUnit.HOURS;
        }
        if (duration.toMinutes() > 0L) {
            return TimeUnit.MINUTES;
        }
        if (duration.toSeconds() > 0L) {
            return TimeUnit.SECONDS;
        }
        if (duration.toMillis() > 0L) {
            return TimeUnit.MILLISECONDS;
        }
        if (duration.toNanos() > 1000L) {
            return TimeUnit.MICROSECONDS;
        }
        return TimeUnit.NANOSECONDS;
    }

    private static String unitToString(TimeUnit unit) {
        return unit.name().toLowerCase(Locale.ROOT);
    }
}

