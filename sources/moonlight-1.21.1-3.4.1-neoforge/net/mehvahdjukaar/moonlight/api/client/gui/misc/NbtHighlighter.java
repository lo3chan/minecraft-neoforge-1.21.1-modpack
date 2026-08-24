package net.mehvahdjukaar.moonlight.api.client.gui.misc;

import java.util.Arrays;

public final class NbtHighlighter implements SyntaxHighlighter {
   public static final NbtHighlighter INSTANCE = new NbtHighlighter();
   private static final int KEY = ConfigGuiColors.SYNTAX_KEY;
   private static final int STRING = ConfigGuiColors.SYNTAX_STRING;
   private static final int NUMBER = ConfigGuiColors.SYNTAX_NUMBER;
   private static final int TYPE = ConfigGuiColors.SYNTAX_TYPE;
   private static final int KEYWORD = ConfigGuiColors.SYNTAX_KEYWORD;
   private static final int PUNCTUATION = ConfigGuiColors.SYNTAX_PUNCTUATION;
   private static final int DEFAULT = ConfigGuiColors.SYNTAX_DEFAULT;

   @Override
   public int[] colors(String line) {
      int n = line.length();
      int[] colors = new int[n];
      int i = 0;

      while (i < n) {
         char c = line.charAt(i);
         if (c != '"' && c != '\'') {
            if (c != '-' && c != '+' ? isDigit(c) : i + 1 < n && isDigit(line.charAt(i + 1))) {
               int start = i++;

               while (i < n && isNumberChar(line.charAt(i))) {
                  i++;
               }

               Arrays.fill(colors, start, i, NUMBER);
               if (i < n && isTypeSuffix(line.charAt(i))) {
                  colors[i++] = TYPE;
               }
            } else if (Character.isLetter(c) || c == '_') {
               int start = i++;

               while (i < n && isWordChar(line.charAt(i))) {
                  i++;
               }

               String word = line.substring(start, i);
               if (!word.equals("true") && !word.equals("false")) {
                  if (word.length() == 1 && charAheadIs(line, i, ';')) {
                     colors[start] = TYPE;
                  } else {
                     Arrays.fill(colors, start, i, isKeyAhead(line, i) ? KEY : STRING);
                  }
               } else {
                  Arrays.fill(colors, start, i, KEYWORD);
               }
            } else if (c != '{' && c != '}' && c != '[' && c != ']' && c != ':' && c != ',' && c != ';') {
               colors[i++] = DEFAULT;
            } else {
               colors[i++] = PUNCTUATION;
            }
         } else {
            char quote = c;
            int start = i++;

            while (true) {
               if (i < n) {
                  char d = line.charAt(i++);
                  if (d == '\\' && i < n) {
                     i++;
                     continue;
                  }

                  if (d != quote) {
                     continue;
                  }
               }

               Arrays.fill(colors, start, i, isKeyAhead(line, i) ? KEY : STRING);
               break;
            }
         }
      }

      return colors;
   }

   private static boolean isKeyAhead(String line, int from) {
      return charAheadIs(line, from, ':');
   }

   private static boolean charAheadIs(String line, int from, char target) {
      int j = from;

      while (j < line.length() && line.charAt(j) == ' ') {
         j++;
      }

      return j < line.length() && line.charAt(j) == target;
   }

   private static boolean isDigit(char c) {
      return c >= '0' && c <= '9';
   }

   private static boolean isNumberChar(char c) {
      return isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-';
   }

   private static boolean isTypeSuffix(char c) {
      return "bslfdBSLFD".indexOf(c) >= 0;
   }

   private static boolean isWordChar(char c) {
      return Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '+' || c == '-';
   }
}
