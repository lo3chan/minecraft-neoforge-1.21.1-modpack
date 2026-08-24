package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.ParserRuleContext;
import amp_libs.org.antlr.v4.runtime.tree.ErrorNode;
import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.time.DateTimeException;
import java.time.ZoneOffset;

final class ZoneOffsetVisitor extends TomlParserBaseVisitor<ZoneOffset> {
   private int hours = 0;
   private int minutes = 0;

   public ZoneOffset visitHourOffset(TomlParser.HourOffsetContext ctx) {
      int hours;
      try {
         hours = Integer.parseInt(ctx.getText());
      } catch (NumberFormatException var4) {
         throw new TomlParseError("Invalid zone offset", new TomlPosition(ctx), var4);
      }

      if (hours >= -18 && hours <= 18) {
         ZoneOffset offset = toZoneOffset(hours, this.minutes, ctx, 0);
         this.hours = hours;
         return offset;
      } else {
         throw new TomlParseError("Invalid zone offset hours (valid range -18..+18)", new TomlPosition(ctx));
      }
   }

   public ZoneOffset visitMinuteOffset(TomlParser.MinuteOffsetContext ctx) {
      int minutes;
      try {
         minutes = Integer.parseInt(ctx.getText());
      } catch (NumberFormatException var4) {
         throw new TomlParseError("Invalid zone offset", new TomlPosition(ctx), var4);
      }

      if (minutes >= 0 && minutes <= 59) {
         ZoneOffset offset = toZoneOffset(this.hours, minutes, ctx, -4);
         this.minutes = minutes;
         return offset;
      } else {
         throw new TomlParseError("Invalid zone offset minutes (valid range 0..59)", new TomlPosition(ctx));
      }
   }

   private static ZoneOffset toZoneOffset(int hours, int minutes, ParserRuleContext ctx, int offset) {
      try {
         return ZoneOffset.ofHoursMinutes(hours, hours < 0 ? -minutes : minutes);
      } catch (DateTimeException var5) {
         throw new TomlParseError("Invalid zone offset (valid range -18:00..+18:00)", new TomlPosition(ctx, offset), var5);
      }
   }

   public ZoneOffset visitErrorNode(ErrorNode node) {
      return null;
   }

   protected ZoneOffset aggregateResult(ZoneOffset aggregate, ZoneOffset nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected ZoneOffset defaultResult() {
      return ZoneOffset.ofHoursMinutes(this.hours, this.hours < 0 ? -this.minutes : this.minutes);
   }
}
