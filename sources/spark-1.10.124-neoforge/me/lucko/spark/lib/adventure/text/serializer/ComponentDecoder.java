package me.lucko.spark.lib.adventure.text.serializer;

import me.lucko.spark.lib.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ComponentDecoder<S, O extends Component> {
   @NotNull
   O deserialize(@NotNull final S input);

   @Contract(
      value = "!null -> !null; null -> null",
      pure = true
   )
   @Nullable
   default O deserializeOrNull(@Nullable final S input) {
      return this.deserializeOr(input, null);
   }

   @Contract(
      value = "!null, _ -> !null; null, _ -> param2",
      pure = true
   )
   @Nullable
   default O deserializeOr(@Nullable final S input, @Nullable final O fallback) {
      return input == null ? fallback : this.deserialize(input);
   }
}
