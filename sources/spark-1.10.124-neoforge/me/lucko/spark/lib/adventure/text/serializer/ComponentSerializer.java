package me.lucko.spark.lib.adventure.text.serializer;

import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public interface ComponentSerializer<I extends Component, O extends Component, R> extends ComponentEncoder<I, R>, ComponentDecoder<R, O> {
   @NotNull
   @Override
   O deserialize(@NotNull final R input);

   @Deprecated
   @ScheduledForRemoval(
      inVersion = "5.0.0"
   )
   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default O deseializeOrNull(@Nullable final R input) {
      return ComponentDecoder.super.deserializeOrNull(input);
   }

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   @Override
   default O deserializeOrNull(@Nullable final R input) {
      return ComponentDecoder.super.deserializeOr(input, null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   @Override
   default O deserializeOr(@Nullable final R input, @Nullable final O fallback) {
      return ComponentDecoder.super.deserializeOr(input, fallback);
   }

   @NotNull
   @Override
   R serialize(@NotNull final I component);

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   @Override
   default R serializeOrNull(@Nullable final I component) {
      return this.serializeOr(component, null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   @Override
   default R serializeOr(@Nullable final I component, @Nullable final R fallback) {
      return component == null ? fallback : this.serialize(component);
   }
}
