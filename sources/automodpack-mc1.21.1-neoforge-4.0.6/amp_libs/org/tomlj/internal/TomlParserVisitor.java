package amp_libs.org.tomlj.internal;

import amp_libs.org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface TomlParserVisitor<T> extends ParseTreeVisitor<T> {
   T visitToml(TomlParser.TomlContext var1);

   T visitExpression(TomlParser.ExpressionContext var1);

   T visitTomlKey(TomlParser.TomlKeyContext var1);

   T visitKeyval(TomlParser.KeyvalContext var1);

   T visitKey(TomlParser.KeyContext var1);

   T visitSimpleKey(TomlParser.SimpleKeyContext var1);

   T visitUnquotedKey(TomlParser.UnquotedKeyContext var1);

   T visitQuotedKey(TomlParser.QuotedKeyContext var1);

   T visitVal(TomlParser.ValContext var1);

   T visitString(TomlParser.StringContext var1);

   T visitBasicString(TomlParser.BasicStringContext var1);

   T visitBasicChar(TomlParser.BasicCharContext var1);

   T visitBasicUnescaped(TomlParser.BasicUnescapedContext var1);

   T visitEscaped(TomlParser.EscapedContext var1);

   T visitMlBasicString(TomlParser.MlBasicStringContext var1);

   T visitMlBasicChar(TomlParser.MlBasicCharContext var1);

   T visitMlBasicUnescaped(TomlParser.MlBasicUnescapedContext var1);

   T visitLiteralString(TomlParser.LiteralStringContext var1);

   T visitLiteralBody(TomlParser.LiteralBodyContext var1);

   T visitMlLiteralString(TomlParser.MlLiteralStringContext var1);

   T visitMlLiteralBody(TomlParser.MlLiteralBodyContext var1);

   T visitInteger(TomlParser.IntegerContext var1);

   T visitDecInt(TomlParser.DecIntContext var1);

   T visitHexInt(TomlParser.HexIntContext var1);

   T visitOctInt(TomlParser.OctIntContext var1);

   T visitBinInt(TomlParser.BinIntContext var1);

   T visitFloatValue(TomlParser.FloatValueContext var1);

   T visitRegularFloat(TomlParser.RegularFloatContext var1);

   T visitRegularFloatInf(TomlParser.RegularFloatInfContext var1);

   T visitRegularFloatNaN(TomlParser.RegularFloatNaNContext var1);

   T visitBooleanValue(TomlParser.BooleanValueContext var1);

   T visitTrueBool(TomlParser.TrueBoolContext var1);

   T visitFalseBool(TomlParser.FalseBoolContext var1);

   T visitDateTime(TomlParser.DateTimeContext var1);

   T visitOffsetDateTime(TomlParser.OffsetDateTimeContext var1);

   T visitLocalDateTime(TomlParser.LocalDateTimeContext var1);

   T visitLocalDate(TomlParser.LocalDateContext var1);

   T visitLocalTime(TomlParser.LocalTimeContext var1);

   T visitDate(TomlParser.DateContext var1);

   T visitTime(TomlParser.TimeContext var1);

   T visitTimeOffset(TomlParser.TimeOffsetContext var1);

   T visitHourOffset(TomlParser.HourOffsetContext var1);

   T visitMinuteOffset(TomlParser.MinuteOffsetContext var1);

   T visitSecondFraction(TomlParser.SecondFractionContext var1);

   T visitYear(TomlParser.YearContext var1);

   T visitMonth(TomlParser.MonthContext var1);

   T visitDay(TomlParser.DayContext var1);

   T visitHour(TomlParser.HourContext var1);

   T visitMinute(TomlParser.MinuteContext var1);

   T visitSecond(TomlParser.SecondContext var1);

   T visitArray(TomlParser.ArrayContext var1);

   T visitArrayValues(TomlParser.ArrayValuesContext var1);

   T visitArrayValue(TomlParser.ArrayValueContext var1);

   T visitTable(TomlParser.TableContext var1);

   T visitStandardTable(TomlParser.StandardTableContext var1);

   T visitInlineTable(TomlParser.InlineTableContext var1);

   T visitInlineTableValues(TomlParser.InlineTableValuesContext var1);

   T visitArrayTable(TomlParser.ArrayTableContext var1);
}
