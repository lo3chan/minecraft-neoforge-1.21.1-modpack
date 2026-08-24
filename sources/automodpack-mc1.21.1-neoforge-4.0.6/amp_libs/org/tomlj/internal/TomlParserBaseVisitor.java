package amp_libs.org.tomlj.internal;

import amp_libs.org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class TomlParserBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements TomlParserVisitor<T> {
   @Override
   public T visitToml(TomlParser.TomlContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitExpression(TomlParser.ExpressionContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitTomlKey(TomlParser.TomlKeyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitKeyval(TomlParser.KeyvalContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitKey(TomlParser.KeyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitSimpleKey(TomlParser.SimpleKeyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitUnquotedKey(TomlParser.UnquotedKeyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitQuotedKey(TomlParser.QuotedKeyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitVal(TomlParser.ValContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitString(TomlParser.StringContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitBasicString(TomlParser.BasicStringContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitBasicChar(TomlParser.BasicCharContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitBasicUnescaped(TomlParser.BasicUnescapedContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitEscaped(TomlParser.EscapedContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMlBasicString(TomlParser.MlBasicStringContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMlBasicChar(TomlParser.MlBasicCharContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMlBasicUnescaped(TomlParser.MlBasicUnescapedContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitLiteralString(TomlParser.LiteralStringContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitLiteralBody(TomlParser.LiteralBodyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMlLiteralString(TomlParser.MlLiteralStringContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMlLiteralBody(TomlParser.MlLiteralBodyContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitInteger(TomlParser.IntegerContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitDecInt(TomlParser.DecIntContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitHexInt(TomlParser.HexIntContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitOctInt(TomlParser.OctIntContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitBinInt(TomlParser.BinIntContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitFloatValue(TomlParser.FloatValueContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitRegularFloat(TomlParser.RegularFloatContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitRegularFloatInf(TomlParser.RegularFloatInfContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitRegularFloatNaN(TomlParser.RegularFloatNaNContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitBooleanValue(TomlParser.BooleanValueContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitTrueBool(TomlParser.TrueBoolContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitFalseBool(TomlParser.FalseBoolContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitDateTime(TomlParser.DateTimeContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitOffsetDateTime(TomlParser.OffsetDateTimeContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitLocalDateTime(TomlParser.LocalDateTimeContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitLocalDate(TomlParser.LocalDateContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitLocalTime(TomlParser.LocalTimeContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitDate(TomlParser.DateContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitTime(TomlParser.TimeContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitTimeOffset(TomlParser.TimeOffsetContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitHourOffset(TomlParser.HourOffsetContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMinuteOffset(TomlParser.MinuteOffsetContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitSecondFraction(TomlParser.SecondFractionContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitYear(TomlParser.YearContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMonth(TomlParser.MonthContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitDay(TomlParser.DayContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitHour(TomlParser.HourContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitMinute(TomlParser.MinuteContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitSecond(TomlParser.SecondContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitArray(TomlParser.ArrayContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitArrayValues(TomlParser.ArrayValuesContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitArrayValue(TomlParser.ArrayValueContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitTable(TomlParser.TableContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitStandardTable(TomlParser.StandardTableContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitInlineTable(TomlParser.InlineTableContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitInlineTableValues(TomlParser.InlineTableValuesContext ctx) {
      return this.visitChildren(ctx);
   }

   @Override
   public T visitArrayTable(TomlParser.ArrayTableContext ctx) {
      return this.visitChildren(ctx);
   }
}
