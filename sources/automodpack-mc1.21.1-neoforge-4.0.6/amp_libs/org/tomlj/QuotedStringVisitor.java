package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.ParserRuleContext;
import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;

final class QuotedStringVisitor extends TomlParserBaseVisitor<StringBuilder> {
   private final TomlVersion version;
   private final StringBuilder builder = new StringBuilder();

   public QuotedStringVisitor(TomlVersion version) {
      this.version = version;
   }

   public StringBuilder visitLiteralBody(TomlParser.LiteralBodyContext ctx) {
      return this.appendText(ctx.getText(), ctx);
   }

   public StringBuilder visitMlLiteralBody(TomlParser.MlLiteralBodyContext ctx) {
      return this.appendText(ctx.getText(), ctx);
   }

   public StringBuilder visitBasicUnescaped(TomlParser.BasicUnescapedContext ctx) {
      return this.appendText(ctx.getText(), ctx);
   }

   public StringBuilder visitMlBasicUnescaped(TomlParser.MlBasicUnescapedContext ctx) {
      return this.appendText(ctx.getText(), ctx);
   }

   private StringBuilder appendText(String text, ParserRuleContext ctx) {
      if (!this.version.after(TomlVersion.V0_5_0) && text.indexOf(9) != -1) {
         throw new TomlParseError("Use \\t to represent a tab in a string (TOML versions before 1.0.0)", new TomlPosition(ctx));
      } else {
         return this.builder.append(text);
      }
   }

   public StringBuilder visitEscaped(TomlParser.EscapedContext ctx) {
      String text = ctx.getText();
      if (text.isEmpty()) {
         return this.builder;
      } else {
         assert text.charAt(0) == '\\';

         if (text.length() == 1) {
            return this.builder.append('\\');
         } else {
            switch (text.charAt(1)) {
               case '"':
                  return this.builder.append('"');
               case '\'':
                  return this.builder.append('\'');
               case 'U':
                  assert text.length() == 10;

                  return this.builder.append(this.convertUnicodeEscape(text.substring(2), ctx));
               case '\\':
                  return this.builder.append('\\');
               case 'b':
                  return this.builder.append('\b');
               case 'f':
                  return this.builder.append('\f');
               case 'n':
                  return this.builder.append('\n');
               case 'r':
                  return this.builder.append('\r');
               case 't':
                  return this.builder.append('\t');
               case 'u':
                  assert text.length() == 6;

                  return this.builder.append(this.convertUnicodeEscape(text.substring(2), ctx));
               default:
                  throw new TomlParseError("Invalid escape sequence '" + text + "'", new TomlPosition(ctx));
            }
         }
      }
   }

   private char[] convertUnicodeEscape(String hexChars, TomlParser.EscapedContext ctx) {
      try {
         char[] characters = Character.toChars(Integer.parseInt(hexChars, 16));
         if (characters.length == 1 && Character.isSurrogate(characters[0])) {
            throw new IllegalArgumentException();
         } else {
            return characters;
         }
      } catch (IllegalArgumentException var4) {
         throw new TomlParseError("Invalid unicode escape sequence", new TomlPosition(ctx));
      }
   }

   protected StringBuilder aggregateResult(StringBuilder aggregate, StringBuilder nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected StringBuilder defaultResult() {
      return this.builder;
   }
}
