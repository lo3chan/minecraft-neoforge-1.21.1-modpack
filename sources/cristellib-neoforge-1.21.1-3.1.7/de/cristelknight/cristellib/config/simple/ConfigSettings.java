package de.cristelknight.cristellib.config.simple;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import org.jetbrains.annotations.Nullable;

public interface ConfigSettings<T> {
   String getSubPath();

   Codec<T> getCodec();

   T getDefault();

   @Nullable
   default HashMap<String, String> getComments() {
      return null;
   }

   default String getHeader() {
      return "";
   }

   default boolean isSorted() {
      return false;
   }
}
