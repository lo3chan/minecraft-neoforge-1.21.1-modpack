package dh_sqlite.date;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface DatePrinter {
   String format(long l);

   String format(Date date);

   String format(Calendar calendar);

   StringBuffer format(long l, StringBuffer stringBuffer);

   StringBuffer format(Date date, StringBuffer stringBuffer);

   StringBuffer format(Calendar calendar, StringBuffer stringBuffer);

   String getPattern();

   TimeZone getTimeZone();

   Locale getLocale();

   StringBuffer format(Object object, StringBuffer stringBuffer, FieldPosition fieldPosition);
}
