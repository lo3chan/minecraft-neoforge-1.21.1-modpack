package dev.latvian.mods.rhino.util;

import java.util.Locale;

public enum DefaultValueTypeHint {
   STRING,
   NUMBER,
   BOOLEAN,
   FUNCTION,
   CLASS;

   public final String name = this.name().toLowerCase(Locale.ROOT);

   @Override
   public String toString() {
      return this.name;
   }
}
