package corgitaco.corgilib.shadow.blue.endless.jankson.api;

import java.lang.Character.UnicodeBlock;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Escaper {
   private static final Set<UnicodeBlock> DEFAULT_BLOCKS;

   private Escaper() {
   }

   public static String escapeString(String s) {
      return escapeString(s, '"', DEFAULT_BLOCKS);
   }

   public static String escapeString(String s, char quoteChar, Set<UnicodeBlock> unquotedBlocks) {
      StringBuilder result = new StringBuilder(s.length());

      for (int i = 0; i < s.length(); i++) {
         char ch = s.charAt(i);
         switch (ch) {
            case '\b':
               result.append("\\b");
               break;
            case '\t':
               result.append("\\t");
               break;
            case '\n':
               result.append("\\n");
               break;
            case '\f':
               result.append("\\f");
               break;
            case '\r':
               result.append("\\r");
               break;
            case '"':
               if (quoteChar == ch) {
                  result.append("\\\"");
               } else {
                  result.append(ch);
               }
               break;
            case '\'':
               if (quoteChar == ch) {
                  result.append("\\'");
               } else {
                  result.append(ch);
               }
               break;
            case '\\':
               result.append("\\\\");
               break;
            default:
               if (Character.isBmpCodePoint(ch)) {
                  UnicodeBlock block = UnicodeBlock.of(ch);
                  if (ch != '\uffff' && !Character.isISOControl(ch) && block != null && unquotedBlocks.contains(block)) {
                     result.append(ch);
                  } else {
                     result.append(unicodeEscape(ch));
                  }
               } else {
                  char upper = s.charAt(++i);
                  int codePoint = Character.toCodePoint(ch, upper);
                  result.append(unicodeEscape(codePoint));
               }
         }
      }

      return result.toString();
   }

   private static String unicodeEscape(int codePoint) {
      String codeString = "" + Integer.toHexString(codePoint);

      while (codeString.length() < 4) {
         codeString = "0" + codeString;
      }

      return "\\u" + codeString;
   }

   static {
      HashSet<UnicodeBlock> tmp = new HashSet<>();
      tmp.add(UnicodeBlock.BASIC_LATIN);
      DEFAULT_BLOCKS = Collections.unmodifiableSet(tmp);
   }
}
