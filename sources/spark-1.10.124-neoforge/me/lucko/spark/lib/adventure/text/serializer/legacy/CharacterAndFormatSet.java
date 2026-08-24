package me.lucko.spark.lib.adventure.text.serializer.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextFormat;

final class CharacterAndFormatSet {
   static final CharacterAndFormatSet DEFAULT = of(CharacterAndFormat.defaults());
   final List<TextFormat> formats;
   final List<TextColor> colors;
   final String characters;

   static CharacterAndFormatSet of(final List<CharacterAndFormat> pairs) {
      int size = pairs.size();
      List<TextColor> colors = new ArrayList<>();
      List<TextFormat> formats = new ArrayList<>(size);
      StringBuilder characters = new StringBuilder(size);

      for (int i = 0; i < size; i++) {
         CharacterAndFormat pair = pairs.get(i);
         char character = pair.character();
         TextFormat format = pair.format();
         boolean formatIsTextColor = format instanceof TextColor;
         characters.append(character);
         formats.add(format);
         if (formatIsTextColor) {
            colors.add((TextColor)format);
         }

         if (pair.caseInsensitive()) {
            boolean added = false;
            if (Character.isUpperCase(character)) {
               characters.append(Character.toLowerCase(character));
               added = true;
            } else if (Character.isLowerCase(character)) {
               characters.append(Character.toUpperCase(character));
               added = true;
            }

            if (added) {
               formats.add(format);
               if (formatIsTextColor) {
                  colors.add((TextColor)format);
               }
            }
         }
      }

      if (formats.size() != characters.length()) {
         throw new IllegalStateException("formats length differs from characters length");
      } else {
         return new CharacterAndFormatSet(Collections.unmodifiableList(formats), Collections.unmodifiableList(colors), characters.toString());
      }
   }

   CharacterAndFormatSet(final List<TextFormat> formats, final List<TextColor> colors, final String characters) {
      this.formats = formats;
      this.colors = colors;
      this.characters = characters;
   }
}
