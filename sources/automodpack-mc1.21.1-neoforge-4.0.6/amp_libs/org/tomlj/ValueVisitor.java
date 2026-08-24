package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.ParserRuleContext;
import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.regex.Pattern;

final class ValueVisitor extends TomlParserBaseVisitor<Object> {
   private static final Pattern zeroFloat = Pattern.compile("[+-]?0+(\\.[+-]?0*)?([eE].*)?");
   private final TomlVersion version;

   ValueVisitor(TomlVersion version) {
      this.version = version;
   }

   @Override
   public Object visitString(TomlParser.StringContext ctx) {
      return ctx.accept(new QuotedStringVisitor(this.version)).toString();
   }

   @Override
   public Object visitDecInt(TomlParser.DecIntContext ctx) {
      return this.toLong(ctx.getText().replaceAll("_", ""), 10, ctx);
   }

   @Override
   public Object visitHexInt(TomlParser.HexIntContext ctx) {
      return this.toLong(ctx.getText().substring(2).replaceAll("_", ""), 16, ctx);
   }

   @Override
   public Object visitOctInt(TomlParser.OctIntContext ctx) {
      return this.toLong(ctx.getText().substring(2).replaceAll("_", ""), 8, ctx);
   }

   @Override
   public Object visitBinInt(TomlParser.BinIntContext ctx) {
      return this.toLong(ctx.getText().substring(2).replaceAll("_", ""), 2, ctx);
   }

   private Long toLong(String s, int radix, ParserRuleContext ctx) {
      try {
         return Long.valueOf(s, radix);
      } catch (NumberFormatException var5) {
         throw new TomlParseError("Integer is too large", new TomlPosition(ctx));
      }
   }

   @Override
   public Object visitRegularFloat(TomlParser.RegularFloatContext ctx) {
      return this.toDouble(ctx.getText().replaceAll("_", ""), ctx);
   }

   @Override
   public Object visitRegularFloatInf(TomlParser.RegularFloatInfContext ctx) {
      return ctx.getText().startsWith("-") ? -1.0 / 0.0 : 1.0 / 0.0;
   }

   @Override
   public Object visitRegularFloatNaN(TomlParser.RegularFloatNaNContext ctx) {
      return 0.0 / 0.0;
   }

   private Double toDouble(String s, ParserRuleContext ctx) {
      try {
         double value = Double.parseDouble(s);
         if (value == 1.0 / 0.0 || value == -1.0 / 0.0) {
            throw new TomlParseError("Float is too large", new TomlPosition(ctx));
         } else if (value == 0.0 && !zeroFloat.matcher(s).matches()) {
            throw new TomlParseError("Float is too small", new TomlPosition(ctx));
         } else {
            return value;
         }
      } catch (NumberFormatException var5) {
         throw new TomlParseError("Invalid floating point number: " + var5.getMessage(), new TomlPosition(ctx));
      }
   }

   @Override
   public Object visitTrueBool(TomlParser.TrueBoolContext ctx) {
      return Boolean.TRUE;
   }

   @Override
   public Object visitFalseBool(TomlParser.FalseBoolContext ctx) {
      return Boolean.FALSE;
   }

   @Override
   public Object visitOffsetDateTime(TomlParser.OffsetDateTimeContext ctx) {
      LocalDate date = ctx.date().accept(new LocalDateVisitor());
      LocalTime time = ctx.time().accept(new LocalTimeVisitor());
      ZoneOffset offset = ctx.timeOffset().accept(new ZoneOffsetVisitor());
      return OffsetDateTime.of(date, time, offset);
   }

   @Override
   public Object visitLocalDateTime(TomlParser.LocalDateTimeContext ctx) {
      LocalDate date = ctx.date().accept(new LocalDateVisitor());
      LocalTime time = ctx.time().accept(new LocalTimeVisitor());
      return LocalDateTime.of(date, time);
   }

   @Override
   public Object visitLocalDate(TomlParser.LocalDateContext ctx) {
      return ctx.date().accept(new LocalDateVisitor());
   }

   @Override
   public Object visitLocalTime(TomlParser.LocalTimeContext ctx) {
      return ctx.time().accept(new LocalTimeVisitor());
   }

   @Override
   public Object visitArray(TomlParser.ArrayContext ctx) {
      TomlParser.ArrayValuesContext valuesContext = ctx.arrayValues();
      return valuesContext == null ? EmptyTomlArray.EMPTY_ARRAY : valuesContext.accept(new ArrayVisitor(this.version));
   }

   @Override
   public Object visitInlineTable(TomlParser.InlineTableContext ctx) {
      TomlParser.InlineTableValuesContext valuesContext = ctx.inlineTableValues();
      if (valuesContext == null) {
         return EmptyTomlTable.EMPTY_TABLE;
      } else {
         InlineTableVisitor visitor = new InlineTableVisitor(this.version, new TomlPosition(ctx));
         MutableTomlTable result = valuesContext.accept(visitor);
         visitor.defineOpenTables();
         return result;
      }
   }

   @Override
   protected Object aggregateResult(Object aggregate, Object nextResult) {
      return nextResult;
   }

   @Override
   protected Object defaultResult() {
      return null;
   }
}
