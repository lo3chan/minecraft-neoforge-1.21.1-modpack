package me.flashyreese.mods.reeses_sodium_options.client.search;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;

public final class SearchNormalizer {
   private final boolean foldDiacritics;

   public SearchNormalizer(boolean foldDiacritics) {
      this.foldDiacritics = foldDiacritics;
   }

   public String normalize(String text) {
      if (text != null && !text.isEmpty()) {
         String normalized = Normalizer.normalize(text, Form.NFKC).toLowerCase(Locale.ROOT);
         if (this.foldDiacritics) {
            normalized = Normalizer.normalize(normalized, Form.NFD).replaceAll("\\p{M}", "");
         }

         StringBuilder builder = new StringBuilder(normalized.length());
         boolean lastWasSpace = false;
         int length = normalized.length();
         int offset = 0;

         while (offset < length) {
            int codePoint = normalized.codePointAt(offset);
            int type = Character.getType(codePoint);
            boolean isSpace = Character.isWhitespace(codePoint) || type == 12 || type == 13 || type == 14;
            boolean isPunctuation = type == 23 || type == 20 || type == 21 || type == 22 || type == 24 || type == 29 || type == 30;
            if (!isSpace && !isPunctuation) {
               builder.appendCodePoint(codePoint);
               lastWasSpace = false;
            } else if (!lastWasSpace) {
               builder.append(' ');
               lastWasSpace = true;
            }

            offset += Character.charCount(codePoint);
         }

         String result = builder.toString().trim();
         return result.isEmpty() ? "" : result;
      } else {
         return "";
      }
   }
}
