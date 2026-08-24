package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.tree.ErrorNode;
import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.time.LocalTime;

final class LocalTimeVisitor extends TomlParserBaseVisitor<LocalTime> {
   private LocalTime time = LocalTime.MIN;

   public LocalTime visitHour(TomlParser.HourContext ctx) {
      String text = ctx.getText();
      if (text.length() != 2) {
         throw new TomlParseError("Invalid hour (valid range 00..23)", new TomlPosition(ctx));
      } else {
         int hour;
         try {
            hour = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid hour", new TomlPosition(ctx), var5);
         }

         if (hour >= 0 && hour <= 23) {
            this.time = this.time.withHour(hour);
            return this.time;
         } else {
            throw new TomlParseError("Invalid hour (valid range 00..23)", new TomlPosition(ctx));
         }
      }
   }

   public LocalTime visitMinute(TomlParser.MinuteContext ctx) {
      String text = ctx.getText();
      if (text.length() != 2) {
         throw new TomlParseError("Invalid minutes (valid range 00..59)", new TomlPosition(ctx));
      } else {
         int minute;
         try {
            minute = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid minutes", new TomlPosition(ctx), var5);
         }

         if (minute >= 0 && minute <= 59) {
            this.time = this.time.withMinute(minute);
            return this.time;
         } else {
            throw new TomlParseError("Invalid minutes (valid range 00..59)", new TomlPosition(ctx));
         }
      }
   }

   public LocalTime visitSecond(TomlParser.SecondContext ctx) {
      String text = ctx.getText();
      if (text.length() != 2) {
         throw new TomlParseError("Invalid seconds (valid range 00..59)", new TomlPosition(ctx));
      } else {
         int second;
         try {
            second = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid seconds", new TomlPosition(ctx), var5);
         }

         if (second >= 0 && second <= 59) {
            this.time = this.time.withSecond(second);
            return this.time;
         } else {
            throw new TomlParseError("Invalid seconds (valid range 00..59)", new TomlPosition(ctx));
         }
      }
   }

   public LocalTime visitSecondFraction(TomlParser.SecondFractionContext ctx) {
      String text = ctx.getText();
      if (!text.isEmpty() && text.length() <= 9) {
         if (text.length() < 9) {
            text = text + "000000000".substring(text.length());
         }

         int nano;
         try {
            nano = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid nanoseconds", new TomlPosition(ctx), var5);
         }

         this.time = this.time.withNano(nano);
         return this.time;
      } else {
         throw new TomlParseError("Invalid nanoseconds (valid range 0..999999999)", new TomlPosition(ctx));
      }
   }

   public LocalTime visitErrorNode(ErrorNode node) {
      return null;
   }

   protected LocalTime aggregateResult(LocalTime aggregate, LocalTime nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected LocalTime defaultResult() {
      return this.time;
   }
}
