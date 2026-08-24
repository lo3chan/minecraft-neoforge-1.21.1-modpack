package amp_libs.org.tomlj;

import amp_libs.org.tomlj.internal.TomlLexer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Stream;

enum TokenName {
   LOWER_ALPHA("a-z", 13),
   UPPER_ALPHA("A-Z", 13),
   DIGITS("0-9", 13),
   ARRAY_END("]", 28, 10),
   ARRAY_TABLE_END("]]", 12),
   INLINE_TABLE_END("}", 37),
   DOT(".", 5),
   DASH("-", 31),
   PLUS("+", 32),
   COLON(":", 33),
   EQUALS("=", 6),
   COMMA("a comma", 4),
   Z("Z", 34),
   APOSTROPHE("'", 8),
   QUOTATION_MARK("\"", 7),
   TRIPLE_APOSTROPHE("'''", 2),
   TRIPLE_QUOTATION_MARK("\"\"\"", 1),
   CHARACTER("a character", 30, 3),
   NUMBER("a number", 18, 21, 20, 19, 22, 23, 24),
   BOOLEAN("a boolean", 25, 26),
   DATETIME("a date/time", 36),
   TIME("a time", 35),
   ARRAY("an array", 27),
   INLINE_TABLE("a table", 29),
   TABLE("a table key", 9, 11),
   NEWLINE("a newline", 16),
   EOF("end-of-input", -1),
   NULL("NULL", 0, 14, 15, 17);

   private final String displayName;
   private final BitSet tokenTypes;

   private TokenName(String displayName, int... tokenTypes) {
      this.displayName = displayName;
      this.tokenTypes = new BitSet(TomlLexer.VOCABULARY.getMaxTokenType() + 1);

      for (int type : tokenTypes) {
         this.tokenTypes.set(type + 1);
      }
   }

   static Stream<TokenName> namesForToken(int tokenType) {
      return Arrays.stream(values()).filter(n -> n.tokenTypes.get(tokenType + 1));
   }

   public String displayName() {
      return this.displayName;
   }
}
