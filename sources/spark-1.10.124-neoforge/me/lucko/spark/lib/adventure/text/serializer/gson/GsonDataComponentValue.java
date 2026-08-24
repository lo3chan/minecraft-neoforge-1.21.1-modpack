package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.Objects;
import me.lucko.spark.lib.adventure.text.event.DataComponentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface GsonDataComponentValue extends DataComponentValue {
   static GsonDataComponentValue gsonDataComponentValue(@NotNull final JsonElement data) {
      return (GsonDataComponentValue)(data instanceof JsonNull
         ? GsonDataComponentValueImpl.RemovedGsonComponentValueImpl.INSTANCE
         : new GsonDataComponentValueImpl(Objects.requireNonNull(data, "data")));
   }

   @NotNull
   JsonElement element();
}
