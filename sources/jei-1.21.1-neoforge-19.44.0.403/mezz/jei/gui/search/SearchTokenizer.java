package mezz.jei.gui.search;

import java.util.ArrayList;
import java.util.List;

public class SearchTokenizer {
   public List<Token> tokenize(String filterText) {
      List<Token> tokens = new ArrayList<>();
      if (filterText.isEmpty()) {
         return tokens;
      } else {
         StringBuilder current = new StringBuilder();
         boolean insideQuotes = false;
         boolean exclusion = false;
         boolean escaped = false;

         for (int i = 0; i < filterText.length(); i++) {
            char c = filterText.charAt(i);
            if (escaped) {
               current.append(c);
               escaped = false;
            } else if (c == '\\') {
               escaped = true;
            } else if (c == '"') {
               if (insideQuotes) {
                  this.addToken(tokens, current, exclusion);
                  current.setLength(0);
                  insideQuotes = false;
                  exclusion = false;
               } else {
                  insideQuotes = true;
               }
            } else if (!insideQuotes && Character.isWhitespace(c)) {
               if (!current.isEmpty()) {
                  this.addToken(tokens, current, exclusion);
                  current.setLength(0);
               }

               exclusion = false;
            } else if (!insideQuotes && current.isEmpty() && c == '-') {
               exclusion = true;
            } else {
               current.append(c);
            }
         }

         if (!current.isEmpty() || insideQuotes) {
            this.addToken(tokens, current, exclusion);
         }

         return tokens;
      }
   }

   private void addToken(List<Token> tokens, StringBuilder content, boolean exclusion) {
      String text = content.toString().trim();
      if (!text.isEmpty()) {
         tokens.add(new Token(text, exclusion));
      }
   }
}
