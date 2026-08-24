package me.lucko.spark.lib.adventure.text.serializer;

import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentEncoder<I extends Component, R> {
   @NotNull
   R serialize(@NotNull final I component);

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default R serializeOrNull(@Nullable final I component) {
      return this.serializeOr(component, null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   default R serializeOr(@Nullable final I component, @Nullable final R fallback) {
      return component == null ? fallback : this.serialize(component);
   }
}
