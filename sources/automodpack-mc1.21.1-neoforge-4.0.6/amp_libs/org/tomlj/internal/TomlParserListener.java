package amp_libs.org.tomlj.internal;

import amp_libs.org.antlr.v4.runtime.tree.ParseTreeListener;

public interface TomlParserListener extends ParseTreeListener {
   void enterToml(TomlParser.TomlContext var1);

   void exitToml(TomlParser.TomlContext var1);

   void enterExpression(TomlParser.ExpressionContext var1);

   void exitExpression(TomlParser.ExpressionContext var1);

   void enterTomlKey(TomlParser.TomlKeyContext var1);

   void exitTomlKey(TomlParser.TomlKeyContext var1);

   void enterKeyval(TomlParser.KeyvalContext var1);

   void exitKeyval(TomlParser.KeyvalContext var1);

   void enterKey(TomlParser.KeyContext var1);

   void exitKey(TomlParser.KeyContext var1);

   void enterSimpleKey(TomlParser.SimpleKeyContext var1);

   void exitSimpleKey(TomlParser.SimpleKeyContext var1);

   void enterUnquotedKey(TomlParser.UnquotedKeyContext var1);

   void exitUnquotedKey(TomlParser.UnquotedKeyContext var1);

   void enterQuotedKey(TomlParser.QuotedKeyContext var1);

   void exitQuotedKey(TomlParser.QuotedKeyContext var1);

   void enterVal(TomlParser.ValContext var1);

   void exitVal(TomlParser.ValContext var1);

   void enterString(TomlParser.StringContext var1);

   void exitString(TomlParser.StringContext var1);

   void enterBasicString(TomlParser.BasicStringContext var1);

   void exitBasicString(TomlParser.BasicStringContext var1);

   void enterBasicChar(TomlParser.BasicCharContext var1);

   void exitBasicChar(TomlParser.BasicCharContext var1);

   void enterBasicUnescaped(TomlParser.BasicUnescapedContext var1);

   void exitBasicUnescaped(TomlParser.BasicUnescapedContext var1);

   void enterEscaped(TomlParser.EscapedContext var1);

   void exitEscaped(TomlParser.EscapedContext var1);

   void enterMlBasicString(TomlParser.MlBasicStringContext var1);

   void exitMlBasicString(TomlParser.MlBasicStringContext var1);

   void enterMlBasicChar(TomlParser.MlBasicCharContext var1);

   void exitMlBasicChar(TomlParser.MlBasicCharContext var1);

   void enterMlBasicUnescaped(TomlParser.MlBasicUnescapedContext var1);

   void exitMlBasicUnescaped(TomlParser.MlBasicUnescapedContext var1);

   void enterLiteralString(TomlParser.LiteralStringContext var1);

   void exitLiteralString(TomlParser.LiteralStringContext var1);

   void enterLiteralBody(TomlParser.LiteralBodyContext var1);

   void exitLiteralBody(TomlParser.LiteralBodyContext var1);

   void enterMlLiteralString(TomlParser.MlLiteralStringContext var1);

   void exitMlLiteralString(TomlParser.MlLiteralStringContext var1);

   void enterMlLiteralBody(TomlParser.MlLiteralBodyContext var1);

   void exitMlLiteralBody(TomlParser.MlLiteralBodyContext var1);

   void enterInteger(TomlParser.IntegerContext var1);

   void exitInteger(TomlParser.IntegerContext var1);

   void enterDecInt(TomlParser.DecIntContext var1);

   void exitDecInt(TomlParser.DecIntContext var1);

   void enterHexInt(TomlParser.HexIntContext var1);

   void exitHexInt(TomlParser.HexIntContext var1);

   void enterOctInt(TomlParser.OctIntContext var1);

   void exitOctInt(TomlParser.OctIntContext var1);

   void enterBinInt(TomlParser.BinIntContext var1);

   void exitBinInt(TomlParser.BinIntContext var1);

   void enterFloatValue(TomlParser.FloatValueContext var1);

   void exitFloatValue(TomlParser.FloatValueContext var1);

   void enterRegularFloat(TomlParser.RegularFloatContext var1);

