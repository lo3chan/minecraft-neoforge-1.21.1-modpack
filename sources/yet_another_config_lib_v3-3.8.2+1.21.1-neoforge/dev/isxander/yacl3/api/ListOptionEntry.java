package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

public interface ListOptionEntry<T> extends Option<T> {
   ListOption<T> parentGroup();

   @NotNull
   @Override
   default ImmutableSet<OptionFlag> flags() {
      return this.parentGroup().flags();
   }

   @Override
   default boolean available() {
      return this.parentGroup().available();
   }
}
