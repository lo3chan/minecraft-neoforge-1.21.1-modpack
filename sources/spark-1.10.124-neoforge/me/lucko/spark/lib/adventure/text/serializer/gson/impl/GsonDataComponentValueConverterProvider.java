package me.lucko.spark.lib.adventure.text.serializer.gson.impl;

import com.google.auto.service.AutoService;
import com.google.gson.JsonNull;
import java.util.Collections;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.event.DataComponentValue;
import me.lucko.spark.lib.adventure.text.event.DataComponentValueConverterRegistry;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonDataComponentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@AutoService({DataComponentValueConverterRegistry.Provider.class})
@Internal
public final class GsonDataComponentValueConverterProvider implements DataComponentValueConverterRegistry.Provider {
   private static final Key ID = Key.key("adventure", "serializer/gson");

   @NotNull
   @Override
   public Key id() {
      return ID;
   }

   @NotNull
   @Override
   public Iterable<DataComponentValueConverterRegistry.Conversion<?, ?>> conversions() {
      return Collections.singletonList(
         DataComponentValueConverterRegistry.Conversion.convert(
            DataComponentValue.Removed.class, GsonDataComponentValue.class, (k, removed) -> GsonDataComponentValue.gsonDataComponentValue(JsonNull.INSTANCE)
         )
      );
   }
}