   void exitRegularFloat(TomlParser.RegularFloatContext var1);

   void enterRegularFloatInf(TomlParser.RegularFloatInfContext var1);

   void exitRegularFloatInf(TomlParser.RegularFloatInfContext var1);

   void enterRegularFloatNaN(TomlParser.RegularFloatNaNContext var1);

   void exitRegularFloatNaN(TomlParser.RegularFloatNaNContext var1);

   void enterBooleanValue(TomlParser.BooleanValueContext var1);

   void exitBooleanValue(TomlParser.BooleanValueContext var1);

   void enterTrueBool(TomlParser.TrueBoolContext var1);

   void exitTrueBool(TomlParser.TrueBoolContext var1);

   void enterFalseBool(TomlParser.FalseBoolContext var1);

   void exitFalseBool(TomlParser.FalseBoolContext var1);

   void enterDateTime(TomlParser.DateTimeContext var1);

   void exitDateTime(TomlParser.DateTimeContext var1);

   void enterOffsetDateTime(TomlParser.OffsetDateTimeContext var1);

   void exitOffsetDateTime(TomlParser.OffsetDateTimeContext var1);

   void enterLocalDateTime(TomlParser.LocalDateTimeContext var1);

   void exitLocalDateTime(TomlParser.LocalDateTimeContext var1);

   void enterLocalDate(TomlParser.LocalDateContext var1);

   void exitLocalDate(TomlParser.LocalDateContext var1);

   void enterLocalTime(TomlParser.LocalTimeContext var1);

   void exitLocalTime(TomlParser.LocalTimeContext var1);

   void enterDate(TomlParser.DateContext var1);

   void exitDate(TomlParser.DateContext var1);

   void enterTime(TomlParser.TimeContext var1);

   void exitTime(TomlParser.TimeContext var1);

   void enterTimeOffset(TomlParser.TimeOffsetContext var1);

   void exitTimeOffset(TomlParser.TimeOffsetContext var1);

   void enterHourOffset(TomlParser.HourOffsetContext var1);

   void exitHourOffset(TomlParser.HourOffsetContext var1);

   void enterMinuteOffset(TomlParser.MinuteOffsetContext var1);

   void exitMinuteOffset(TomlParser.MinuteOffsetContext var1);

   void enterSecondFraction(TomlParser.SecondFractionContext var1);

   void exitSecondFraction(TomlParser.SecondFractionContext var1);

   void enterYear(TomlParser.YearContext var1);

   void exitYear(TomlParser.YearContext var1);

   void enterMonth(TomlParser.MonthContext var1);

   void exitMonth(TomlParser.MonthContext var1);

   void enterDay(TomlParser.DayContext var1);

   void exitDay(TomlParser.DayContext var1);

   void enterHour(TomlParser.HourContext var1);

   void exitHour(TomlParser.HourContext var1);

   void enterMinute(TomlParser.MinuteContext var1);

   void exitMinute(TomlParser.MinuteContext var1);

   void enterSecond(TomlParser.SecondContext var1);

   void exitSecond(TomlParser.SecondContext var1);

   void enterArray(TomlParser.ArrayContext var1);

   void exitArray(TomlParser.ArrayContext var1);

   void enterArrayValues(TomlParser.ArrayValuesContext var1);

   void exitArrayValues(TomlParser.ArrayValuesContext var1);

   void enterArrayValue(TomlParser.ArrayValueContext var1);

   void exitArrayValue(TomlParser.ArrayValueContext var1);

   void enterTable(TomlParser.TableContext var1);

   void exitTable(TomlParser.TableContext var1);

   void enterStandardTable(TomlParser.StandardTableContext var1);

   void exitStandardTable(TomlParser.StandardTableContext var1);

   void enterInlineTable(TomlParser.InlineTableContext var1);

   void exitInlineTable(TomlParser.InlineTableContext var1);

   void enterInlineTableValues(TomlParser.InlineTableValuesContext var1);

   void exitInlineTableValues(TomlParser.InlineTableValuesContext var1);

   void enterArrayTable(TomlParser.ArrayTableContext var1);

   void exitArrayTable(TomlParser.ArrayTableContext var1);
}
