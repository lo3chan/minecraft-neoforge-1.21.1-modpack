package me.lucko.spark.lib.adventure.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface Option<V> {
   static Option<Boolean> booleanOption(final String id, final boolean defaultValue) {
      return OptionImpl.option(id, Boolean.class, defaultValue);
   }

   static <E extends Enum<E>> Option<E> enumOption(final String id, final Class<E> enumClazz, final E defaultValue) {
      return OptionImpl.option(id, enumClazz, defaultValue);
   }

   @NotNull
   String id();

   @NotNull
   Class<V> type();

   @Nullable
   V defaultValue();
}
