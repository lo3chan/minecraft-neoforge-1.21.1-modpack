package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.tree.ErrorNode;
import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.time.DateTimeException;
import java.time.LocalDate;

final class LocalDateVisitor extends TomlParserBaseVisitor<LocalDate> {
   private static final LocalDate INITIAL = LocalDate.parse("1900-01-01");
   private LocalDate date = INITIAL;

   public LocalDate visitYear(TomlParser.YearContext ctx) {
      String text = ctx.getText();
      if (text.length() != 4) {
         throw new TomlParseError("Invalid year (valid range 0000..9999)", new TomlPosition(ctx));
      } else {
         int year;
         try {
            year = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid year", new TomlPosition(ctx), var5);
         }

         this.date = this.date.withYear(year);
         return this.date;
      }
   }

   public LocalDate visitMonth(TomlParser.MonthContext ctx) {
      String text = ctx.getText();
      if (text.length() != 2) {
         throw new TomlParseError("Invalid month (valid range 01..12)", new TomlPosition(ctx));
      } else {
         int month;
         try {
            month = Integer.parseInt(text);
         } catch (NumberFormatException var5) {
            throw new TomlParseError("Invalid month", new TomlPosition(ctx), var5);
         }

         if (month >= 1 && month <= 12) {
            this.date = this.date.withMonth(month);
            return this.date;
         } else {
            throw new TomlParseError("Invalid month (valid range 01..12)", new TomlPosition(ctx));
         }
      }
   }

   public LocalDate visitDay(TomlParser.DayContext ctx) {
      String text = ctx.getText();
      if (text.length() != 2) {
         throw new TomlParseError("Invalid day (valid range 01..28/31)", new TomlPosition(ctx));
      } else {
         int day;
         try {
            day = Integer.parseInt(text);
         } catch (NumberFormatException var6) {
            throw new TomlParseError("Invalid day", new TomlPosition(ctx), var6);
         }

         if (day >= 1 && day <= 31) {
            try {
               this.date = this.date.withDayOfMonth(day);
            } catch (DateTimeException var5) {
               throw new TomlParseError(var5.getMessage(), new TomlPosition(ctx), var5);
            }

            return this.date;
         } else {
            throw new TomlParseError("Invalid day (valid range 01..28/31)", new TomlPosition(ctx));
         }
      }
   }

   public LocalDate visitErrorNode(ErrorNode node) {
      return null;
   }

   protected LocalDate aggregateResult(LocalDate aggregate, LocalDate nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected LocalDate defaultResult() {
      return this.date;
   }
}
