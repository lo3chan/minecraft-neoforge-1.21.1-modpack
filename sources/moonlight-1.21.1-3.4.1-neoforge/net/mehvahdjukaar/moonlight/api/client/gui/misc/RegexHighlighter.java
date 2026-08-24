package net.mehvahdjukaar.moonlight.api.client.gui.misc;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class RegexHighlighter implements SyntaxHighlighter {
   public static final RegexHighlighter INSTANCE = new RegexHighlighter();
   private static final int LITERAL = ConfigGuiColors.SYNTAX_DEFAULT;
   private static final int ESCAPE = ConfigGuiColors.SYNTAX_ESCAPE;
   private static final int CHAR_CLASS = ConfigGuiColors.SYNTAX_CHAR_CLASS;
   private static final int GROUP = ConfigGuiColors.SYNTAX_GROUP;
   private static final int QUANTIFIER = ConfigGuiColors.SYNTAX_QUANTIFIER;
   private static final int ANCHOR = ConfigGuiColors.SYNTAX_ANCHOR;
   private static final int ERROR = ConfigGuiColors.ERROR;

   @Override
   public int[] colors(String source) {
      if (!compilesOk(source)) {
         int[] all = new int[source.length()];
         Arrays.fill(all, ERROR);
         return all;
      } else {
         return classify(source);
      }
   }

   private static boolean compilesOk(String source) {
      try {
         Pattern.compile(source);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static int[] classify(String s) {
      int[] colors = new int[s.length()];
      boolean inClass = false;

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (c == '\\') {
            colors[i] = ESCAPE;
            if (i + 1 < s.length()) {
               colors[++i] = ESCAPE;
            }
         } else if (inClass) {
            colors[i] = CHAR_CLASS;
            if (c == ']') {
               inClass = false;
            }
         } else {
            colors[i] = switch (c) {
               case '$', '.', '^', '|' -> ANCHOR;
               case '(', ')' -> GROUP;
               case '*', '+', '?', '{', '}' -> QUANTIFIER;
               case '[' -> {
                  inClass = true;
                  yield CHAR_CLASS;
               }
               default -> LITERAL;
            };
         }
      }

      return colors;
   }
}
