package me.lucko.spark.lib.adventure.text.serializer.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import me.lucko.spark.lib.adventure.internal.Internals;
import me.lucko.spark.lib.adventure.text.format.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CharacterAndFormatImpl implements CharacterAndFormat {
   private final char character;
   private final TextFormat format;
   private final boolean caseInsensitive;

   CharacterAndFormatImpl(final char character, @NotNull final TextFormat format, final boolean caseInsensitive) {
      this.character = character;
      this.format = Objects.requireNonNull(format, "format");
      this.caseInsensitive = caseInsensitive;
   }

   @Override
   public char character() {
      return this.character;
   }

   @NotNull
   @Override
   public TextFormat format() {
      return this.format;
   }

   @Override
   public boolean caseInsensitive() {
      return this.caseInsensitive;
   }

   @Override
   public boolean equals(@Nullable final Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof CharacterAndFormatImpl)) {
         return false;
      } else {
         CharacterAndFormatImpl that = (CharacterAndFormatImpl)other;
         return this.character == that.character && this.format.equals(that.format) && this.caseInsensitive == that.caseInsensitive;
      }
   }

   @Override
   public int hashCode() {
      int result = this.character;
      result = 31 * result + this.format.hashCode();
      return 31 * result + Boolean.hashCode(this.caseInsensitive);
   }

   @NotNull
   @Override
   public String toString() {
      return Internals.toString(this);
   }

   static final class Defaults {
      static final List<CharacterAndFormat> DEFAULTS = createDefaults();

      private Defaults() {
      }

      static List<CharacterAndFormat> createDefaults() {
         List<CharacterAndFormat> formats = new ArrayList<>(22);
         formats.add(CharacterAndFormat.BLACK);
         formats.add(CharacterAndFormat.DARK_BLUE);
         formats.add(CharacterAndFormat.DARK_GREEN);
         formats.add(CharacterAndFormat.DARK_AQUA);
         formats.add(CharacterAndFormat.DARK_RED);
         formats.add(CharacterAndFormat.DARK_PURPLE);
         formats.add(CharacterAndFormat.GOLD);
         formats.add(CharacterAndFormat.GRAY);
         formats.add(CharacterAndFormat.DARK_GRAY);
         formats.add(CharacterAndFormat.BLUE);
         formats.add(CharacterAndFormat.GREEN);
         formats.add(CharacterAndFormat.AQUA);
         formats.add(CharacterAndFormat.RED);
         formats.add(CharacterAndFormat.LIGHT_PURPLE);
         formats.add(CharacterAndFormat.YELLOW);
         formats.add(CharacterAndFormat.WHITE);
         formats.add(CharacterAndFormat.OBFUSCATED);
         formats.add(CharacterAndFormat.BOLD);
         formats.add(CharacterAndFormat.STRIKETHROUGH);
         formats.add(CharacterAndFormat.UNDERLINED);
         formats.add(CharacterAndFormat.ITALIC);
         formats.add(CharacterAndFormat.RESET);
         return Collections.unmodifiableList(formats);
      }
   }
}
